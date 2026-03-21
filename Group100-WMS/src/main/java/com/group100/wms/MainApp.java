package com.group100.wms;

import com.group100.wms.core.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/auth/Login.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        scene.getStylesheets().add(
                getClass().getResource("/css/global.css").toExternalForm());
        scene.getStylesheets().add(
                getClass().getResource("/css/dashboard.css").toExternalForm());
        scene.getStylesheets().add(
                getClass().getResource("/css/tables.css").toExternalForm());
        scene.getStylesheets().add(
                getClass().getResource("/css/forms.css").toExternalForm());
        stage.setTitle("Group100 WMS");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    @Override
    public void init() {
        DatabaseConnection.initialise();
    }

    @Override
    public void stop() {
        DatabaseConnection.shutdown();
    }
}