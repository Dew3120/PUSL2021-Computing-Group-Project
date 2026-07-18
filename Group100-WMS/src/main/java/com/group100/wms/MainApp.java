package com.group100.wms;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.ui.shared.SceneStyles;
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
        Scene scene = SceneStyles.createScene(loader.load(), SceneStyles.LOGIN_WIDTH, SceneStyles.LOGIN_HEIGHT, getClass());
        stage.setTitle("Group100 WMS");
        stage.setScene(scene);
        stage.setMinWidth(SceneStyles.APP_MIN_WIDTH);
        stage.setMinHeight(SceneStyles.APP_MIN_HEIGHT);
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
