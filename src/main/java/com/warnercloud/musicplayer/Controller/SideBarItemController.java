package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Model.PlaylistData;
import com.warnercloud.musicplayer.Utils.CustomConsumers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class SideBarItemController {
    @FXML public HBox sideBarItemParent;
    @FXML public Label playlistLabel;
    private PlaylistData playlist;
    private CustomConsumers.TripleConsumer<Integer, String, String> onPlaylistSelected;
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

    public void setOnplaylistSelected(CustomConsumers.TripleConsumer<Integer, String, String> onPlaylistSelected) {
        this.onPlaylistSelected = onPlaylistSelected;
    }

    private void setOnClick(){
        sideBarItemParent.setOnMouseClicked((event) -> {
            if (onPlaylistSelected != null) {
                onPlaylistSelected.accept(playlist.getPlaylist_id(), playlist.getTitle(), playlist.getDescription());
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
