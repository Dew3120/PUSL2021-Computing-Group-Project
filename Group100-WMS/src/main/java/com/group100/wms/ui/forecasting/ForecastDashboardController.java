package com.group100.wms.ui.forecasting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.model.Forecast;
import com.group100.wms.model.ForecastHistory;
import com.group100.wms.repository.ForecastHistoryRepository;
import com.group100.wms.util.ExcelExporter;
import com.group100.wms.util.PdfExporter;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

// OOP Concepts: Encapsulation (private fields and methods), Abstraction (complex UI logic hidden from user),
// Inheritance (extends from JavaFX controller pattern), Polymorphism (different chart and table rendering)
public class ForecastDashboardController {

    // Label displaying average accuracy percentage for all forecasts
    @FXML private Label lblAvgAccuracy, lblHitCount, lblFairCount, lblMissCount, lblTotalForecasts;

    // Table and columns for displaying current forecasts
    @FXML private TableView<Forecast> currentTable;
    @FXML private TableColumn<Forecast, String> colCurItem, colCurWarehouse, colCurMethod;
    @FXML private TableColumn<Forecast, Number> colCurPredicted, colCurConfidence;
    @FXML private TableColumn<Forecast, String> colCurDate;

    // Table and columns for displaying forecast history with filtering options
    @FXML private TableView<ForecastHistory> historyTable;
    @FXML private TableColumn<ForecastHistory, String> colHistItem, colHistResult;
    @FXML private TableColumn<ForecastHistory, Number> colHistMonth, colHistYear, colHistPredicted, colHistActual, colHistAccuracy, colHistConfidence;
    @FXML private ComboBox<String> cmbResultFilter, cmbMonthFilter, cmbYearFilter;

    // Line chart for displaying forecast trends over time with axis and combo boxes
    @FXML private LineChart<String, Number> trendChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private ComboBox<String> cmbTrendYear, cmbTrendMonth;
    @FXML private Label lblChartTitle;
    @FXML private TableView<TrendRow> trendTable;
    @FXML private TableColumn<TrendRow, String> colTrendPeriod;
    @FXML private TableColumn<TrendRow, Number> colTrendTotal, colTrendHits, colTrendFairs, colTrendMisses, colTrendAvgAcc;

    // Repository for accessing forecast history data from database
    private final ForecastHistoryRepository histRepo = new ForecastHistoryRepository();
    // Observable list holding all forecast history records for filtering
    private ObservableList<ForecastHistory> allHistory;

    // Initializes all UI components and loads initial data into dashboard tabs
    @FXML
    public void initialize() {
        setupCurrentTab();
        setupHistoryTab();
        setupTrendTab();
        loadKPIs();
        loadCurrentForecasts();
        loadHistory();
        loadTrendChart("2025", "Full Year");
        loadTrendTable();
    }

    // Loads and displays key performance indicators (KPIs) for forecast accuracy metrics
    private void loadKPIs() {
        int hits = histRepo.countByResult("HIT");
        int fairs = histRepo.countByResult("FAIR");
        int misses = histRepo.countByResult("MISS");
        double avgAcc = histRepo.averageAccuracy();
        int total = hits + fairs + misses;
        lblAvgAccuracy.setText(String.format("%.1f%%", avgAcc));
        lblHitCount.setText(String.valueOf(hits));
        lblFairCount.setText(String.valueOf(fairs));
        lblMissCount.setText(String.valueOf(misses));
        lblTotalForecasts.setText(String.valueOf(total));
    }

