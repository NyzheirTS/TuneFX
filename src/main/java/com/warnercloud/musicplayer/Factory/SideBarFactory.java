package com.warnercloud.musicplayer.Factory;

import com.warnercloud.musicplayer.Controller.SideBarController;
import com.warnercloud.musicplayer.Controller.SideBarItemController;
import com.warnercloud.musicplayer.Model.PlaylistData;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SideBarFactory {
    private SideBarFactory(){}

    public static VBox createSideBar() {
        try {
            FXMLLoader loader = new FXMLLoader(SideBarFactory.class.getResource("/com/warnercloud/musicplayer/Views/side-bar.fxml"));
            VBox root = loader.load();

            SideBarController controller = loader.getController();
            root.getProperties().put("controller", controller);
            controller.initSideBar();
            return root;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static HBox createSideBarItems(PlaylistData playlistData) {
        try {
            FXMLLoader loader = new FXMLLoader(SideBarFactory.class.getResource("/com/warnercloud/musicplayer/Views/side-bar-item.fxml"));
            HBox root = loader.load();

            SideBarItemController controller = loader.getController();
            root.getProperties().put("controller", controller);
            controller.createItem(playlistData);
            return root;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
