package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Factory.ListItemFactory;
import com.warnercloud.musicplayer.Factory.SideBarFactory;
import com.warnercloud.musicplayer.Model.PlaylistData;
import com.warnercloud.musicplayer.Service.APIService;
import com.warnercloud.musicplayer.Utils.JsonUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.util.Arrays;
import java.util.List;

public class SideBarController {
    @FXML public VBox sidebarContainer;

    private ObservableList<PlaylistData> currentPlaylists = FXCollections.observableArrayList();
    private ObservableList<HBox> items = FXCollections.observableArrayList();
    private final VirtualFlow<HBox, ?> bf = VirtualFlow.createVertical(items, this::regionCell);
    private final VirtualizedScrollPane<VirtualFlow<HBox, ?>> vf = new VirtualizedScrollPane<>(bf, ScrollPane.ScrollBarPolicy.NEVER, ScrollPane.ScrollBarPolicy.AS_NEEDED);
    private CenterViewController centerViewController;


    public void initSideBar(){
        VBox.setVgrow(vf, Priority.ALWAYS);
        sidebarContainer.getChildren().add(vf);
        loadPlaylists();
    };

    private void loadPlaylists(){
        Task<List<PlaylistData>> task = new Task<List<PlaylistData>>() {
            @Override
            protected List<PlaylistData> call() throws Exception {
                String json = APIService.getInstance().apiCall("https://api.warnercloud.com/api/playlists");
                PlaylistData[] playlists = JsonUtil.fromJson(json, PlaylistData[].class);
                return List.of(playlists);
            }
        };

        task.setOnSucceeded(e -> {
            currentPlaylists.setAll(task.getValue());
            createAllTrackNodes();
        });

        new Thread(task).start();
    }


    private void createAllTrackNodes(){
        items.clear();

        for (PlaylistData playlist : currentPlaylists) {
            HBox node = SideBarFactory.createSideBarItems(playlist);
            SideBarItemController controller = (SideBarItemController) node.getProperties().get("controller");
            controller.setOnplaylistSelected(centerViewController::loadPlaylist);

            items.add(node);
        }
        bf.showAsFirst(0);
    }






    private Cell<HBox, ?> regionCell(HBox box) {return Cell.wrapNode(box);}
    public void setCenterViewController(CenterViewController centerViewController) {
        this.centerViewController = centerViewController;
    }
}
