package com.warnercloud.musicplayer.Factory;

import com.warnercloud.musicplayer.Controller.MediaBarController;
import com.warnercloud.musicplayer.Controller.SideBarController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SideBarFactory {
    private SideBarFactory(){}

    public static VBox createSideBar() {
        try {
            FXMLLoader loader = new FXMLLoader(MediaBarFactory.class.getResource("/com/warnercloud/musicplayer/Views/side-bar.fxml"));
            VBox root = loader.load();

            SideBarController controller = loader.getController();
            root.getProperties().put("controller", controller);
            return root;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
