package com.warnercloud.musicplayer.Factory;

import com.warnercloud.musicplayer.Controller.ListItemController;
import com.warnercloud.musicplayer.Model.Track;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ListItemFactory {

    private ListItemFactory(){}

    public static HBox createListItem(Track track){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ListItemFactory.class.getResource("/com/warnercloud/musicplayer/Views/center-list-item.fxml"));
            HBox item = fxmlLoader.load();
            ListItemController controller = fxmlLoader.getController();
            controller.createCard(track);
            item.getProperties().put("controller", controller);
            return item;
        } catch(IOException e){
            throw new RuntimeException("Failed to create Item" , e);
        }
    }
}
