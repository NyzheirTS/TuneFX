package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Factory.*;
import com.warnercloud.musicplayer.Model.Master;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MetaDataService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import com.warnercloud.musicplayer.Utils.MasterJsonManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML public BorderPane root;
    @FXML public BorderPane popup_root;
    @FXML public VBox form_container;

    private final GaussianBlur blur = new GaussianBlur(10);
    private final File playlistFile = new File("C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/");//"C:/Users/eshas/OneDrive/Desktop/testplaylist/");  //"C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/"
    private final HBox mediaBar = MediaBarFactory.createMediaBar();
    private final VBox sideBar = SideBarFactory.createSideBar();
    private final BorderPane centerView = CenterViewFactory.createMediaBar();
    private final MasterJsonManager appJsonManager = new MasterJsonManager();
    private final VBox createPlaylistForm = PlaylistCreationFormFactory.createForm();
    private final VBox queueListView = QueueListFactory.createQueueList();
    private final CenterViewController centerViewController = (CenterViewController) Objects.requireNonNull(centerView).getProperties().get("controller");
    private final PlaylistCreationFormController formController = (PlaylistCreationFormController) Objects.requireNonNull(createPlaylistForm).getProperties().get("controller");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getPlaylists(playlistFile, () -> {});
        initUI();
        setROot();
    }

    private void initUI(){
        Platform.runLater(() -> {
            root.setBottom(mediaBar);
            root.setLeft(sideBar);
            root.setCenter(centerView);
            root.setRight(queueListView);
            form_container.getChildren().addAll(createPlaylistForm);
            popup_root.setVisible(false);
            popup_root.setManaged(false);

            SideBarController sideBarController = (SideBarController) Objects.requireNonNull(sideBar).getProperties().get("controller");
            formController.setSidebarController(sideBarController);
            formController.closeForm(this::hidePopupForm);
            sideBarController.setMainController(this);
        });
    }

    public void showPopupForm(){
        popup_root.setVisible(true);
        popup_root.setManaged(true);
        root.setEffect(blur);
    }

    public void hidePopupForm(){
        popup_root.setVisible(false);
        popup_root.setManaged(false);
        root.setEffect(null);
    }

    private void getPlaylists(File playlistFile, Runnable callback) {
        // Sync json manager with files in user music directory
        appJsonManager.syncWithDirectory(playlistFile);
        // Load entries from the JSON file after done syncing files
        List<Master> entries = appJsonManager.getAllEntries();
        // Build Track objects from synced entries to be sent to playlist manager
        List<Track> tracks = new ArrayList<>();
        for (Master entry : entries) {
            File file = new File(entry.getFilePath());
            if (file.exists()) {
                tracks.add(new Track(file.getPath(), entry.getUuid(), entry.getPlayCount(), entry.getDate()));
            }
        }
        // Extract metadata and update playlist service
        MetaDataService service = new MetaDataService();
        service.extractBasicMetadataAsync(tracks, () -> {
            PlaylistNavigationService.getInstance().initPlaylistService(tracks);
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
                centerViewController.clearSelection();
            }
        });

        popup_root.setOnMouseClicked(event -> {
            javafx.scene.Node target = (javafx.scene.Node) event.getTarget();
            while (target != null) {
                if (target == form_container) {
                    return;
                }
                target = target.getParent();
            }
            hidePopupForm();
            event.consume();
        });

    }



}