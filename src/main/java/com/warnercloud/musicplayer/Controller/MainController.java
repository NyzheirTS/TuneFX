package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Factory.*;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.APIService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import com.warnercloud.musicplayer.Utils.JsonUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class MainController implements Initializable {

    @FXML public BorderPane root;
    @FXML public BorderPane popup_root;
    @FXML public VBox form_container;

    private final HBox mediaBar = MediaBarFactory.createMediaBar();
    private final VBox sideBar = SideBarFactory.createSideBar();
    private final BorderPane centerView = CenterViewFactory.createMediaBar();
    private final CenterViewController centerViewController = (CenterViewController) Objects.requireNonNull(centerView).getProperties().get("controller");
    private final SideBarController sideBarController = (SideBarController) Objects.requireNonNull(sideBar).getProperties().get("controller");


    private final GaussianBlur blur = new GaussianBlur(10);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sideBarController.setCenterViewController(centerViewController);
        initUI();
    }

    private void initUI(){
        Platform.runLater(() -> {
            root.setBottom(mediaBar);
            root.setLeft(sideBar);
            root.setCenter(centerView);
        });
    }


    /*private void getPlaylists(String url) throws IOException {

        String json = APIService.getInstance().apiCall(url);
        System.out.println(json);

        Track[] trackArray = JsonUtil.fromJson(json, Track[].class);

        List<Track> tracks = new ArrayList<>(Arrays.asList(trackArray));

        PlaylistNavigationService.getInstance().initPlaylistService(tracks);
    }*/

}