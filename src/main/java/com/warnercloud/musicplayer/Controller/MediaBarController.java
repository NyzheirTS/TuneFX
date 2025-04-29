package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import com.warnercloud.musicplayer.Utils.TimeUtils;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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
    private final FileChooser chooser = new FileChooser();
    private boolean updatingValue;
    private boolean wasPlaying;


    public MediaBarController() {
        MediaService.getInstance().addTrackChangeListener(this::updateTrack);
    }

    private void updateTrack(Track track) {
        this.track = track;
        Platform.runLater(() -> {
           albumCover.setImage(track.getCover());
           artistLabel.setText(track.getArtist());
           songLabel.setText(track.getTitle());
           durationLabel.setText(TimeUtils.formatDuration(track.getDuration()));
           initSeekBar();
        });
    }

    private void initSeekBar(){
        seekBar.setMin(0.0);
        seekBar.setMax(1.0);
        seekBar.setValue(0.0);
        seekBar.valueProperty().addListener(this::onValueInvalidated);
        seekBar.valueChangingProperty().addListener(this::onValueChangingChange);
        MediaService.getInstance().getPlayer().currentTimeProperty().addListener(this::onCurrentTimeChanges);
    }

    private void onValueInvalidated(Observable observable) {
        if (!updatingValue){
            double ms = MediaService.getInstance().getPlayer().getMedia().getDuration().toMillis() * seekBar.getValue();
            MediaService.getInstance().getPlayer().seek(Duration.millis(ms));
        }
    }

    private void onValueChangingChange(ObservableValue<? extends Boolean> observable, Boolean wasValueChanging, Boolean isValueChanging) {
        if (Boolean.TRUE.equals(isValueChanging)){
            if (MediaService.getInstance().isPlaying()){
                wasPlaying = true;
                MediaService.getInstance().getPlayer().pause();
            } else {
                wasPlaying = false;
            }
        } else if (wasPlaying) {
            MediaService.getInstance().getPlayer().play();
        }
    }

    private void onCurrentTimeChanges(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
        if (!seekBar.isValueChanging()){
            updatingValue = true;
            try {
                double value = newValue.toMillis() / MediaService.getInstance().getPlayer().getMedia().getDuration().toMillis();
                Platform.runLater(() -> {
                   runtimeLabel.setText(TimeUtils.formatDuration(Duration.millis(newValue.toMillis())));
                });
                seekBar.setValue(value);
            } finally{
                updatingValue = false;
            }
        }
    }

    @FXML
    public void shufflePlaylistFunction(ActionEvent event) {}

    @FXML
    public void resetGoBackFunction(ActionEvent event) {
        if (PlaylistNavigationService.getInstance().peekPrevious() != null) {
            Track previousTrack = PlaylistNavigationService.getInstance().playPrevious();
            loadTrack(previousTrack.getFilePath());
        } else {
            System.out.println("No Previous Track");
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
    public void skipTrackFunction(ActionEvent event){
        if (PlaylistNavigationService.getInstance().peekNext() != null) {
            Track nextTrack = PlaylistNavigationService.getInstance().playNext();
            loadTrack(nextTrack.getFilePath());
        } else {
            System.out.println("No Next Track");
        }
    }

    @FXML
    public void repeatTracksFunction(ActionEvent event){
        //filler to change song testing
        chooser.setTitle("Select your music");
        chooser.setInitialDirectory(new File("C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/"));
        File file = chooser.showOpenDialog(null);

        if (file != null) {
            loadTrack(file);
            //PlaylistNavigationService.getInstance().startPlaybackFrom();
        }
    }

    private void loadTrack(File file) {
        // Create Track object to hold the track information
        Track currentTrack = new Track(file);
        // MediaService used to load and prepare the track
        MediaService.getInstance().loadTrack(currentTrack, this::updateTrack);
        MediaService.getInstance().play();
    }


    @FXML
    public void muteRestoreFunction(ActionEvent actionEvent) {
    }
}
