package com.group100.wms.ui.shared;

import com.group100.wms.core.SessionManager;
import com.group100.wms.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class SidebarController {

    @FXML private Label usernameLabel;
    @FXML private Label roleLabel;
    @FXML private VBox navMenu;
    @FXML private Button btnInventory;
    @FXML private Button btnInbound;
    @FXML private Button btnOutbound;
    @FXML private Button btnAttendance;
    @FXML private Button btnPayroll;
    @FXML private Button btnForecasting;
    @FXML private Button btnReports;
    @FXML private Button btnAdmin;
    @FXML private Button btnEmployeeDirectory;
    @FXML private Button btnAuditLog;
    @FXML private Button btnLeaveRequests;

    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) {
            return;
        }
        usernameLabel.setText(user.getUsername());
        roleLabel.setText(roleName(user.getRoleId()));
        applyRbac(user.getRoleId());
    }

    private void applyRbac(int roleId) {
        setBtn(btnInventory, true);
        setBtn(btnInbound, roleId == 1 || roleId == 2);
        setBtn(btnOutbound, roleId == 1 || roleId == 2 || roleId == 3 || roleId == 5);
        setBtn(btnAttendance, roleId == 1 || roleId == 2 || roleId == 3 || roleId == 4);
        setBtn(btnPayroll, roleId == 1 || roleId == 4);
        setBtn(btnForecasting, roleId == 1 || roleId == 5);
        setBtn(btnReports, roleId == 1 || roleId == 4 || roleId == 5);
        setBtn(btnAdmin, roleId == 1);
        setBtn(btnEmployeeDirectory, roleId == 3);
        setBtn(btnLeaveRequests, roleId == 3);
        setBtn(btnAuditLog, roleId == 1);
    }

    private void setBtn(Button btn, boolean visible) {
        btn.setVisible(visible);
        btn.setManaged(visible);
    }

    @FXML private void onInventory() { navigate("/fxml/inventory/InventoryList.fxml", btnInventory); }
    @FXML private void onInbound() { navigate("/fxml/inbound/PurchaseOrderList.fxml", btnInbound); }
    @FXML private void onOutbound() { navigate("/fxml/outbound/GinList.fxml", btnOutbound); }
    @FXML private void onPayroll() { navigate("/fxml/payroll/PayrollGeneration.fxml", btnPayroll); }
    @FXML private void onForecasting() { navigate("/fxml/forecasting/ForecastDashboard.fxml", btnForecasting); }
    @FXML private void onReports() { navigate("/fxml/reports/ReportCentre.fxml", btnReports); }
    @FXML private void onAdmin() { navigate("/fxml/admin/UserManagement.fxml", btnAdmin); }
    @FXML private void onAuditLog() { navigate("/fxml/admin/AuditLog.fxml", btnAuditLog); }
    @FXML private void onEmployeeDirectory() { navigate("/fxml/supervisor/EmployeeDirectory.fxml", btnEmployeeDirectory); }
    @FXML private void onLeaveRequests() { navigate("/fxml/supervisor/LeaveRequests.fxml", btnLeaveRequests); }

    @FXML
    private void onAttendance() {
        User user = SessionManager.getCurrentUser();
        String view = user != null && user.getRoleId() == 3
                ? "/fxml/supervisor/AttendanceCompilation.fxml"
                : "/fxml/attendance/AttendanceList.fxml";
        navigate(view, btnAttendance);
    }

    @FXML
    private void onLogout() {
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/Login.fxml"));
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            Scene scene = SceneStyles.createScene(
                    loader.load(),
                    SceneStyles.LOGIN_WIDTH,
                    SceneStyles.LOGIN_HEIGHT,
                    getClass());
            stage.setScene(scene);
            stage.setMaximized(false);
        } catch (IOException e) {
            System.err.println("[UI] Failed to navigate to login: " + e.getMessage());
        }
    }

    private void navigate(String fxmlPath, Button activeButton) {
        try {
            setActiveButton(activeButton);
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            MainLayoutController controller = (MainLayoutController) stage.getScene().getUserData();
            if (controller != null) {
                controller.loadView(fxmlPath);
            }
        } catch (Exception e) {
            System.err.println("[UI] Navigation failed: " + e.getMessage());
        }
    }

    private void setActiveButton(Button activeButton) {
        for (Button button : navButtons()) {
            button.getStyleClass().remove("sidebar-btn-active");
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("sidebar-btn-active")) {
            activeButton.getStyleClass().add("sidebar-btn-active");
        }
    }

    private List<Button> navButtons() {
        return List.of(
                btnInventory,
                btnInbound,
                btnOutbound,
                btnAttendance,
                btnEmployeeDirectory,
                btnLeaveRequests,
                btnPayroll,
                btnForecasting,
                btnReports,
                btnAdmin,
                btnAuditLog);
    }

    private String roleName(int roleId) {
        return switch (roleId) {
            case 1 -> "Administrator";
            case 2 -> "Warehouse Manager";
            case 3 -> "Supervisor";
            case 4 -> "Accountant";
            case 5 -> "Senior Manager";
            default -> "User";
        };
    }
}
