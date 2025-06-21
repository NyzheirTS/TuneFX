package com.warnercloud.musicplayer.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warnercloud.musicplayer.Factory.ListItemFactory;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.util.List;

public class CenterViewController {
    @FXML public BorderPane parent;
    @FXML public Label trackCount;
    @FXML public TextField searchBox;

    private final ObservableList<Track> selectedTracks = FXCollections.observableArrayList();
    private int lastSelectedIndex = -1;
    private List<Track> lastFilteredPlaylistTracks = List.of();
    private final ObservableList<HBox> allTrackNodes = FXCollections.observableArrayList();
    private final ObservableList<Track> allTracks = FXCollections.observableArrayList();
    private final ObservableList<HBox> visibleTrackNodes = FXCollections.observableArrayList();
    private final VirtualFlow<HBox, ?> bf = VirtualFlow.createVertical(visibleTrackNodes, this::regionCell);
    private final VirtualizedScrollPane<VirtualFlow<HBox, ?>> vf = new VirtualizedScrollPane<>(bf, ScrollPane.ScrollBarPolicy.NEVER, ScrollPane.ScrollBarPolicy.ALWAYS);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClipboardContent content = new ClipboardContent();
    private final PauseTransition pauseTransition = new PauseTransition(Duration.millis(250));  //debounce timer
    private static final String CONTROLLER = "controller";


    public void initContainer() {
        styleVf();
        parent.setCenter(vf);
        PlaylistNavigationService.getInstance().addListFullListener(this::initTracks);
        PlaylistNavigationService.getInstance().addTrackFilterListener(this::onPlaylistFilterUpdated);
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.stop();
            pauseTransition.setOnFinished(event -> filterVisibleTracks(newValue));
            pauseTransition.playFromStart();
        });
    }

    private void styleVf() {
        vf.setStyle("-fx-background-color: rgba(64, 64, 64, 0.25)");
        Rectangle clip = new Rectangle();
        clip.setArcHeight(16);
        clip.setArcWidth(16);
        clip.widthProperty().bind(vf.widthProperty());
        clip.heightProperty().bind(vf.heightProperty());
        vf.setClip(clip);
    }

    private void initTracks(List<Track> tracks) {
        allTracks.setAll(tracks);
        if (allTrackNodes.isEmpty()) {
            createAllTrackNodes(tracks);
        } else {
            onPlaylistFilterUpdated(tracks);
        }
        filterVisibleTracks(searchBox.getText());
        trackCount.setText(tracks.size() + " Tracks");
    }

    private void onPlaylistFilterUpdated(List<Track> tracks) {
        this.lastFilteredPlaylistTracks = tracks;
        filterVisibleTracks(searchBox.getText());
    }

    private void createAllTrackNodes(List<Track> tracks) {
        showLoading(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (int i = 0; i < tracks.size(); i++) {
                    Track track = tracks.get(i);
                    HBox node = ListItemFactory.createListItem(track); // heavy work
                    ListItemController controller = (ListItemController) node.getProperties().get(CONTROLLER);

                    int finalI = i;
                    Platform.runLater(() -> {
                        setupMouseClickHandler(node, track, controller, allTrackNodes, allTracks, finalI);
                        setupDragHandler(node, track, controller, finalI);
                        attachContextMenu(node, track, controller, finalI);
                    });

                    allTrackNodes.add(node);
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showLoading(false);
            //shaw all on init
            bf.showAsFirst(0);

            Platform.runLater(() -> onPlaylistFilterUpdated(lastFilteredPlaylistTracks));
        });

        task.setOnFailed(e -> showLoading(false));

        new Thread(task).start();
    }

    private void filterVisibleTracks(String filterText) {
        if (allTrackNodes.size() < allTracks.size()) {
            Platform.runLater(() -> filterVisibleTracks(filterText));
            return;
        }

        String lowerFilter = (filterText == null) ? "" : filterText.toLowerCase();
        ObservableList<HBox> filteredNodes = FXCollections.observableArrayList();

        for (int i = 0; i < allTracks.size(); i++) {
            Track track = allTracks.get(i);
            boolean matchesPlaylist = lastFilteredPlaylistTracks.isEmpty() || lastFilteredPlaylistTracks.contains(track);
            boolean matchesSearch = lowerFilter.isEmpty() || matchesFilter(track, lowerFilter);

            if (matchesPlaylist && matchesSearch) {
                filteredNodes.add(allTrackNodes.get(i)); // reuse node
            }
        }

        visibleTrackNodes.setAll(filteredNodes);
        bf.showAsFirst(0);
        trackCount.setText(filteredNodes.size() + " Tracks");
    }


    private boolean matchesFilter(Track track, String filter) {
        //match PNS filter logic
        return track.getTitle().toLowerCase().contains(filter)
                || track.getArtist().toLowerCase().contains(filter)
                    || track.getAlbum().toLowerCase().contains(filter);
    }

    private void showLoading(boolean show) {
        Platform.runLater(() -> {
            // Implement loading indicator idk
        });
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
            ListItemController c = (ListItemController) n.getProperties().get(CONTROLLER);
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
            ListItemController c = (ListItemController) item.getProperties().get(CONTROLLER);
            c.setSelected(false);
        }
        selectedTracks.clear();
    }

    private void setupDragHandler(HBox node, Track track, ListItemController controller, int index) {
        node.setOnDragDetected(event -> {
            if (!selectedTracks.contains(track)) {
                clearAllSelections(visibleTrackNodes);
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
            ClipboardContent copy = getSelectedClipboardContent();
            System.out.println(copy);
        });
        MenuItem Queue = new MenuItem("Queue");
        Queue.setOnAction(e -> PlaylistNavigationService.getInstance().addToPlayNextQueue(track));
        contextMenu.getItems().addAll(Copy, Queue);
        node.setOnContextMenuRequested(e -> contextMenu.show(node, e.getScreenX(), e.getScreenY()));
    }

    public void clearSelection() {
        clearAllSelections(visibleTrackNodes);
        lastSelectedIndex = -1;
    }

    public VirtualizedScrollPane<VirtualFlow<HBox, ?>> getVf() {
        return vf;
    }

    private Cell<HBox, ?> regionCell(HBox box) {
        return Cell.wrapNode(box);
    }
}

