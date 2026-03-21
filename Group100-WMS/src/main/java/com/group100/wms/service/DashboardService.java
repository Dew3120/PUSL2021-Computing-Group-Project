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

public class DashboardService {

    private final ItemRepository itemRepository;
    private final BatchRepository batchRepository;
    private final GrnRepository grnRepository;
    private final GinRepository ginRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollRepository payrollRepository;

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

    public Map<String, Object> getWarehouseManagerKpis(int warehouseId)
            throws DatabaseException {
        Map<String, Object> kpis = new HashMap<>();
        List<Item> items = itemRepository.findByWarehouseId(warehouseId);
        int totalItems = items.size();
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

    public Map<String, Object> getAccountantKpis(int month, int year)
            throws DatabaseException {
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("payrollCount",
                payrollRepository.findByMonthYear(month, year).size());
        kpis.put("attendanceCount",
                attendanceRepository.findByMonthYear(month, year).size());
        return kpis;
    }

    public Map<String, Object> getAdminKpis() throws DatabaseException {
        Map<String, Object> kpis = new HashMap<>();
        kpis.put("totalItems", itemRepository.findAll().size());
        kpis.put("totalGrns", grnRepository.findAll().size());
        kpis.put("totalGins", ginRepository.findAll().size());
        LocalDate now = LocalDate.now();
        kpis.put("payrollThisMonth",
                payrollRepository.findByMonthYear(
                        now.getMonthValue(), now.getYear()).size());
        return kpis;
    }
}