package com.warnercloud.musicplayer.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PlaylistListItemController {
    @FXML public HBox parent;
    @FXML public HBox imageTitleContainer;
    @FXML public ImageView playlistIcon;
    @FXML public Label labelWithIcon;
    @FXML public HBox labelOnlyContainer;
    @FXML public Label labelOnly;
    @FXML public ImageView pinIcon;

    private boolean isPinned = false;


    public void createCard(String title, String img){
        if (img.equals("null")){
            Platform.runLater(() -> {
               imageTitleContainer.setVisible(false);
               labelOnly.setText(title);
            });
        } else {
            Platform.runLater(() -> {
                labelOnlyContainer.setVisible(false);
                playlistIcon.setImage(new Image(img));
                labelWithIcon.setText(title);
            });
        }
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
