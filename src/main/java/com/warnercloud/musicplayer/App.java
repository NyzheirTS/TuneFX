package com.warnercloud.musicplayer;


import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Utils.WindowResizeUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.SQLException;

public class App extends Application {

    private double xOffset;
    private double yOffset;

    private HBox createTitleBar(Stage stage) {

        Label title = new Label("TuneFX");
        title.getStyleClass().add("window-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minimize = new Button("—");
        Button maximize = new Button("□");
        Button close = new Button("×");

        minimize.getStyleClass().add("window-button");
        maximize.getStyleClass().add("window-button");

        close.getStyleClass().addAll("window-button", "close-button");

        // Minimize
        minimize.setOnAction(e -> stage.setIconified(true));

        // Maximize / restore
        maximize.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));

        // Close
        close.setOnAction(e -> {
            stage.close();
            Platform.exit();
        });

        HBox bar = new HBox(title, spacer, minimize, maximize, close);

        bar.getStyleClass().add("title-bar");


        bar.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;

            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        bar.setOnMouseDragged(event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;
            if (stage.isMaximized()) return;

            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Double click title bar to maximize / restore
        bar.setOnMouseClicked(event -> {

            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
            }
        });

        return bar;
    }


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Views/main-view.fxml"));
        Parent mainContent = fxmlLoader.load();
        HBox titleBar = createTitleBar(stage);
        VBox root = new VBox(titleBar, mainContent);

        // Allow main content to fill all remaining space
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // Rounded window clipping
        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());

        clip.setArcWidth(24);
        clip.setArcHeight(24);

        root.setClip(clip);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);

        stage.setTitle("TuneFX");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.setMinHeight(650);
        stage.setMinWidth(1090);

        WindowResizeUtil.makeResizable(stage, scene);

        stage.setOnCloseRequest(event -> MediaService.getInstance().dispose());
        stage.show();
    }


    public static void main(String[] args) throws SQLException {
        launch();
    }

}