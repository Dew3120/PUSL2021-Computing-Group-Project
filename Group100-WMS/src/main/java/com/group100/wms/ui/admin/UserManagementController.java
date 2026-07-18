package com.group100.wms.ui.admin;

import com.group100.wms.core.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.sql.*;

public class UserManagementController {

    @FXML private Label lblTotalUsers, lblActive;
    @FXML private FlowPane userCardsPane;

    @FXML
    public void initialize() {
        loadUsers();
    }

    private void loadUsers() {
        userCardsPane.getChildren().clear();
        int total = 0, active = 0;

        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id ORDER BY u.user_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                total++;
                if (rs.getBoolean("is_active")) active++;

                VBox card = createUserCard(
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("role_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("department"),
                        rs.getString("bio"),
                        rs.getString("nic"),
                        rs.getString("emergency_contact"),
                        rs.getString("skills"),
                        rs.getString("availability"),
                        rs.getString("date_joined"),
                        rs.getBoolean("is_active")
                );
                userCardsPane.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lblTotalUsers.setText(String.valueOf(total));
        lblActive.setText(String.valueOf(active));
    }

    private VBox createUserCard(String fullName, String username, String role,
                                String email, String phone, String department,
                                String bio, String nic, String emergencyContact,
                                String skills, String availability, String dateJoined,
                                boolean isActive) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setPrefWidth(350);
        card.setMinHeight(320);
        card.getStyleClass().add("user-card");

        String initials = "";
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.split(" ");
            initials = parts[0].substring(0, 1);
            if (parts.length > 1) initials += parts[parts.length - 1].substring(0, 1);
        }

        Label avatar = new Label(initials.toUpperCase());
        avatar.getStyleClass().add("user-avatar");
        avatar.setAlignment(Pos.CENTER);
        avatar.setMinSize(50, 50);
        avatar.setMaxSize(50, 50);

        Label nameLabel = new Label(fullName != null ? fullName : username);
        nameLabel.getStyleClass().add("user-card-name");

        Label roleLabel = new Label(role != null ? role.replace("_", " ") : "");
        String badgeColor = switch (role) {
            case "ADMIN" -> "#e74c3c";
            case "WAREHOUSE_MANAGER" -> "#3498db";
            case "SUPERVISOR" -> "#27ae60";
            case "ACCOUNTANT" -> "#f39c12";
            case "SENIOR_MANAGER" -> "#8e44ad";
            default -> "#95a5a6";
        };
        roleLabel.getStyleClass().add("role-badge");
        roleLabel.setStyle("-fx-background-color: " + badgeColor + ";");

        Label statusLabel = new Label(isActive ? "ACTIVE" : "INACTIVE");
        statusLabel.getStyleClass().add(isActive ? "status-online" : "status-offline");

        HBox header = new HBox(10, avatar, new VBox(4, nameLabel, new HBox(8, roleLabel, statusLabel)));
        header.setAlignment(Pos.CENTER_LEFT);

        VBox details = new VBox(4);
        details.setStyle("-fx-padding: 5 0 0 0;");
        if (department != null) addDetail(details, "Department", department);
        if (email != null) addDetail(details, "Email", email);
        if (phone != null) addDetail(details, "Phone", phone);
        if (nic != null) addDetail(details, "NIC", nic);
        if (dateJoined != null) addDetail(details, "Joined", dateJoined);
        if (skills != null) addDetail(details, "Skills", skills);
        if (emergencyContact != null) addDetail(details, "Emergency", emergencyContact);

        if (bio != null && !bio.isEmpty()) {
            Label bioLabel = new Label(bio);
            bioLabel.setWrapText(true);
            bioLabel.getStyleClass().add("user-bio");
            details.getChildren().add(bioLabel);
        }

        card.getChildren().addAll(header, new Separator(), details);
        return card;
    }

    private void addDetail(VBox container, String label, String value) {
        HBox row = new HBox(5);
        Label lbl = new Label(label + ":");
        lbl.getStyleClass().add("user-detail-label");
        Label val = new Label(value);
        val.getStyleClass().add("user-detail-value");
        val.setWrapText(true);
        row.getChildren().addAll(lbl, val);
        container.getChildren().add(row);
    }
}
