package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.IOException;

public class SideBarController {
    @FXML public VBox sidebarContainer;
    @FXML public Button createPlaylist;
    @FXML public Button homeButton;
    @FXML public BorderPane playlistContainer;


    private ObservableList<HBox> items = FXCollections.observableArrayList();
    private final VirtualFlow<HBox, ?> bf = VirtualFlow.createVertical(items, this::regionCell);
    private final VirtualizedScrollPane<VirtualFlow<HBox, ?>> vf = new VirtualizedScrollPane<>(bf, ScrollPane.ScrollBarPolicy.NEVER, ScrollPane.ScrollBarPolicy.ALWAYS);
    private final Stage popup = new Stage();
    private boolean popupOpened = false;

    public void initSideBar() {
        playlistContainer.setCenter(vf);
        initPopup();
    }

    private void initPopup(){
        popup.initOwner(App.getMainStage());
        popup.initStyle(StageStyle.UTILITY);
        popup.initModality(Modality.WINDOW_MODAL);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SideBarController.class.getResource("/com/warnercloud/musicplayer/Views/playlist-creation-form.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            popup.setTitle("Form");
            popup.setScene(scene);
            popup.setResizable(false);
            popup.setWidth(200);
            popup.setHeight(200);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML
    public void createPlaylists(ActionEvent actionEvent) {
        System.out.println("Creating Playlists");
        popup.show();
    }

    @FXML
    public void goHome(ActionEvent actionEvent) {
        System.out.println("Go Home");
    }

    private Cell<HBox, ?> regionCell(HBox box) {return Cell.wrapNode(box);}
}
