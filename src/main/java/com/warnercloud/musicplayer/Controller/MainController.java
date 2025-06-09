package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Factory.CenterViewFactory;
import com.warnercloud.musicplayer.Factory.MediaBarFactory;
import com.warnercloud.musicplayer.Factory.SideBarFactory;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Service.MetaDataService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import com.warnercloud.musicplayer.Utils.MasterJsonManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML public BorderPane root;
    private final FileChooser chooser = new FileChooser();
    private final File playlistFile = new File("C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/");
    private final HBox mediaBar = MediaBarFactory.createMediaBar();
    private final VBox sideBar = SideBarFactory.createSideBar();
    private final BorderPane centerView = CenterViewFactory.createMediaBar();
    // After creating the centerView BorderPane:
    CenterViewController centerViewController = (CenterViewController) centerView.getProperties().get("controller");

    private final MasterJsonManager appJsonManager = new MasterJsonManager();
    private final Stage startingStage = new Stage();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*// Initialize FileChooser for loading music
        chooser.setTitle("Select your music");
        chooser.setInitialDirectory(new File("C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/"));
        File file = chooser.showOpenDialog(null);*/
        getPlaylists(playlistFile, () -> {});
        initUI();
        setROot();
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
        List<MasterJsonManager.MasterCRUD> entries = appJsonManager.getAllEntries();
        // Build Track objects from synced entries to be sent to playlist manager
        List<Track> tracks = new ArrayList<>();
        for (MasterJsonManager.MasterCRUD entry : entries) {
            File file = new File(entry.getFilePath());
            if (file.exists()) {
                tracks.add(new Track(file.getPath(), entry.getUuid(), entry.getPlayCount()));
            }
        }
        // Extract metadata and update playlist service
        MetaDataService service = new MetaDataService();
        service.extractBasicMetadataAsync(tracks, () -> {
            PlaylistNavigationService.getInstance().loadPlaylist(tracks);
            // Debug
            //PlaylistNavigationService.getInstance().getAllTracks().forEach(track -> System.out.println("Path: " + track.getFilePath() + ", UUID: " + track.getUUID()));
            callback.run();
            service.shutdown();
        });
    }

    private void setROot(){
        root.setOnMouseClicked(event -> {
            javafx.scene.Node target = (javafx.scene.Node) event.getTarget();
            VirtualizedScrollPane<?> listPane = centerViewController.getVf();


            boolean clickedInList = false;
            while (target != null) {
                if (target == listPane) {
                    clickedInList = true;
                    break;
                }
                target = target.getParent();
            }

            if (!clickedInList) {
                if (centerViewController != null) {
                    centerViewController.clearSelection();
                }
            }
        });


    }

}