package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import com.warnercloud.musicplayer.Utils.ImageCache;
import com.warnercloud.musicplayer.Utils.TimeUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
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
    @FXML public ImageView playingGif;
    private Track track;
    private static final String BASE_IMAGE_URL = "https://api.warnercloud.com/api/thumbnail/";


    private boolean isSelected = false;
    private boolean isHovered = false;
    private boolean isPlaying = false;
    private ChangeListener<Boolean> playingListener;


    public void createCard(Track track) {
        dispose();
        this.track = track;
        isSelected = false;
        isHovered = false;
        setLabels();
        setHoverEffects();
    }

    private void setLabels(){
        coverArtImg.setImage(ImageCache.get(BASE_IMAGE_URL + track.getTrack_id()));
        songTitle.setText(track.getTitle());
        artistLabel.setText(track.getArtist_name());
        albumLabel.setText(track.getAlbum_title());
        countLabel.textProperty().bind(track.playCountProperty().asString());
        durationLabel.setText(TimeUtils.formatDuration(track.getDuration()));
        dateLabel.setText(track.getDate_created().toString());
        isPlaying = track.isPlaying();
        playingListener = (_, _, newValue) -> {
            isPlaying = newValue;
            updateBackground();
        };
        track.playingProperty().addListener(playingListener);
    }

    private void setHoverEffects(){
        parent.setOnMouseEntered(_ -> {
            isHovered = true;
            playButton.setVisible(true);
            updateBackground();
        });
        parent.setOnMouseExited(_ -> {
            isHovered = false;
            playButton.setVisible(false);
            updateBackground();
        });
    }


    @FXML
    public void startPlayback(ActionEvent actionEvent) {
        System.out.println("Starting playback: " + track.getTitle() + " - " + track.getArtist_name() + " - " + track.getTrack_id());
        PlaylistNavigationService.getInstance().startPlaybackFrom(track);
    }

    private void updateBackground() {
        if (isSelected) {
            parent.setStyle("-fx-background-color: #3399FF");  // Selected blue background
        } else if (isHovered) {
            parent.setStyle("-fx-background-color: rgba(96,109,109,0.72)");  // Hover color
        } else if (isPlaying) {
            parent.setStyle("-fx-background-color: transparent");
            songTitle.setStyle("-fx-text-fill: #1D6E85");
            playingGif.setVisible(true);// playing color
        } else {
            playingGif.setVisible(false);
            songTitle.setStyle("-fx-text-fill: white");
            parent.setStyle("-fx-background-color: transparent");
            // Default
        }
    }

    // dispose of listeners when discarded by flowless
    public void dispose() {
        countLabel.textProperty().unbind();
        if (track != null && playingListener != null) {
            track.playingProperty().removeListener(playingListener);
        }
        playingListener = null;
        track = null;
    }
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        updateBackground();
    }

    public boolean isSelected() {
        return isSelected;
    }

}
