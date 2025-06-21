package com.warnercloud.musicplayer.Factory;

import com.warnercloud.musicplayer.Controller.MediaBarController;
import com.warnercloud.musicplayer.Controller.QueueListController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class QueueListFactory {
    QueueListFactory() {}

    public static VBox createQueueList() {
        try {
            FXMLLoader loader = new FXMLLoader(QueueListFactory.class.getResource("/com/warnercloud/musicplayer/Views/queue-view.fxml"));
            VBox root = loader.load();

            QueueListController controller = loader.getController();

            root.getProperties().put("controller", controller);
            return root;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
