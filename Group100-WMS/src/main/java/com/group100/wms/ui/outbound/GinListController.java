```java id="q2v9ld"
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

// OOP Concepts Used:
// Encapsulation - UI components and data handling are encapsulated within the controller.
// Abstraction - Controller abstracts database queries and table handling from the user.
// Inheritance - JavaFX UI components inherit from base classes.
// Polymorphism - Methods like cell factories and streams allow flexible behavior across multiple data types.

public class GinListController {

    // Labels displaying summary statistics for GINs (total, completed, in transit, cancelled)
    @FXML private Label lblTotal, lblCompleted, lblTransit, lblCancelled;

    // ComboBoxes for filtering GINs by status and destination
    @FXML private ComboBox<String> cmbStatus, cmbDest;

    // TableView displaying GIN records
    @FXML private TableView<GinRow> ginTable;

    // Table columns for GIN properties
    @FXML private TableColumn<GinRow, Number> colGinId;
    @FXML private TableColumn<GinRow, String> colWarehouse, colDest, colDestType, colDate, colStatus;

    // Observable list storing all GIN rows loaded from the database
    private ObservableList<GinRow> allRows;

    // Initializes table columns, sets up status column formatting, and loads GIN data
    @FXML
    public void initialize() {
        colGinId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().ginId));
        colWarehouse.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().warehouseName));
        colDest.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().destination));
        colDestType.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().destType));
        colDate.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().issuedDate));
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().status));

        // Customize cell appearance based on status (color coding)
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

    // Loads GIN records from the database, populates the table, summary labels, and filter ComboBoxes
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

    // Filters the displayed GINs based on selected status and destination
    @FXML private void handleFilter() {
        String st = cmbStatus.getValue(); String dest = cmbDest.getValue();
        ginTable.setItems(FXCollections.observableArrayList(allRows.stream()
                .filter(r -> "All".equals(st) || r.status.equals(st))
                .filter(r -> "All".equals(dest) || r.destination.equals(dest))
                .collect(Collectors.toList())));
    }

    // Resets filters and displays all GIN records
    @FXML private void handleReset() { cmbStatus.setValue("All"); cmbDest.setValue("All"); ginTable.setItems(allRows); }

    // Exports the currently displayed GIN table data to a PDF report
    @FXML private void handleExportPdf() {
        String[] h = {"GIN #", "Warehouse", "Destination", "Type", "Date", "Status"};
        List<String[]> d = new ArrayList<>();
        for (GinRow r : ginTable.getItems()) d.add(new String[]{String.valueOf(r.ginId), r.warehouseName, r.destination, r.destType, r.issuedDate, r.status});
        PdfExporter.export("GIN Report", h, d, ginTable.getScene().getWindow());
    }

    // Exports the currently displayed GIN table data to an Excel report
    @FXML private void handleExportExcel() {
        String[] h = {"GIN #", "Warehouse", "Destination", "Type", "Date", "Status"};
        List<String[]> d = new ArrayList<>();
        for (GinRow r : ginTable.getItems()) d.add(new String[]{String.valueOf(r.ginId), r.warehouseName, r.destination, r.destType, r.issuedDate, r.status});
        ExcelExporter.export("GIN Report", h, d, ginTable.getScene().getWindow());
    }

    // Inner class representing a row in the GIN TableView
    public static class GinRow {
        public int ginId;
        public String warehouseName, destination, destType, issuedDate, status;
    }
}
```
