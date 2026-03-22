package com.group100.wms.ui.admin;

import com.group100.wms.core.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;

import java.sql.*;

// OOP Concepts Used:
// Encapsulation - UI components and related logic are contained within this controller class.
// Abstraction - Database operations are abstracted using DatabaseConnection and SQL queries.
// Inheritance - JavaFX UI elements (Label, VBox, HBox, FlowPane) inherit from base Node classes.
// Polymorphism - Methods like setText(), setStyle(), and layout behaviors differ across UI components.

public class UserManagementController {

    // Label to display total number of users in the system
    @FXML private Label lblTotalUsers, lblActive;

    // FlowPane layout to dynamically hold and display user cards
    @FXML private FlowPane userCardsPane;

    // Automatically called when the UI is loaded; initializes user data loading
    @FXML
    public void initialize() {
        loadUsers();
    }

    // Fetches user data from the database, creates UI cards, and updates summary labels
    private void loadUsers() {
        userCardsPane.getChildren().clear();

        // Stores total number of users retrieved from database
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

    // Builds and returns a styled user card containing personal and professional details
    private VBox createUserCard(String fullName, String username, String role,
                                String email, String phone, String department,
                                String bio, String nic, String emergencyContact,
                                String skills, String availability, String dateJoined,
                                boolean isActive) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setPrefWidth(350);
        card.setMinHeight(320);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);");

        // Stores initials generated from user's full name for avatar display
        String initials = "";
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.split(" ");
            initials = parts[0].substring(0, 1);
            if (parts.length > 1) initials += parts[parts.length - 1].substring(0, 1);
        }

        Label avatar = new Label(initials.toUpperCase());
        avatar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 20; " +
                "-fx-font-weight: bold; -fx-pref-width: 50; -fx-pref-height: 50; " +
                "-fx-background-radius: 25; -fx-alignment: center;");
        avatar.setAlignment(Pos.CENTER);
        avatar.setMinSize(50, 50);
        avatar.setMaxSize(50, 50);

        Label nameLabel = new Label(fullName != null ? fullName : username);
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label roleLabel = new Label(role != null ? role.replace("_", " ") : "");

        // Determines badge color based on user role
        String badgeColor = switch (role) {
            case "ADMIN" -> "#e74c3c";
            case "WAREHOUSE_MANAGER" -> "#3498db";
            case "SUPERVISOR" -> "#27ae60";
            case "ACCOUNTANT" -> "#f39c12";
            case "SENIOR_MANAGER" -> "#8e44ad";
            default -> "#95a5a6";
        };

        roleLabel.setStyle("-fx-background-color: " + badgeColor + "; -fx-text-fill: white; " +
                "-fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 11;");

        Label statusLabel = new Label(isActive ? "● ONLINE" : "● OFFLINE");
        statusLabel.setStyle("-fx-text-fill: " + (isActive ? "#27ae60" : "#95a5a6") + "; -fx-font-weight: bold; -fx-font-size: 11;");

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

        // Displays user bio if available
        if (bio != null && !bio.isEmpty()) {
            Label bioLabel = new Label(bio);
            bioLabel.setWrapText(true);
            bioLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666; -fx-padding: 5 0 0 0;");
            details.getChildren().add(bioLabel);
        }

        card.getChildren().addAll(header, new Separator(), details);
        return card;
    }

    // Adds a labeled detail (field name and value) to the provided container
    private void addDetail(VBox container, String label, String value) {
        HBox row = new HBox(5);
        Label lbl = new Label(label + ":");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11; -fx-min-width: 100;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 11;");
        val.setWrapText(true);
        row.getChildren().addAll(lbl, val);
        container.getChildren().add(row);
    }
}