package com.warnercloud.musicplayer.Factory;

import com.warnercloud.musicplayer.Controller.CenterViewController;
import com.warnercloud.musicplayer.Controller.MediaBarController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class CenterViewFactory {
    private CenterViewFactory() {}

    public static BorderPane createMediaBar() {
        try {
            FXMLLoader loader = new FXMLLoader(MediaBarFactory.class.getResource("/com/warnercloud/musicplayer/Views/center-view.fxml"));
            BorderPane root = loader.load();

            CenterViewController controller = loader.getController();
            root.getProperties().put("controller", controller);
            return root;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
