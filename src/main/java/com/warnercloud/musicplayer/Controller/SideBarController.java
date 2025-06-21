package com.warnercloud.musicplayer.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class SideBarController {
    @FXML public VBox sidebarContainer;
    @FXML public Button createPlaylist;
    @FXML public Button homeButton;
    @FXML public BorderPane playlistContainer;


    private ObservableList<HBox> items = FXCollections.observableArrayList();
    private final VirtualFlow<HBox, ?> bf = VirtualFlow.createVertical(items, this::regionCell);
    private final VirtualizedScrollPane<VirtualFlow<HBox, ?>> vf = new VirtualizedScrollPane<>(bf, ScrollPane.ScrollBarPolicy.AS_NEEDED, ScrollPane.ScrollBarPolicy.AS_NEEDED);
    private MainController mainController;



    public void initSideBar() {
        playlistContainer.setCenter(vf);
    }


    @FXML
    public void createPlaylists(ActionEvent actionEvent) {
        if (mainController != null) {
            mainController.showPopupForm();
        }

    }

    @FXML
    public void goHome(ActionEvent actionEvent) {
        System.out.println("Go Home");
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private Cell<HBox, ?> regionCell(HBox box) {return Cell.wrapNode(box);}
}
