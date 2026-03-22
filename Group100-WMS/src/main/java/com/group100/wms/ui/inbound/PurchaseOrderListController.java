package com.group100.wms.ui.inbound;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.util.PdfExporter;
import com.group100.wms.util.ExcelExporter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for displaying and managing Purchase Orders list in the UI.
 *
 * OOP Concepts Used:
 * - Encapsulation: Data (PoRow) and UI components are managed within this class.
 * - Abstraction: Hides database operations and UI logic behind methods.
 * - Polymorphism: Method overriding in TableCell (updateItem).
 * - No Inheritance explicitly defined (except implicit JavaFX controller behavior).
 */
public class PurchaseOrderListController {

    // Labels used to display KPI values (total, received, pending, cancelled)
    @FXML private Label lblTotalPo, lblReceived, lblPending, lblCancelled;

    // ComboBoxes used for filtering by status and supplier
    @FXML private ComboBox<String> cmbStatus, cmbSupplier;

    // TableView to display purchase order data
    @FXML private TableView<PoRow> poTable;

    // Table columns for displaying PO details
    @FXML private TableColumn<PoRow, Number> colPoId;
    @FXML private TableColumn<PoRow, String> colSupplier, colWarehouse, colOrderDate, colExpDate, colStatus;

    // Stores all purchase order rows fetched from the database
    private ObservableList<PoRow> allRows;

    // Initializes the controller, sets up table columns, styling, and loads data
    @FXML
    public void initialize() {
        colPoId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().poId));
        colSupplier.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().supplierName));
        colWarehouse.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().warehouseName));
        colOrderDate.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().orderDate));
        colExpDate.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().expectedDate));
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().status));
        // Color-code status
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            // Overrides the default method to customize how status cells are displayed
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty  item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "RECEIVED" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "PENDING" -> setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    case "PARTIALLY_RECEIVED" -> setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                    case "CANCELLED" -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    default -> setStyle("");
                }
            }
        });

        loadData();
    }

    // Loads purchase order data from the database and initializes table, KPIs, and filters
    private void loadData() {
        allRows = FXCollections.observableArrayList();
        String sql = "SELECT po.*, s.name AS supplier_name, w.name AS warehouse_name " +
                "FROM purchase_orders po " +
                "JOIN suppliers s ON po.supplier_id = s.supplier_id " +
                "JOIN warehouses w ON po.warehouse_id = w.warehouse_id " +
                "ORDER BY po.order_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PoRow r = new PoRow();
                r.poId = rs.getInt("po_id");
                r.supplierName = rs.getString("supplier_name");
                r.warehouseName = rs.getString("warehouse_name");
                r.orderDate = rs.getString("order_date");
                r.expectedDate = rs.getString("expected_date");
                r.status = rs.getString("status");
                allRows.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        poTable.setItems(allRows);

        // KPIs
        lblTotalPo.setText(String.valueOf(allRows.size()));
        lblReceived.setText(String.valueOf(allRows.stream().filter(r -> "RECEIVED".equals(r.status)).count()));
        lblPending.setText(String.valueOf(allRows.stream().filter(r -> "PENDING".equals(r.status)).count()));
        lblCancelled.setText(String.valueOf(allRows.stream().filter(r -> "CANCELLED".equals(r.status)).count()));

        // Filters
        List<String> statuses = allRows.stream().map(r -> r.status).distinct().sorted().collect(Collectors.toList());
        statuses.add(0, "All");
        cmbStatus.setItems(FXCollections.observableArrayList(statuses));
        cmbStatus.setValue("All");

        List<String> suppliers = allRows.stream().map(r -> r.supplierName).distinct().sorted().collect(Collectors.toList());
        suppliers.add(0, "All");
        cmbSupplier.setItems(FXCollections.observableArrayList(suppliers));
        cmbSupplier.setValue("All");
    }

    // Applies filtering to the table based on selected status and supplier
    @FXML
    private void handleFilter() {
        String st = cmbStatus.getValue();
        String sup = cmbSupplier.getValue();
        List<PoRow> filtered = allRows.stream()
                .filter(r -> "All".equals(st)  r.status.equals(st))
                .filter(r -> "All".equals(sup) || r.supplierName.equals(sup))
                .collect(Collectors.toList());
        poTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // Resets filters to default ("All") and reloads full dataset into the table
    @FXML private void handleReset() { cmbStatus.setValue("All"); cmbSupplier.setValue("All"); poTable.setItems(allRows); }
    // Exports the currently displayed table data to a PDF file
    @FXML
    private void handleExportPdf() {
        String[] h = {"PO #", "Supplier", "Warehouse", "Order Date", "Expected", "Status"};
        List<String[]> d = new ArrayList<>();
        for (PoRow r : poTable.getItems()) d.add(new String[]{String.valueOf(r.poId), r.supplierName, r.warehouseName, r.orderDate, r.expectedDate, r.status});
        PdfExporter.export("Purchase Orders Report", h, d, poTable.getScene().getWindow());
    }

    // Exports the currently displayed table data to an Excel file
    @FXML
    private void handleExportExcel() {
        String[] h = {"PO #", "Supplier", "Warehouse", "Order Date", "Expected", "Status"};
        List<String[]> d = new ArrayList<>();
        for (PoRow r : poTable.getItems()) d.add(new String[]{String.valueOf(r.poId), r.supplierName, r.warehouseName, r.orderDate, r.expectedDate, r.status});
        ExcelExporter.export("Purchase Orders", h, d, poTable.getScene().getWindow());
    }

    // Data model class representing a single Purchase Order row
    public static class PoRow {

        // Stores purchase order ID
        public int poId;

        // Stores supplier name
        public String supplierName;

        // Stores warehouse name
        public String warehouseName;

        // Stores order date
        public String orderDate;

        // Stores expected delivery date
        public String expectedDate;

        // Stores current status of the purchase order
        public String status;
    }
}
