package com.warnercloud.musicplayer.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warnercloud.musicplayer.Factory.ListItemFactory;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.util.List;

public class CenterViewController {
    @FXML public BorderPane parent;
    @FXML public Label trackCount;

    private final ObservableList<Track> selectedTracks = FXCollections.observableArrayList();
    private int lastSelectedIndex = -1;
    private ObservableList<HBox> items = FXCollections.observableArrayList();
    private final VirtualFlow<HBox, ?> bf = VirtualFlow.createVertical(items, this::regionCell);
    private final VirtualizedScrollPane<VirtualFlow<HBox, ?>> vf = new VirtualizedScrollPane<>(bf, ScrollPane.ScrollBarPolicy.NEVER, ScrollPane.ScrollBarPolicy.ALWAYS);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClipboardContent content = new ClipboardContent();


    public void initContainer(){
        styleVf();
        parent.setCenter(vf);
        PlaylistNavigationService.getInstance().addListFullListener(this::onTrackLoaded);
    }

    private void styleVf(){
        vf.setStyle("-fx-background-color: rgba(64, 64, 64, 0.25)");
        Rectangle clip = new Rectangle();
        clip.setArcHeight(16);
        clip.setArcWidth(16);
        clip.widthProperty().bind(vf.widthProperty());
        clip.heightProperty().bind(vf.heightProperty());
        vf.setClip(clip);
    }


    private void onTrackLoaded(List<Track> tracks){
        items.clear();
        Platform.runLater(()-> {
            items = getAllItems(tracks);
            trackCount.setText(tracks.size() + " Tracks");
        });

    }

    private ObservableList<HBox> getAllItems(List<Track> tracks) {
        ObservableList<HBox> newItems = FXCollections.observableArrayList();

        for (int i = 0; i < tracks.size(); i++) {
            Track track = tracks.get(i);
            HBox node = ListItemFactory.createListItem(track);
            ListItemController controller = (ListItemController) node.getProperties().get("controller");

            setupMouseClickHandler(node, track, controller, newItems, tracks, i);
            setupDragHandler(node, track, controller, i);
            attachContextMenu(node, track, controller, i);

            newItems.add(node);
        }

        Platform.runLater(() -> {
            items.setAll(newItems);
            bf.showAsFirst(0);
        });

        return items;
    }

    private void setupMouseClickHandler(HBox node, Track track, ListItemController controller, ObservableList<HBox> itemList, List<Track> trackList, int index) {
        node.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                return;
            }
            if (event.isControlDown()) {
                if (controller.isSelected()) {
                    controller.setSelected(false);
                    selectedTracks.remove(track);
                } else {
                    controller.setSelected(true);
                    selectedTracks.add(track);
                }
            } else if (event.isShiftDown() && lastSelectedIndex != -1) {
                applyShiftSelection(itemList, trackList, index);
            } else {
                selectOnlyThis(itemList, controller, track);
            }

            lastSelectedIndex = index;
        });
    }

    private void applyShiftSelection(ObservableList<HBox> items, List<Track> tracks, int currentIndex) {
        clearAllSelections(items);

        int from = Math.min(lastSelectedIndex, currentIndex);
        int to = Math.max(lastSelectedIndex, currentIndex);

        for (int i = from; i <= to; i++) {
            Track t = tracks.get(i);
            HBox n = items.get(i);
            ListItemController c = (ListItemController) n.getProperties().get("controller");
            c.setSelected(true);
            selectedTracks.add(t);
        }
    }

    private void selectOnlyThis(ObservableList<HBox> items, ListItemController controller, Track track) {
        clearAllSelections(items);
        controller.setSelected(true);
        selectedTracks.add(track);
    }

    private void clearAllSelections(ObservableList<HBox> items) {
        for (HBox item : items) {
            ListItemController c = (ListItemController) item.getProperties().get("controller");
            c.setSelected(false);
        }
        selectedTracks.clear();
    }

    private void setupDragHandler(HBox node, Track track, ListItemController controller, int index) {
        node.setOnDragDetected(event -> {
            if (!selectedTracks.contains(track)) {
                clearAllSelections(items);
                controller.setSelected(true);
                selectedTracks.add(track);
                lastSelectedIndex = index;
            }
            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
            db.setContent(getSelectedClipboardContent());
            event.consume();
        });
    }

    private ClipboardContent getSelectedClipboardContent() {
        try {
            List<String> uuids = selectedTracks.stream()
                    .map(Track::getUUID)
                    .toList();
            String json = objectMapper.writeValueAsString(uuids);
            content.clear();
            content.putString(json);
            return content;
        } catch (Exception _) {}
        return null;
    }


    private void attachContextMenu(HBox node, Track track, ListItemController controller, int index) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem Copy = new MenuItem("Copy");
        Copy.setOnAction(e -> {
            ClipboardContent content = getSelectedClipboardContent();
            System.out.println(content);
        });
        contextMenu.getItems().addAll(Copy);
        node.setOnContextMenuRequested(e -> {
            contextMenu.show(node, e.getScreenX(), e.getScreenY());
        });
    }


    public void clearSelection() {
        for (HBox item : items) {
            ListItemController c = (ListItemController) item.getProperties().get("controller");
            c.setSelected(false);
        }
        selectedTracks.clear();
        lastSelectedIndex = -1;
    }

    public VirtualizedScrollPane<VirtualFlow<HBox, ?>> getVf() {
        return vf;
    }


    private Cell<HBox, ?> regionCell(HBox box){return Cell.wrapNode(box);}

}
