package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Factory.CenterViewFactory;
import com.warnercloud.musicplayer.Factory.MediaBarFactory;
import com.warnercloud.musicplayer.Factory.SideBarFactory;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML public BorderPane root;
    private final FileChooser chooser = new FileChooser();
    private TestClassChangeListener changeListener = new TestClassChangeListener();
    private final File playlistFile = new File("C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/");
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
            //loadTrack(file);
        }
        getPlaylists(playlistFile, () -> loadFirstTrack(PlaylistNavigationService.getInstance().playNext().getFilePath()));
        initUI();
    }

    private void loadFirstTrack(File file) {
        // Create Track object to hold the track information
        Track currentTrack = new Track(file, 0);
        // MediaService used to load and prepare the track
        MediaService.getInstance().loadTrack(currentTrack, this::onMetadataReady);
        MediaService.getInstance().play();
    }

    // Called when the metadata is ready
    private void onMetadataReady(Track track) {

    }

    private void initUI(){
        Platform.runLater(() -> {
            root.setBottom(mediaBar);
            root.setLeft(sideBar);
            root.setCenter(centerView);
        });
    }

    private void getPlaylists(File playlistFile, Runnable callback) {
        List<Track> trackList = new ArrayList<>();
        int index = -1;
        for (final File fileEntry : Objects.requireNonNull(playlistFile.listFiles())) {
            if (fileEntry.isDirectory()) {
                getPlaylists(fileEntry, () -> {});
            } else {
                trackList.add(new Track(fileEntry, index++));
            }
        }
        PlaylistNavigationService.getInstance().setTracks(trackList);
        PlaylistNavigationService.getInstance().getAllTracks().forEach(track -> {System.out.println("Path: " + track.getFilePath() + ", Index: " + track.getIndex());});
        callback.run();
    }

}