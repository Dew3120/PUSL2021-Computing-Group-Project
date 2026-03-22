package com.group100.wms.service;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Item;
import com.group100.wms.repository.AttendanceRepository;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.GinRepository;
import com.group100.wms.repository.GrnRepository;
import com.group100.wms.repository.ItemRepository;
import com.group100.wms.repository.PayrollRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// OOP Concepts used in this class:
// 1. Encapsulation: The service hides the internal repository dependencies and the complexity of data aggregation.
// 2. Abstraction: It provides simplified methods to fetch Key Performance Indicators (KPIs) for different user roles without exposing SQL or specific repository logic.
public class DashboardService {

    // Stores the repository for item and inventory data operations
    private final ItemRepository itemRepository;
    // Stores the repository for managing batches of goods
    private final BatchRepository batchRepository;
    // Stores the repository for Goods Received Note (GRN) records
    private final GrnRepository grnRepository;
    // Stores the repository for Goods Issue Note (GIN) records
    private final GinRepository ginRepository;
    // Stores the repository for employee attendance data
    private final AttendanceRepository attendanceRepository;
    // Stores the repository for payroll and financial records
    private final PayrollRepository payrollRepository;

    // Constructor to inject all required data access dependencies into the dashboard service
    public DashboardService(ItemRepository itemRepository,
                            BatchRepository batchRepository,
                            GrnRepository grnRepository,
                            GinRepository ginRepository,
                            AttendanceRepository attendanceRepository,
                            PayrollRepository payrollRepository) {
        this.itemRepository = itemRepository;
        this.batchRepository = batchRepository;
        this.grnRepository = grnRepository;
        this.ginRepository = ginRepository;
        this.attendanceRepository = attendanceRepository;
        this.payrollRepository = payrollRepository;
    }

    // Aggregates data relevant to Warehouse Managers, such as inventory counts and low stock alerts
    public Map<String, Object> getWarehouseManagerKpis(int warehouseId)
            throws DatabaseException {
        // Stores the map of KPI keys and their corresponding calculated values
        Map<String, Object> kpis = new HashMap<>();
        // Stores the list of items associated with a specific warehouse
        List<Item> items = itemRepository.findByWarehouseId(warehouseId);
        // Stores the total number of unique item types in the warehouse
        int totalItems = items.size();
        // Stores the count of items where stock level is below the threshold of 20 units
        long lowStockCount = items.stream().filter(item -> {
            try {
                return itemRepository.getStockLevel(item.getId()) <= 20;
            } catch (DatabaseException e) { return false; }
        }).count();

        kpis.put("totalItems", totalItems);
        kpis.put("lowStockCount", lowStockCount);
        kpis.put("totalGrns", grnRepository.findAll().size());
        kpis.put("totalGins", ginRepository.findAll().size());
        return kpis;
    }

    // Aggregates financial and attendance data for the Accountant's dashboard view
    public Map<String, Object> getAccountantKpis(int month, int year)
            throws DatabaseException {
        // Stores the map of financial and attendance metrics for a specific period
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("payrollCount",
                payrollRepository.findByMonthYear(month, year).size());
        kpis.put("attendanceCount",
                attendanceRepository.findByMonthYear(month, year).size());
        return kpis;
    }

    // Aggregates high-level system-wide data for the System Administrator dashboard
    public Map<String, Object> getAdminKpis() throws DatabaseException {
        // Stores the map of system-wide administrative metrics
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("totalItems", itemRepository.findAll().size());
        kpis.put("totalGrns", grnRepository.findAll().size());
        kpis.put("totalGins", ginRepository.findAll().size());
        // Stores the current system date to filter data for the current month
        LocalDate now = LocalDate.now();
        kpis.put("payrollThisMonth",
                payrollRepository.findByMonthYear(
                        now.getMonthValue(), now.getYear()).size());
        return kpis;
    }
}
