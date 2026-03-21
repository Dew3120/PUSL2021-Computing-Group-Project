package com.group100.wms.ui.outbound;

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

public class GinListController {

    @FXML private Label lblTotal, lblCompleted, lblTransit, lblCancelled;
    @FXML private ComboBox<String> cmbStatus, cmbDest;
    @FXML private TableView<GinRow> ginTable;
    @FXML private TableColumn<GinRow, Number> colGinId;
    @FXML private TableColumn<GinRow, String> colWarehouse, colDest, colDestType, colDate, colStatus;

    private ObservableList<GinRow> allRows;

    @FXML
    public void initialize() {
        colGinId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().ginId));
        colWarehouse.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().warehouseName));
        colDest.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().destination));
        colDestType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().destType));
        colDate.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().issuedDate));
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().status));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "COMPLETED" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "IN_TRANSIT" -> setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                    case "CANCELLED" -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    default -> setStyle("");
                }
            }
        });

        loadData();
    }

    private void loadData() {
        allRows = FXCollections.observableArrayList();
        String sql = "SELECT g.*, w.name AS warehouse_name FROM goods_issue_notes g " +
                "JOIN warehouses w ON g.warehouse_id = w.warehouse_id ORDER BY g.issued_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                GinRow r = new GinRow();
                r.ginId = rs.getInt("gin_id");
                r.warehouseName = rs.getString("warehouse_name");
                r.destination = rs.getString("destination");
                r.destType = rs.getString("destination_type");
                r.issuedDate = rs.getString("issued_date");
                r.status = rs.getString("status");
                allRows.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        ginTable.setItems(allRows);

        lblTotal.setText(String.valueOf(allRows.size()));
        lblCompleted.setText(String.valueOf(allRows.stream().filter(r -> "COMPLETED".equals(r.status)).count()));
        lblTransit.setText(String.valueOf(allRows.stream().filter(r -> "IN_TRANSIT".equals(r.status)).count()));
        lblCancelled.setText(String.valueOf(allRows.stream().filter(r -> "CANCELLED".equals(r.status)).count()));

        List<String> statuses = allRows.stream().map(r -> r.status).distinct().sorted().collect(Collectors.toList());
        statuses.add(0, "All"); cmbStatus.setItems(FXCollections.observableArrayList(statuses)); cmbStatus.setValue("All");
        List<String> dests = allRows.stream().map(r -> r.destination).distinct().sorted().collect(Collectors.toList());
        dests.add(0, "All"); cmbDest.setItems(FXCollections.observableArrayList(dests)); cmbDest.setValue("All");
    }

    @FXML private void handleFilter() {
        String st = cmbStatus.getValue(); String dest = cmbDest.getValue();
        ginTable.setItems(FXCollections.observableArrayList(allRows.stream()
                .filter(r -> "All".equals(st) || r.status.equals(st))
                .filter(r -> "All".equals(dest) || r.destination.equals(dest))
                .collect(Collectors.toList())));
    }
    @FXML private void handleReset() { cmbStatus.setValue("All"); cmbDest.setValue("All"); ginTable.setItems(allRows); }

    @FXML private void handleExportPdf() {
        String[] h = {"GIN #", "Warehouse", "Destination", "Type", "Date", "Status"};
        List<String[]> d = new ArrayList<>();
        for (GinRow r : ginTable.getItems()) d.add(new String[]{String.valueOf(r.ginId), r.warehouseName, r.destination, r.destType, r.issuedDate, r.status});
        PdfExporter.export("GIN Report", h, d, ginTable.getScene().getWindow());
    }
    @FXML private void handleExportExcel() {
        String[] h = {"GIN #", "Warehouse", "Destination", "Type", "Date", "Status"};
        List<String[]> d = new ArrayList<>();
        for (GinRow r : ginTable.getItems()) d.add(new String[]{String.valueOf(r.ginId), r.warehouseName, r.destination, r.destType, r.issuedDate, r.status});
        ExcelExporter.export("GIN Report", h, d, ginTable.getScene().getWindow());
    }

    public static class GinRow { public int ginId; public String warehouseName, destination, destType, issuedDate, status; }
}