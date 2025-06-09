package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import com.warnercloud.musicplayer.Utils.TimeUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ListItemController {
    @FXML public ImageView coverArtImg;
    @FXML public Label songTitle;
    @FXML public Label artistLabel;
    @FXML public Label albumLabel;
    @FXML public Label dateLabel;
    @FXML public Label countLabel;
    @FXML public Label durationLabel;
    @FXML public Button playButton;
    @FXML public HBox parent;
    private Track track;


    private boolean isSelected = false;
    private boolean isHovered = false;


    public void createCard(Track track) {
        this.track = track;
        setLabels();
        setHoverEffects();
    }

    private void setLabels(){
        coverArtImg.setImage(new Image(track.getCover(), 93, 93, true , true, true));
        songTitle.setText(track.getTitle());
        artistLabel.setText(track.getArtist());
        albumLabel.setText(track.getAlbum());
        countLabel.setText(String.valueOf(track.getPlayCount()));
        durationLabel.setText(TimeUtils.formatDuration(track.getDuration()));
    }

    private void setHoverEffects(){
        parent.setOnMouseEntered(e -> {
            isHovered = true;
            playButton.setVisible(true);
            updateBackground();
        });
        parent.setOnMouseExited(e -> {
            isHovered = false;
            playButton.setVisible(false);
            updateBackground();
        });
    }


    @FXML
    public void startPlayback(ActionEvent actionEvent) {
        System.out.println("Starting playback: " + track.getTitle() + " - " + track.getArtist() + " - " + track.getUUID());
        PlaylistNavigationService.getInstance().startPlaybackFrom(track);
    }

    private void updateBackground() {
        if (isSelected) {
            parent.setStyle("-fx-background-color: #3399FF");  // Selected blue background
        } else if (isHovered) {
            parent.setStyle("-fx-background-color: rgba(96,109,109,0.72)");  // Hover color
        } else {
            parent.setStyle("-fx-background-color: transparent");  // Default
        }
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        updateBackground();
    }

    public boolean isSelected() {
        return isSelected;
    }

}
