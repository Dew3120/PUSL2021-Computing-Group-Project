package com.group100.wms.ui.inventory;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.model.Item;
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

public class InventoryListController {

    @FXML private Label lblTotalItems, lblCategories, lblWh1, lblWh2;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtSearch;
    @FXML private TableView<Item> itemTable;
    @FXML private TableColumn<Item, Number> colId;
    @FXML private TableColumn<Item, String> colSku, colName, colCategory, colColour, colUnit, colWarehouse;

    private ObservableList<Item> allItems;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getId()));
        colSku.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSku()));
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getName()));
        colCategory.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCategory()));
        colColour.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getColour()));
        colUnit.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUnit()));
        colWarehouse.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getWarehouseId() == 1 ? "Main Warehouse" : "Secondary Warehouse"));
        loadData();
    }

    private void loadData() {
        allItems = FXCollections.observableArrayList();
        String sql = "SELECT * FROM items ORDER BY category, name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("item_id"));
                item.setSku(rs.getString("sku"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setCategory(rs.getString("category"));
                item.setColour(rs.getString("colour"));
                item.setUnit(rs.getString("unit"));
                item.setWarehouseId(rs.getInt("warehouse_id"));
                allItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        itemTable.setItems(allItems);

        List<String> categories = allItems.stream()
                .map(Item::getCategory).distinct().sorted().collect(Collectors.toList());
        categories.add(0, "All Categories");
        cmbCategory.setItems(FXCollections.observableArrayList(categories));
        cmbCategory.setValue("All Categories");

        lblTotalItems.setText(String.valueOf(allItems.size()));
        lblCategories.setText(String.valueOf(categories.size() - 1));
        lblWh1.setText(String.valueOf(allItems.stream().filter(i -> i.getWarehouseId() == 1).count()));
        lblWh2.setText(String.valueOf(allItems.stream().filter(i -> i.getWarehouseId() == 2).count()));
    }

    @FXML
    private void handleFilter() {
        String cat = cmbCategory.getValue();
        String search = txtSearch.getText() != null ? txtSearch.getText().toLowerCase().trim() : "";
        List<Item> filtered = allItems.stream()
                .filter(i -> "All Categories".equals(cat) || i.getCategory().equals(cat))
                .filter(i -> search.isEmpty() || i.getName().toLowerCase().contains(search)
                        || i.getSku().toLowerCase().contains(search))
                .collect(Collectors.toList());
        itemTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleReset() {
        cmbCategory.setValue("All Categories");
        txtSearch.clear();
        itemTable.setItems(allItems);
    }

    @FXML
    private void handleExportPdf() {
        String[] headers = {"ID", "SKU", "Name", "Category", "Colour", "Unit", "Warehouse"};
        List<String[]> data = new ArrayList<>();
        for (Item i : itemTable.getItems()) {
            data.add(new String[]{String.valueOf(i.getId()), i.getSku(), i.getName(),
                    i.getCategory(), i.getColour(), i.getUnit(),
                    i.getWarehouseId() == 1 ? "Main" : "Secondary"});
        }
        PdfExporter.export("Inventory Report", headers, data, itemTable.getScene().getWindow());
    }

    @FXML
    private void handleExportExcel() {
        String[] headers = {"ID", "SKU", "Name", "Category", "Colour", "Unit", "Warehouse"};
        List<String[]> data = new ArrayList<>();
        for (Item i : itemTable.getItems()) {
            data.add(new String[]{String.valueOf(i.getId()), i.getSku(), i.getName(),
                    i.getCategory(), i.getColour(), i.getUnit(),
                    i.getWarehouseId() == 1 ? "Main" : "Secondary"});
        }
        ExcelExporter.export("Inventory", headers, data, itemTable.getScene().getWindow());
    }
}