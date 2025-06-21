package com.warnercloud.musicplayer.Factory;

import com.warnercloud.musicplayer.Controller.QueueItemController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class QueueItemFactory {
    QueueItemFactory(){}

    public static HBox creaetQueueItem(){
        try {
            FXMLLoader loader = new FXMLLoader(QueueItemFactory.class.getResource("/com/warnercloud/musicplayer/Views/queue-list-item.fxml"));
            HBox item = loader.load();
            QueueItemController controller = loader.getController();
            item.getProperties().put("controller", controller);
            return item;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
