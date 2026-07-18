package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.FXCustomSkins.CustomSliderBar;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import com.warnercloud.musicplayer.Utils.TimeUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Objects;

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
    @FXML public ImageView shuffleImg;
    @FXML public Button queueButton;
    @FXML public ImageView speakerIcon;
    @FXML public ImageView pausePlayIMG;

    private boolean wasPlaying;
    private boolean updatingValue;
    private boolean isSeeking = false;
    private Timeline timeline;
    private long lastSeekTime = 0;
    private long lastVolumeTime = 0;// For throttling seeks
    private double lastVolume = 0;
    private long volume = 100;
    private boolean shuffled = false;
    private boolean isPlaying = false;
    private static final String BASE_IMAGE_URL = "https://api.warnercloud.com/api/image/";

    public MediaBarController() {
        MediaService.getInstance().addTrackChangeListener(this::updateTrack);
    }

    public void initUI(){
        initSeekBar();
        initVolumeControls();
        updateShuffleIcon(PlaylistNavigationService.getInstance().isIsShuffled());
    }

    private void updateTrack(Track track) {
        Platform.runLater(() -> {
            albumCover.setImage(new Image(BASE_IMAGE_URL+track.getTrack_id(), 93, 93, true, true, true));
            artistLabel.setText(track.getArtist_name());
            songLabel.setText(track.getTitle());
            durationLabel.setText(TimeUtils.formatDuration(track.getDuration()));
            // Start playback immediately
            MediaService.getInstance().play();
            updatePausePlayIcon(true);
            // Start polling for progress updates if playback has started
            startPlaybackPolling();
        });
    }

    private void initSeekBar() {
        seekBar.setSkin(new CustomSliderBar(seekBar));
        seekBar.setMin(0.0);
        seekBar.setMax(1.0);
        seekBar.setValue(0.0);
        seekBar.valueProperty().addListener(this::seekBaronValueInvalidated);
        seekBar.valueChangingProperty().addListener(this::seekBaronValueChangingChange);
    }



    private void initVolumeControls() {
        volumeSlider.setSkin(new CustomSliderBar(volumeSlider));
        volumeSlider.setMin(0.00);
        volumeSlider.setMax(100.00);
        volumeSlider.setValue(volume);
        volumeSliderValueProperty();
        volumeSliderSetOnScroll();

    }

    private void volumeSliderValueProperty() {
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSeekTime < 200) {
                return;
            }
            lastVolumeTime = currentTime;
            volume = newValue.intValue();
            MediaService.getInstance().setCurrentVolume(newValue.intValue());
        });
    }

    private void volumeSliderSetOnScroll(){
        volumeSlider.setOnScroll(scrollEvent -> {
            volume -= (long) (scrollEvent.getDeltaY() * -0.25 );
            volume = Math.round(volume / 10.0) * 10;
            volume = Math.max(0, Math.min(100, volume));

            System.out.println("volume " + volume);

            MediaService.getInstance().setCurrentVolume((int) volume);
            Platform.runLater(() -> volumeSlider.setValue(volume));


            scrollEvent.consume();
        });
    }

    private void seekBaronValueInvalidated(Observable obs){
        if (!updatingValue){
            MediaService media = MediaService.getInstance();
            double ms = media.getMediaDuration() * seekBar.getValue();
            media.seek((long) ms);
        }
    }

    private void seekBaronValueChangingChange(ObservableValue<? extends Boolean> obs, Boolean oldValue, Boolean newValue) {
        if (Boolean.TRUE.equals(newValue)) {
            if (MediaService.getInstance().isPlaying()) {
                wasPlaying = true;
                MediaService.getInstance().pause();
            } else {
                wasPlaying = false;
            }
        } else if (wasPlaying) {
            MediaService.getInstance().play();
        }
    }

    private void startPlaybackPolling() {
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(250), event -> {
            if (!seekBar.isValueChanging()) {//&& MediaService.getInstance().isPlaying()) {
                updatingValue = true;
                long currentTime = MediaService.getInstance().getCurrentTime();
                long totalDuration = MediaService.getInstance().getMediaDuration();
                try {
                    double progress = (double) currentTime / totalDuration;
                    seekBar.setValue(progress);
                    runtimeLabel.setText(TimeUtils.formatDuration((int) currentTime));
                } finally {
                    updatingValue = false;
                }
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    @FXML
    public void shufflePlaylistFunction(ActionEvent event) {
        boolean newShuffleState = !PlaylistNavigationService.getInstance().isIsShuffled();
        PlaylistNavigationService.getInstance().setIsShuffled(newShuffleState);
        updateShuffleIcon(newShuffleState);
    }

    @FXML
    public void pausePlayFunction(ActionEvent event) {
        if (MediaService.getInstance().isPlaying()) {
            MediaService.getInstance().pause();
            updatePausePlayIcon(false);
        } else {
            MediaService.getInstance().play();
            updatePausePlayIcon(true);
        }
    }

    @FXML
    public void resetGoBackFunction(ActionEvent event) {
        if (PlaylistNavigationService.getInstance().peekPrevious() != null) {
            Track previousTrack = PlaylistNavigationService.getInstance().playPrevious();
            loadTrack(previousTrack);
        } else {
            System.out.println("No Previous Track");
        }
    }

    @FXML
    public void skipTrackFunction(ActionEvent event) {
        if (PlaylistNavigationService.getInstance().peekNext() != null) {
            Track nextTrack = PlaylistNavigationService.getInstance().playNext();
            loadTrack(nextTrack);
        } else {
            System.out.println("No Next Track");
        }
    }

    @FXML
    public void repeatTracksFunction(ActionEvent event) {
    }

    private void loadTrack(Track track) {
        // MediaService used to load and prepare the track
        MediaService.getInstance().loadTrack(track);
        updateTrack(track);
        Platform.runLater(this::startPlaybackPolling);
    }


    @FXML
    public void muteRestoreFunction(ActionEvent actionEvent) {
        if (!MediaService.getInstance().isMuted()) {
            lastVolume = volumeSlider.getValue();
            MediaService.getInstance().mute();
            Platform.runLater(() -> volumeSlider.setValue(0));
        } else {
            MediaService.getInstance().unmute();
            Platform.runLater(() -> volumeSlider.setValue(lastVolume));
        }
    }
    private void updatePausePlayIcon(boolean isPlaying) {
        String iconPath = isPlaying ? "/com/warnercloud/musicplayer/Assets/pause.png" : "/com/warnercloud/musicplayer/Assets/play.png";
        Platform.runLater(() -> pausePlayIMG.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)))));
        this.isPlaying = isPlaying;
    }

    private void updateShuffleIcon(boolean isShuffled) {
        String iconPath = isShuffled ? "/com/warnercloud/musicplayer/Assets/ShuffleOn.png" : "/com/warnercloud/musicplayer/Assets/Shuffle.png";

        Platform.runLater(() -> shuffleImg.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)))));

        this.shuffled = isShuffled; // keep local state in sync
    }

    @FXML
    public void showQueue(ActionEvent actionEvent) {
    }
}
