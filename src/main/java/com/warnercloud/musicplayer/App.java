package com.warnercloud.musicplayer;

import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Utils.AppJsonManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setMinHeight(650);
        stage.setMinWidth(1000);
        stage.setOnCloseRequest(event -> MediaService.getInstance().dispose());
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        launch();
    }

}