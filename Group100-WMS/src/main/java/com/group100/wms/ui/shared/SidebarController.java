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

// OOP Concepts Used:
// Encapsulation - Uses User object and SessionManager to access user data safely via methods
// Abstraction - Hides navigation and RBAC logic inside methods like navigate() and applyRbac()
// Polymorphism - Method behavior varies based on user role (e.g., onAttendance())
// Inheritance - JavaFX controllers implicitly extend Object and use JavaFX framework classes

public class SidebarController {

    // Label to display the logged-in user's username
    @FXML private Label usernameLabel;

    // Label to display the user's role (currently unused but reserved for role display)
    @FXML private Label roleLabel;

    // Container holding navigation menu items
    @FXML private VBox navMenu;

    // Navigation buttons for different modules
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

    // Initializes the sidebar when the UI is loaded, setting username and applying role-based access control
    @FXML
    public void initialize() {
        User user = SessionManager.getCurrentUser();
        if (user == null) return;
        usernameLabel.setText(user.getUsername());
        applyRbac(user.getRoleId());
    }

    // Applies role-based access control (RBAC) by showing/hiding buttons based on user role
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

    // Controls visibility and layout management of a button
    private void setBtn(Button btn, boolean visible) {
        btn.setVisible(visible);
        btn.setManaged(visible);
    }

    // Navigates to the Inventory module
    @FXML private void onInventory()   { navigate("/fxml/inventory/InventoryList.fxml"); }

    // Navigates to the Inbound (Purchase Orders) module
    @FXML private void onInbound()     { navigate("/fxml/inbound/PurchaseOrderList.fxml"); }

    // Navigates to the Outbound (GIN) module
    @FXML private void onOutbound()    { navigate("/fxml/outbound/GinList.fxml"); }

    // Navigates to Attendance module with role-based view selection
    @FXML
    private void onAttendance() {
        User user = SessionManager.getCurrentUser();
        if (user != null && user.getRoleId() == 3) {
            navigate("/fxml/supervisor/AttendanceCompilation.fxml");
        } else {
            navigate("/fxml/attendance/AttendanceList.fxml");
        }
    }

    // Navigates to Payroll generation module
    @FXML private void onPayroll()            { navigate("/fxml/payroll/PayrollGeneration.fxml"); }

    // Navigates to Forecasting dashboard
    @FXML private void onForecasting()        { navigate("/fxml/forecasting/ForecastDashboard.fxml"); }

    // Navigates to Reports center
    @FXML private void onReports()            { navigate("/fxml/reports/ReportCentre.fxml"); }

    // Navigates to Admin (User Management) panel
    @FXML private void onAdmin()              { navigate("/fxml/admin/UserManagement.fxml"); }

    // Navigates to Audit Log view
    @FXML private void onAuditLog()           { navigate("/fxml/admin/AuditLog.fxml"); }

    // Navigates to Employee Directory (Supervisor view)
    @FXML private void onEmployeeDirectory()  { navigate("/fxml/supervisor/EmployeeDirectory.fxml"); }

    // Navigates to Leave Requests (Supervisor view)
    @FXML private void onLeaveRequests()      { navigate("/fxml/supervisor/LeaveRequests.fxml"); }

    // Handles user logout and redirects to login screen
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

    // Handles navigation by loading the given FXML into the main layout
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