    // Sets up table column bindings for the current forecasts tab
    // ========== Tab 1: Current ==========
    private void setupCurrentTab() {
        colCurItem.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getItemName()));
        colCurWarehouse.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getWarehouseName()));
        colCurPredicted.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getPredictedQty()));
        colCurConfidence.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getConfidence()));
        colCurMethod.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMethod()));
        colCurDate.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue().getGeneratedDate() != null ? cd.getValue().getGeneratedDate().toString() : ""));
    }

    // Fetches current forecast records from database and populates the current forecasts table
    private void loadCurrentForecasts() {
        ObservableList<Forecast> list = FXCollections.observableArrayList();
        String sql = "SELECT f.*, i.name AS item_name, w.name AS warehouse_name " +
                "FROM forecasts f JOIN items i ON f.item_id = i.item_id " +
                "JOIN warehouses w ON f.warehouse_id = w.warehouse_id ORDER BY f.generated_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Forecast f = new Forecast();
                f.setForecastId(rs.getInt("forecast_id"));
                f.setItemId(rs.getInt("item_id"));
                f.setWarehouseId(rs.getInt("warehouse_id"));
                f.setPredictedQty(rs.getDouble("predicted_qty"));
                f.setConfidence(rs.getDouble("confidence"));
                java.sql.Date d = rs.getDate("generated_date");
                if (d != null) f.setGeneratedDate(d.toLocalDate());
                f.setMethod(rs.getString("method"));
                f.setItemName(rs.getString("item_name"));
                f.setWarehouseName(rs.getString("warehouse_name"));
                list.add(f);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        currentTable.setItems(list);
    }

    // Sets up table column bindings and color formatting for the history tab with result-based styling
    // ========== Tab 2: History ==========
    private void setupHistoryTab() {
        colHistItem.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getItemName()));
        colHistMonth.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getForecastMonth()));
        colHistYear.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getForecastYear()));
        colHistPredicted.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getPredictedQty()));
        colHistActual.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getActualQty()));
        colHistAccuracy.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getAccuracy()));
        colHistResult.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getResult()));
        colHistConfidence.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().getConfidence()));

        colHistResult.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "HIT" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "FAIR" -> setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    case "MISS" -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    default -> setStyle("");
                }
            }
        });

        cmbResultFilter.setItems(FXCollections.observableArrayList("All", "HIT", "FAIR", "MISS"));
        cmbResultFilter.setValue("All");
        cmbMonthFilter.setItems(FXCollections.observableArrayList(
                "All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
        cmbMonthFilter.setValue("All");
        cmbYearFilter.setItems(FXCollections.observableArrayList("All", "2025", "2026"));
        cmbYearFilter.setValue("All");
    }

    // Loads all forecast history records from repository and displays in history table
    private void loadHistory() {
        allHistory = FXCollections.observableArrayList(histRepo.findAll());
        historyTable.setItems(allHistory);
    }

    // Filters history table by selected result, month, and year criteria
    @FXML
    private void handleHistoryFilter() {
        String result = cmbResultFilter.getValue();
        String monthStr = cmbMonthFilter.getValue();
        String yearStr = cmbYearFilter.getValue();
        List<ForecastHistory> filtered = allHistory.stream()
                .filter(fh -> "All".equals(result) || fh.getResult().equals(result))
                .filter(fh -> "All".equals(monthStr) || fh.getForecastMonth() == Integer.parseInt(monthStr))
                .filter(fh -> "All".equals(yearStr) || fh.getForecastYear() == Integer.parseInt(yearStr))
                .collect(Collectors.toList());
        historyTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // Resets all filters and displays complete history data
    @FXML
    private void handleHistoryReset() {
        cmbResultFilter.setValue("All");
        cmbMonthFilter.setValue("All");
        cmbYearFilter.setValue("All");
        historyTable.setItems(allHistory);
    }

    // Exports current history table data to PDF file
    @FXML
    private void handleExportPdf() {
        String[] headers = {"Item", "Month", "Year", "Predicted", "Actual", "Accuracy %", "Result"};
        List<String[]> data = new ArrayList<>();
        for (ForecastHistory fh : historyTable.getItems()) {
            data.add(new String[]{fh.getItemName(), String.valueOf(fh.getForecastMonth()),
                    String.valueOf(fh.getForecastYear()), String.format("%.0f", fh.getPredictedQty()),
                    String.format("%.0f", fh.getActualQty()), String.format("%.1f", fh.getAccuracy()),
                    fh.getResult()});
        }
        PdfExporter.export("Forecast History Report", headers, data, historyTable.getScene().getWindow());
    }

    // Exports current history table data to Excel file with additional confidence column
    @FXML
    private void handleExportExcel() {
        String[] headers = {"Item", "Month", "Year", "Predicted", "Actual", "Accuracy %", "Result", "Confidence"};
        List<String[]> data = new ArrayList<>();
        for (ForecastHistory fh : historyTable.getItems()) {
            data.add(new String[]{fh.getItemName(), String.valueOf(fh.getForecastMonth()),
                    String.valueOf(fh.getForecastYear()), String.format("%.0f", fh.getPredictedQty()),
                    String.format("%.0f", fh.getActualQty()), String.format("%.1f", fh.getAccuracy()),
                    fh.getResult(), String.format("%.4f", fh.getConfidence())});
        }
        ExcelExporter.export("Forecast History", headers, data, historyTable.getScene().getWindow());
    }

    // Initializes trend tab combined controls and populates trend data table
    // ========== Tab 3: Trends with LineChart ==========
    private void setupTrendTab() {
        colTrendPeriod.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().period));
        colTrendTotal.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().total));
        colTrendHits.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().hits));
        colTrendFairs.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().fairs));
        colTrendMisses.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().misses));
        colTrendAvgAcc.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().avgAccuracy));

        cmbTrendYear.setItems(FXCollections.observableArrayList("2025", "2026"));
        cmbTrendYear.setValue("2025");
        cmbTrendMonth.setItems(FXCollections.observableArrayList(
                "Full Year", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"));
        cmbTrendMonth.setValue("Full Year");
    }

    // Reloads trend chart when user changes year or month selection
    @FXML
    private void handleTrendFilter() {
        String year = cmbTrendYear.getValue();
        String month = cmbTrendMonth.getValue();
        loadTrendChart(year, month);
    }

    // Loads and visualizes forecast trend data as line chart showing predicted vs actual quantities and accuracy
    private void loadTrendChart(String year, String monthName) {
        trendChart.getData().clear();
        boolean fullYear = "Full Year".equals(monthName);

        String title;
        String sql;

        if (fullYear) {
            title = "Full Year " + year + " — Predicted vs Actual per Month";
            sql = "SELECT fh.forecast_month, " +
                    "SUM(fh.predicted_qty) AS total_predicted, " +
                    "SUM(fh.actual_qty) AS total_actual, " +
                    "AVG(fh.accuracy) AS avg_accuracy " +
                    "FROM forecast_history fh WHERE fh.forecast_year = " + year + " " +
                    "GROUP BY fh.forecast_month ORDER BY fh.forecast_month";

            XYChart.Series<String, Number> predictedSeries = new XYChart.Series<>();
            predictedSeries.setName("Predicted");
            XYChart.Series<String, Number> actualSeries = new XYChart.Series<>();
            actualSeries.setName("Actual");
            XYChart.Series<String, Number> accuracySeries = new XYChart.Series<>();
            accuracySeries.setName("Accuracy %");

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                while (rs.next()) {
                    int m = rs.getInt("forecast_month");
                    String label = monthNames[m];
                    predictedSeries.getData().add(new XYChart.Data<>(label, rs.getDouble("total_predicted")));
                    actualSeries.getData().add(new XYChart.Data<>(label, rs.getDouble("total_actual")));
                    accuracySeries.getData().add(new XYChart.Data<>(label, rs.getDouble("avg_accuracy") * 100));
                }
            } catch (SQLException e) { e.printStackTrace(); }

            xAxis.setLabel("Month");
            yAxis.setLabel("Quantity");
            trendChart.getData().addAll(predictedSeries, actualSeries, accuracySeries);

        } else {
            int monthNum = getMonthNumber(monthName);
            title = monthName + " " + year + " — Per Item: Predicted vs Actual";
            sql = "SELECT fh.*, i.name AS item_name FROM forecast_history fh " +
                    "JOIN items i ON fh.item_id = i.item_id " +
                    "WHERE fh.forecast_year = " + year + " AND fh.forecast_month = " + monthNum + " " +
                    "ORDER BY i.name";

            XYChart.Series<String, Number> predictedSeries = new XYChart.Series<>();
            predictedSeries.setName("Predicted");
            XYChart.Series<String, Number> actualSeries = new XYChart.Series<>();
            actualSeries.setName("Actual");
            XYChart.Series<String, Number> accuracySeries = new XYChart.Series<>();
            accuracySeries.setName("Accuracy %");

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    if (itemName.length() > 15) itemName = itemName.substring(0, 15) + "..";
                    predictedSeries.getData().add(new XYChart.Data<>(itemName, rs.getDouble("predicted_qty")));
                    actualSeries.getData().add(new XYChart.Data<>(itemName, rs.getDouble("actual_qty")));
                    accuracySeries.getData().add(new XYChart.Data<>(itemName, rs.getDouble("accuracy")));
                }
            } catch (SQLException e) { e.printStackTrace(); }

            xAxis.setLabel("Item");
            yAxis.setLabel("Quantity");
            trendChart.getData().addAll(predictedSeries, actualSeries, accuracySeries);
        }

        lblChartTitle.setText(title);

        // Color the lines
        trendChart.applyCss();
        trendChart.layout();
        if (trendChart.getData().size() >= 3) {
            setSeriesColor(trendChart.getData().get(0), "#3498db");
            setSeriesColor(trendChart.getData().get(1), "#27ae60");
            setSeriesColor(trendChart.getData().get(2), "#e74c3c");
        }
    }

    // Applies a specific color style to a chart series and its data points
    private void setSeriesColor(XYChart.Series<String, Number> series, String color) {
        if (series.getNode() != null) {
            series.getNode().setStyle("-fx-stroke: " + color + "; -fx-stroke-width: 2px;");
        }
        for (XYChart.Data<String, Number> d : series.getData()) {
            if (d.getNode() != null) {
                d.getNode().setStyle("-fx-background-color: " + color + ", white; -fx-background-radius: 4px;");
            }
        }
    }

    // Populates trend data table with forecast statistics grouped by year and month
    private void loadTrendTable() {
        ObservableList<TrendRow> trends = FXCollections.observableArrayList();
        String sql = "SELECT forecast_year, forecast_month, COUNT(*) AS total, " +
                "SUM(CASE WHEN result='HIT' THEN 1 ELSE 0 END) AS hits, " +
                "SUM(CASE WHEN result='FAIR' THEN 1 ELSE 0 END) AS fairs, " +
                "SUM(CASE WHEN result='MISS' THEN 1 ELSE 0 END) AS misses, " +
                "AVG(accuracy) AS avg_acc " +
                "FROM forecast_history GROUP BY forecast_year, forecast_month " +
                "ORDER BY forecast_year, forecast_month";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TrendRow row = new TrendRow();
                row.period = rs.getInt("forecast_year") + "-" + String.format("%02d", rs.getInt("forecast_month"));
                row.total = rs.getInt("total");
                row.hits = rs.getInt("hits");
                row.fairs = rs.getInt("fairs");
                row.misses = rs.getInt("misses");
                row.avgAccuracy = Math.round(rs.getDouble("avg_acc") * 10.0) / 10.0;
                trends.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        trendTable.setItems(trends);
    }

    // Converts month name to its numeric representation (1-12)
    private int getMonthNumber(String name) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(name)) return i + 1;
        }
        return 1;
    }

    // Inner class representing a row of trend data with period, hit counts, and accuracy metrics
    public static class TrendRow {
        // String representing the period (year-month)
        public String period;
        // Total number of forecasts in this period
        public int total;
        // Number of accurate HIT forecasts
        public int hits;
        // Number of partially accurate FAIR forecasts
        public int fairs;
        // Number of inaccurate MISS forecasts
        public int misses;
        // Average accuracy percentage for all forecasts in period
        public double avgAccuracy;
    }
}