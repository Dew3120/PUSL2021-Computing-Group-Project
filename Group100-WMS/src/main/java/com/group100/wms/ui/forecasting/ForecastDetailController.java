package com.group100.wms.ui.forecasting;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Forecast;
import com.group100.wms.repository.ForecastRepository;
import com.group100.wms.service.ForecastService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ForecastDetailController {

    @FXML private TextField itemIdField;
    @FXML private TextField warehouseIdField;
    @FXML private TableView<Forecast> detailTable;
    @FXML private TableColumn<Forecast, String> colDate;
    @FXML private TableColumn<Forecast, Integer> colQty;
    @FXML private TableColumn<Forecast, Double> colLower;
    @FXML private TableColumn<Forecast, Double> colUpper;
    @FXML private Label statusLabel;

    private final ForecastService forecastService =
            new ForecastService(new ForecastRepository());

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