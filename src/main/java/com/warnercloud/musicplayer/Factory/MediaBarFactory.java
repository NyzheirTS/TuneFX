package com.warnercloud.musicplayer.Factory;

import com.warnercloud.musicplayer.Controller.MediaBarController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class MediaBarFactory {
    private MediaBarFactory() {}

    public static HBox createMediaBar() {
        try {
            FXMLLoader loader = new FXMLLoader(MediaBarFactory.class.getResource("/com/warnercloud/musicplayer/Views/media-bar.fxml"));
            HBox root = loader.load();

            MediaBarController controller = loader.getController();
            controller.initUI();
            root.getProperties().put("controller", controller);
            return root;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
