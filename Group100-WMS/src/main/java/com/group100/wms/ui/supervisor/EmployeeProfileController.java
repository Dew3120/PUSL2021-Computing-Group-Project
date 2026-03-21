package com.group100.wms.ui.supervisor;

import com.group100.wms.ui.supervisor.EmployeeDirectoryController.EmpRow;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EmployeeProfileController {

    @FXML private Label lblInitials;
    @FXML private Label lblName;
    @FXML private Label lblDesignation;
    @FXML private Label lblSection;
    @FXML private Label lblStatusBadge;
    @FXML private Label lblEmpId;
    @FXML private Label lblGender;
    @FXML private Label lblAge;
    @FXML private Label lblDob;
    @FXML private Label lblMaritalStatus;
    @FXML private Label lblBloodGroup;
    @FXML private Label lblNic;
    @FXML private Label lblPhone;
    @FXML private Label lblEmail;
    @FXML private Label lblAddress;
    @FXML private Label lblCity;
    @FXML private Label lblEmergencyName;
    @FXML private Label lblEmergencyPhone;
    @FXML private Label lblJoinedDate;
    @FXML private Label lblDailyRate;
    @FXML private Label lblResignationDate;
    @FXML private Label lblBankName;
    @FXML private Label lblBankBranch;
    @FXML private Label lblAccountNumber;

    public void setEmployee(EmpRow emp) {
        lblInitials.setText(getInitials(emp.fullName));
        lblName.setText(emp.fullName);
        lblDesignation.setText(emp.designation);
        lblSection.setText(emp.section);
        boolean resigned = emp.status.equals("RESIGNED");
        lblStatusBadge.setText(resigned ? "RESIGNED" : "ACTIVE");
        lblStatusBadge.setStyle(resigned
                ? "-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-padding:3 10;-fx-background-radius:12;-fx-font-weight:bold;"
                : "-fx-background-color:#27ae60;-fx-text-fill:white;-fx-padding:3 10;-fx-background-radius:12;-fx-font-weight:bold;");
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

    @FXML private void onClose() {
        ((Stage) lblName.getScene().getWindow()).close();
    }

    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) if (!p.isEmpty()) sb.append(p.charAt(0));
        return sb.length() > 2 ? sb.substring(0, 2).toUpperCase() : sb.toString().toUpperCase();
    }
}
