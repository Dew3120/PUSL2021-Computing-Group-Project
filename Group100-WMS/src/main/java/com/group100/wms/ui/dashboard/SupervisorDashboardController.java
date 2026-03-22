import java.util.Map;

// OOP Concepts Used:
// Encapsulation - Variables and methods are contained within this controller class.
// Abstraction - DashboardService hides business logic and repository interaction.
// Association - This controller communicates with DashboardService and JavaFX Label controls.

public class SupervisorDashboardController {

    // Label used to display total number of items
    @FXML private Label totalItemsLabel;

    // Label used to display low stock item count
    @FXML private Label lowStockLabel;

    // Label used to display status or error messages
    @FXML private Label statusLabel;

    // Service object used to fetch dashboard KPI information
    private final DashboardService dashboardService = new DashboardService(
            new ItemRepository(), new BatchRepository(), new GrnRepository(),
            new GinRepository(), new AttendanceRepository(), new PayrollRepository());

    // Automatically called when the FXML controller is loaded
    @FXML
    public void initialize() { loadKpis(); }

    // Loads KPI values and updates dashboard labels
    private void loadKpis() {
        try {
            Map<String, Object> kpis = dashboardService.getAdminKpis();
            totalItemsLabel.setText(String.valueOf(kpis.get("totalItems")));

            if (kpis.containsKey("lowStockCount")) {
                lowStockLabel.setText(String.valueOf(kpis.get("lowStockCount")));
            } else {
                lowStockLabel.setText("0");
            }

            statusLabel.setText("");
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading KPIs: " + e.getMessage());
        }
    }

    // Refresh action to reload KPI data
    @FXML private void handleRefresh() { loadKpis(); }
}
