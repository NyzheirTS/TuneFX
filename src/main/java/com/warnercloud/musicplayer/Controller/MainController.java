package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Factory.CenterViewFactory;
import com.warnercloud.musicplayer.Factory.MediaBarFactory;
import com.warnercloud.musicplayer.Factory.SideBarFactory;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Service.MetaDataService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import com.warnercloud.musicplayer.Utils.AppJsonManager;
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

    private final AppJsonManager appJsonManager = new AppJsonManager();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize FileChooser for loading music
        chooser.setTitle("Select your music");
        chooser.setInitialDirectory(new File("C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/"));
        File file = chooser.showOpenDialog(null);

        if (file != null) {
            //loadTrack(file);
        }
        getPlaylists(playlistFile, () -> loadFirstTrack(PlaylistNavigationService.getInstance().playNext()));
        initUI();
    }

    private void loadFirstTrack(Track track) {
        // MediaService used to load and prepare the track
        MediaService.getInstance().loadTrack(track);
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
        // Sync json manager with files in user music directory
        appJsonManager.syncWithDirectory(playlistFile);
        // Load entries from the JSON file after done syncing files
        List<AppJsonManager.MasterCRUD> entries = appJsonManager.getAllEntries();
        // Build Track objects from synced entries to be sent to playlist manager
        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            File file = new File(entries.get(i).getFilePath());
            if (file.exists()) {
                tracks.add(new Track(file.getPath(), entries.get(i).getUuid()));
            }
        }
        // Extract metadata and update playlist service
        MetaDataService service = new MetaDataService();
        service.extractBasicMetadataAsync(tracks);
        PlaylistNavigationService.getInstance().setTracks(tracks);
        // Debug
        //PlaylistNavigationService.getInstance().getAllTracks().forEach(track -> System.out.println("Path: " + track.getFilePath() + ", UUID: " + track.getIndex()));
        callback.run();
        service.shutdown();
    }

}