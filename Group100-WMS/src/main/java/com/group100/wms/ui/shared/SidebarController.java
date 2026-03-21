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
    // Role IDs:
    // 1 = ADMIN
    // 2 = WAREHOUSE_MANAGER
    // 3 = SUPERVISOR
    // 4 = ACCOUNTANT
    // 5 = SENIOR_MANAGER
    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;
        usernameLabel.setText(user.getUsername());
        applyRbac(user.getRoleId());
    }
    private void applyRbac(int roleId) {
        setBtn(btnInventory,         true);
        setBtn(btnInbound,           roleId == 1 || roleId == 2);
        setBtn(btnOutbound,          roleId == 1 || roleId == 2 || roleId == 3 || roleId == 5);
        setBtn(btnAttendance,        roleId == 1 || roleId == 2 || roleId == 3 || roleId == 4);
        setBtn(btnPayroll,           roleId == 1 || roleId == 4);
        setBtn(btnForecasting,       roleId == 1 || roleId == 5);
        setBtn(btnReports,           roleId == 1 || roleId == 4 || roleId == 5);
        setBtn(btnAdmin,             roleId == 1);
        setBtn(btnEmployeeDirectory, roleId == 3);
        setBtn(btnLeaveRequests,     roleId == 3);
        setBtn(btnAuditLog,          roleId == 1);
    }

    private void setBtn(Button btn, boolean visible) {
        btn.setVisible(visible);
        btn.setManaged(visible);
    }
    @FXML private void onInventory()   { navigate("/fxml/inventory/InventoryList.fxml"); }
    @FXML private void onInbound()     { navigate("/fxml/inbound/PurchaseOrderList.fxml"); }
    @FXML private void onOutbound()    { navigate("/fxml/outbound/GinList.fxml"); }
    @FXML
    private void onAttendance() {
        User user = SessionManager.getCurrentUser();
        if (user != null && user.getRoleId() == 3) {
            navigate("/fxml/supervisor/AttendanceCompilation.fxml");
        } else {
            navigate("/fxml/attendance/AttendanceList.fxml");
        }
    }
    @FXML private void onPayroll()            { navigate("/fxml/payroll/PayrollGeneration.fxml"); }
    @FXML private void onForecasting()        { navigate("/fxml/forecasting/ForecastDashboard.fxml"); }
    @FXML private void onReports()            { navigate("/fxml/reports/ReportCentre.fxml"); }
    @FXML private void onAdmin()              { navigate("/fxml/admin/UserManagement.fxml"); }
    @FXML private void onAuditLog()           { navigate("/fxml/admin/AuditLog.fxml"); }
    @FXML private void onEmployeeDirectory()  { navigate("/fxml/supervisor/EmployeeDirectory.fxml"); }
    @FXML private void onLeaveRequests()      { navigate("/fxml/supervisor/LeaveRequests.fxml"); }
    @FXML
    private void onLogout() {
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/auth/Login.fxml"));
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setMaximized(false);
        } catch (IOException e) {
            System.err.println("[UI] Failed to navigate to login: " + e.getMessage());
        }
    }
    private void navigate(String fxmlPath) {
        try {
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            MainLayoutController controller =
                    (MainLayoutController) stage.getScene().getUserData();
            if (controller != null) controller.loadView(fxmlPath);
        } catch (Exception e) {
            System.err.println("[UI] Navigation failed: " + e.getMessage());
        }
    }
}
