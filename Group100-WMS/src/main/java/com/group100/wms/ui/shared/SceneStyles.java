package com.group100.wms.ui.shared;

import javafx.scene.Parent;
import javafx.scene.Scene;

public final class SceneStyles {

    public static final double LOGIN_WIDTH = 1120;
    public static final double LOGIN_HEIGHT = 720;
    public static final double APP_WIDTH = 1280;
    public static final double APP_HEIGHT = 800;
    public static final double APP_MIN_WIDTH = 1100;
    public static final double APP_MIN_HEIGHT = 700;

    private static final String[] STYLESHEETS = {
            "/css/global.css",
            "/css/dashboard.css",
            "/css/tables.css",
            "/css/forms.css",
            "/css/redesign.css"
    };

    private SceneStyles() {
    }

    public static Scene createScene(Parent root, double width, double height, Class<?> anchor) {
        Scene scene = new Scene(root, width, height);
        apply(scene, anchor);
        return scene;
    }

    public static void apply(Scene scene, Class<?> anchor) {
        for (String stylesheet : STYLESHEETS) {
            scene.getStylesheets().add(anchor.getResource(stylesheet).toExternalForm());
        }
    }
}
