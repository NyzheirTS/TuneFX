package com.warnercloud.musicplayer.Factory;

import com.warnercloud.musicplayer.Controller.PlaylistListItemController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PlaylistListItemFactory {

    private PlaylistListItemFactory(){}

    public static HBox createPlaylistListItem(String title, String img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(PlaylistListItemFactory.class.getResource("/com/warnercloud/musicplayer/Views/playlist-list-item.fxml"));
            HBox item = fxmlLoader.load();
            PlaylistListItemController controller = fxmlLoader.getController();
            controller.createCard(title, img);
            item.getProperties().put("controller", controller);
            return item;
        } catch (IOException e){
            throw new RuntimeException("Failed to create Playlist Item", e);
        }
    }
}
