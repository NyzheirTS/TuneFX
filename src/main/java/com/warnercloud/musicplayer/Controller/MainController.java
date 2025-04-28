package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Factory.CenterViewFactory;
import com.warnercloud.musicplayer.Factory.MediaBarFactory;
import com.warnercloud.musicplayer.Factory.SideBarFactory;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML public BorderPane root;
    private FileChooser chooser = new FileChooser();
    private TestClassChangeListener changeListener = new TestClassChangeListener();
    private final HBox mediaBar = MediaBarFactory.createMediaBar();
    private final VBox sideBar = SideBarFactory.createSideBar();
    private final BorderPane centerView = CenterViewFactory.createMediaBar();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize FileChooser for loading music
        chooser.setTitle("Select your music");
        chooser.setInitialDirectory(new File("C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/"));
        File file = chooser.showOpenDialog(null);

        if (file != null) {
            loadTrack(file);
        }
        initUI();
    }

    private void loadTrack(File file) {
        // Create Track object to hold the track information
        Track currentTrack = new Track(file.toURI().toString());
        // MediaService used to load and prepare the track
        MediaService.getInstance().loadTrack(currentTrack, this::onMetadataReady);
        MediaService.getInstance().play();
    }

    // Called when the metadata is ready
    private void onMetadataReady(Track track) {
        // Update the UI based on track's metadata

    }


    private void initUI(){
        Platform.runLater(() -> {
            root.setBottom(mediaBar);
            root.setLeft(sideBar);
            root.setCenter(centerView);
        });
    }



    // Play/Pause button functionality
    public void play(ActionEvent actionEvent) {
        if (MediaService.getInstance().isPlaying()) {
            MediaService.getInstance().pause();
        } else {
            MediaService.getInstance().play();
        }
    }

    // Stop button functionality
    public void stop(ActionEvent actionEvent) {
        MediaService.getInstance().stop();
    }

    public void newTrack(ActionEvent actionEvent) {
        File file = chooser.showOpenDialog(null);

        if (file != null) {
            loadTrack(file);
        }
    }

    public void printTitle(ActionEvent actionEvent) {
        System.out.println(MediaService.getInstance().getPlayer().getMedia().getMetadata().get("title").toString());
    }
}