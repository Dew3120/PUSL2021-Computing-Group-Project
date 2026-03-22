package com.group100.wms.ui.supervisor;

import com.group100.wms.ui.supervisor.EmployeeDirectoryController.EmpRow;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// OOP Concepts used in this class:
// 1. Encapsulation: The class encapsulates the logic for displaying a specific employee's data, ensuring that the UI updates (like status badge colors) are handled internally.
// 2. Data Transfer Object (DTO) usage: It accepts an "EmpRow" object, demonstrating how data is passed between controllers in a JavaFX application.
public class EmployeeProfileController {

    // Profile Header components
    @FXML private Label lblInitials, lblName, lblDesignation, lblSection, lblStatusBadge;
    
    // Personal and Contact detail labels
    @FXML private Label lblEmpId, lblGender, lblAge, lblDob, lblMaritalStatus, lblBloodGroup, lblNic;
    @FXML private Label lblPhone, lblEmail, lblAddress, lblCity, lblEmergencyName, lblEmergencyPhone;
    
    // Employment and Financial detail labels
    @FXML private Label lblJoinedDate, lblDailyRate, lblResignationDate;
    @FXML private Label lblBankName, lblBankBranch, lblAccountNumber;

    // Populates the entire UI with data from the selected Employee row
    public void setEmployee(EmpRow emp) {
        // Generates initials for the profile avatar (e.g., "John Doe" -> "JD")
        lblInitials.setText(getInitials(emp.fullName));
        lblName.setText(emp.fullName);
        lblDesignation.setText(emp.designation);
        lblSection.setText(emp.section);
        
        // Handles conditional logic for the status badge (Green for Active, Red for Resigned)
        boolean resigned = emp.status.equals("RESIGNED");
        lblStatusBadge.setText(resigned ? "RESIGNED" : "ACTIVE");
        lblStatusBadge.setStyle(resigned
                ? "-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-padding:3 10;-fx-background-radius:12;-fx-font-weight:bold;"
                : "-fx-background-color:#27ae60;-fx-text-fill:white;-fx-padding:3 10;-fx-background-radius:12;-fx-font-weight:bold;");
        
        // Formats data for display (ID padding, Age, and Currency)
        lblEmpId.setText("EMP-" + String.format("%03d", emp.empId));
        lblGender.setText(emp.gender);
        lblAge.setText(emp.age > 0 ? emp.age + " years" : "-");
        lblDob.setText(emp.dob);
        lblMaritalStatus.setText(emp.maritalStatus);
        lblBloodGroup.setText(emp.bloodGroup);
        lblNic.setText(emp.nic);
        lblPhone.setText(emp.phone);
        lblEmail.setText(emp.email);
        lblAddress.setText(emp.address);
        lblCity.setText(emp.city);
        lblEmergencyName.setText(emp.emergencyName);
        lblEmergencyPhone.setText(emp.emergencyPhone);
        lblJoinedDate.setText(emp.joinedDate);
        lblDailyRate.setText(String.format("Rs. %,.2f / day", emp.dailyRate));
        lblResignationDate.setText(resigned ? emp.resignationDate : "-");
        lblBankName.setText(emp.bankName);
        lblBankBranch.setText(emp.bankBranch);
        lblAccountNumber.setText(emp.accountNumber);
    }

    // Event handler to close the modal window
    @FXML private void onClose() {
        ((Stage) lblName.getScene().getWindow()).close();
    }

    // Helper logic to extract up to two initials from a full name string
    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) if (!p.isEmpty()) sb.append(p.charAt(0));
        return sb.length() > 2 ? sb.substring(0, 2).toUpperCase() : sb.toString().toUpperCase();
    }
}
