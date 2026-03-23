package com.group100.wms.ui.forecasting;

import java.util.List;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Forecast;
import com.group100.wms.repository.ForecastRepository;
import com.group100.wms.service.ForecastService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

// OOP Concepts: Encapsulation (private fields and methods), Abstraction (complex service logic hidden),
// Inheritance (extends from JavaFX controller pattern), Dependency Injection (service injected)
public class ForecastDetailController {

    // Input field for specifying which item to view forecasts for
    @FXML private TextField itemIdField;
    // Input field for specifying warehouse ID for forecast generation
    @FXML private TextField warehouseIdField;
    // Table displaying forecast details for selected item
    @FXML private TableView<Forecast> detailTable;
    // Column showing forecast date in the detail table
    @FXML private TableColumn<Forecast, String> colDate;
    // Column showing forecasted quantity
    @FXML private TableColumn<Forecast, Integer> colQty;
    // Column showing lower confidence bound
    @FXML private TableColumn<Forecast, Double> colLower;
    // Column showing upper confidence bound
    @FXML private TableColumn<Forecast, Double> colUpper;
    // Label displaying operation status messages and error information
    @FXML private Label statusLabel;

    // Service layer for forecast operations with dependency injection
    private final ForecastService forecastService =
            new ForecastService(new ForecastRepository());

    // Initializes table column bindings for displaying forecast data in UI
    @FXML
    public void initialize() {
        colDate.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getForecastDate() != null
                                ? d.getValue().getForecastDate().toString() : ""));
        colQty.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(
                        d.getValue().getForecastedQuantity()).asObject());
        colLower.setCellValueFactory(d ->
                new javafx.beans.property.SimpleDoubleProperty(
                        d.getValue().getConfidenceLower()).asObject());
        colUpper.setCellValueFactory(d ->
                new javafx.beans.property.SimpleDoubleProperty(
                        d.getValue().getConfidenceUpper()).asObject());
    }

    // Loads and displays all forecast records for the specified item ID
    @FXML
    private void handleLoad() {
        String itemText = itemIdField.getText().trim();
        if (itemText.isBlank()) {
            statusLabel.setText("Enter an item ID.");
            return;
        }
        try {
            int itemId = Integer.parseInt(itemText);
            List<Forecast> forecasts = forecastService.getForecastsByItem(itemId);
            detailTable.setItems(FXCollections.observableArrayList(forecasts));
            statusLabel.setText("Loaded " + forecasts.size() + " forecast entries.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid item ID.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Triggers a new forecast for the specified item and warehouse by executing Python AI module
    @FXML
    private void handleRunForecast() {
        String itemText = itemIdField.getText().trim();
        String whText = warehouseIdField.getText().trim();
        if (itemText.isBlank() || whText.isBlank()) {
            statusLabel.setText("Enter item ID and warehouse ID.");
            return;
        }
        try {
            int itemId = Integer.parseInt(itemText);
            int warehouseId = Integer.parseInt(whText);
            forecastService.runForecast(itemId, warehouseId);
            statusLabel.setText("Forecast triggered. Reload to see results.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid input.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}