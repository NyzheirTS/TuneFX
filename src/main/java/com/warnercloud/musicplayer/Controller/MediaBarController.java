package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class MediaBarController {
    @FXML public HBox parentContainer;
    @FXML public ImageView albumCover;
    @FXML public VBox trackInfoContainer;
    @FXML public Label songLabel;
    @FXML public Label artistLabel;
    @FXML public VBox mediaControlContainer;
    @FXML public HBox trackBarContainer;
    @FXML public Label runtimeLabel;
    @FXML public Slider seekBar;
    @FXML public Label durationLabel;
    @FXML public HBox mediaButtonsContainer;
    @FXML public Button shuffleButton;
    @FXML public Button backButton;
    @FXML public Button pausePlayButton;
    @FXML public Button nextButton;
    @FXML public Button repeatButton;
    @FXML public VBox cushionSpacing;
    @FXML public HBox volumeControlContainer;
    @FXML public Button volumeButton;
    @FXML public Slider volumeSlider;

    private Track track;
    private FileChooser chooser = new FileChooser();


    public MediaBarController() {
        MediaService.getInstance().addTrackChangeListener(this::updateTrack);
    }

    private void updateTrack(Track track) {
        this.track = track;
        Platform.runLater(() -> {
           albumCover.setImage(track.getCover());
           artistLabel.setText(track.getArtist());
           songLabel.setText(track.getTitle());
        });
    }

    @FXML
    public void shufflePlaylistFunction(ActionEvent event) {}

    @FXML
    public void resetGoBackFunction(ActionEvent event) {
        if (MediaService.getInstance().isPlaying() || MediaService.getInstance().inProgress()){
            MediaService.getInstance().resetTrack();
        } else {
            //TODO: go back one song in que
        }
    }

    @FXML
    public void pausePlayFunction(ActionEvent event){
        if (MediaService.getInstance().isPlaying()) {
            MediaService.getInstance().pause();
        } else {
            MediaService.getInstance().play();
        }
    }

    @FXML
    public void skipTrackFunction(ActionEvent event){}

    @FXML
    public void repeatTracksFunction(ActionEvent event){
        //filler to change song testing
        chooser.setTitle("Select your music");
        chooser.setInitialDirectory(new File("C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/"));
        File file = chooser.showOpenDialog(null);

        if (file != null) {
            loadTrack(file);
        }
    }

    private void loadTrack(File file) {
        // Create Track object to hold the track information
        Track currentTrack = new Track(file.toURI().toString());
        // MediaService used to load and prepare the track
        MediaService.getInstance().loadTrack(currentTrack, this::updateTrack);
        MediaService.getInstance().play();
    }


    @FXML
    public void muteRestoreFunction(ActionEvent actionEvent) {
    }
}
