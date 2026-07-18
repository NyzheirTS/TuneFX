package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Model.PlaylistData;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class SideBarItemController {
    @FXML public HBox sideBarItemParent;
    @FXML public Label playlistLabel;
    private PlaylistData playlist;
    private Consumer<Integer> onPlaylistSelected;
    private boolean isHovered = false;

    public void createItem(PlaylistData playlist) {
        this.playlist = playlist;
        setContent();
        setHoverEffects();
        setOnClick();
    }

    private void setContent(){
        playlistLabel.setText(playlist.getTitle());
    }

    public void setOnplaylistSelected(Consumer<Integer> onPlaylistSelected) {
        this.onPlaylistSelected = onPlaylistSelected;
    }

    private void setOnClick(){
        sideBarItemParent.setOnMouseClicked((event) -> {
            if (onPlaylistSelected != null) {
                onPlaylistSelected.accept(playlist.getPlaylist_id());
            }
        });
    }

    private void setHoverEffects(){
        sideBarItemParent.setOnMouseEntered(_ -> {
            isHovered = true;
            updateBackground();
        });
        sideBarItemParent.setOnMouseExited(_ -> {
            isHovered = false;
            updateBackground();
        });
    }

    private void updateBackground() {
        if(isHovered){
            sideBarItemParent.setStyle("-fx-background-color: rgba(96,109,109,0.72)");  // Hover color
        } else {
            sideBarItemParent.setStyle("-fx-background-color: transparent"); // Default
        }
    }
}
