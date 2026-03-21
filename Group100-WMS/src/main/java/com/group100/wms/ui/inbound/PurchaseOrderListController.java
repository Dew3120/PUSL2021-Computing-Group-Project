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

public class PurchaseOrderListController {

    @FXML private Label lblTotalPo, lblReceived, lblPending, lblCancelled;
    @FXML private ComboBox<String> cmbStatus, cmbSupplier;
    @FXML private TableView<PoRow> poTable;
    @FXML private TableColumn<PoRow, Number> colPoId;
    @FXML private TableColumn<PoRow, String> colSupplier, colWarehouse, colOrderDate, colExpDate, colStatus;

    private ObservableList<PoRow> allRows;

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
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
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

    @FXML
    private void handleFilter() {
        String st = cmbStatus.getValue();
        String sup = cmbSupplier.getValue();
        List<PoRow> filtered = allRows.stream()
                .filter(r -> "All".equals(st) || r.status.equals(st))
                .filter(r -> "All".equals(sup) || r.supplierName.equals(sup))
                .collect(Collectors.toList());
        poTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML private void handleReset() { cmbStatus.setValue("All"); cmbSupplier.setValue("All"); poTable.setItems(allRows); }

    @FXML
    private void handleExportPdf() {
        String[] h = {"PO #", "Supplier", "Warehouse", "Order Date", "Expected", "Status"};
        List<String[]> d = new ArrayList<>();
        for (PoRow r : poTable.getItems()) d.add(new String[]{String.valueOf(r.poId), r.supplierName, r.warehouseName, r.orderDate, r.expectedDate, r.status});
        PdfExporter.export("Purchase Orders Report", h, d, poTable.getScene().getWindow());
    }

    @FXML
    private void handleExportExcel() {
        String[] h = {"PO #", "Supplier", "Warehouse", "Order Date", "Expected", "Status"};
        List<String[]> d = new ArrayList<>();
        for (PoRow r : poTable.getItems()) d.add(new String[]{String.valueOf(r.poId), r.supplierName, r.warehouseName, r.orderDate, r.expectedDate, r.status});
        ExcelExporter.export("Purchase Orders", h, d, poTable.getScene().getWindow());
    }

    public static class PoRow {
        public int poId; public String supplierName, warehouseName, orderDate, expectedDate, status;
    }
}