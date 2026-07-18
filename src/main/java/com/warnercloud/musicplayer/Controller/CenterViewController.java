package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Factory.ListItemFactory;
import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.APIService;
import com.warnercloud.musicplayer.Service.PlaylistNavigationService;
import com.warnercloud.musicplayer.Utils.JsonUtil;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class CenterViewController {
    @FXML public BorderPane parent;
    @FXML public Label trackCount;
    @FXML public TextField searchBox;

    private int lastSelectedIndex = -1;
    private final ObservableList<Track> selectedTracks = FXCollections.observableArrayList();
    private final ObservableList<Track> currentTracks = FXCollections.observableArrayList();
    private final ObservableList<Track> visibleTracks = FXCollections.observableArrayList();
    private final VirtualFlow<Track, ?> bf = VirtualFlow.createVertical(visibleTracks, this::trackCell);
    private final VirtualizedScrollPane<VirtualFlow<Track, ?>> vf = new VirtualizedScrollPane<>(bf, ScrollPane.ScrollBarPolicy.NEVER, ScrollPane.ScrollBarPolicy.ALWAYS);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClipboardContent content = new ClipboardContent();
    private final PauseTransition pauseTransition = new PauseTransition(Duration.millis(250));
    private static final String CONTROLLER = "controller";

    public void initContainer() {
        styleVf();
        parent.setCenter(vf);
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

    public void loadPlaylist(int playlistId) {
        clearSelection();
        currentTracks.clear();
        visibleTracks.clear();

        Task<List<Track>> task = new Task<>() {
            @Override
            protected List<Track> call() throws Exception {
                String json = APIService.getInstance().apiCall("https://api.warnercloud.com/api/playlists/" + playlistId + "/tracks");
                Track[] tracks = JsonUtil.fromJson(json, Track[].class);
                return List.of(tracks);
            }
        };

        task.setOnSucceeded(e -> {
            currentTracks.setAll(task.getValue());
            filterVisibleTracks(searchBox.getText());
        });
        task.setOnFailed(e -> { });
        new Thread(task).start();
    }

    private void filterVisibleTracks(String filterText) {
        String filter = filterText == null ? "" : filterText.toLowerCase();
        ObservableList<Track> filtered = FXCollections.observableArrayList();
        for (Track track : currentTracks) {
            if (filter.isEmpty() || matchesFilter(track, filter)) {
                filtered.add(track);
            }
        }
        visibleTracks.setAll(filtered);
        bf.showAsFirst(0);
        trackCount.setText(filtered.size() + " Tracks");
    }

    private boolean matchesFilter(Track track, String filter) {
        return track.getTitle().toLowerCase().contains(filter)
                || track.getArtist_name().toLowerCase().contains(filter)
                || track.getAlbum_title().toLowerCase().contains(filter);
    }

    // PROPER IMPLEMENTATION YOU NEED TO BUILD A WRAPPER FOR YOUR NODES!!!
    private Cell<Track, ?> trackCell(Track track) {
        HBox node = ListItemFactory.createListItem(track);
        ListItemController controller = (ListItemController) node.getProperties().get(CONTROLLER);
        int index = currentTracks.indexOf(track);
        controller.setSelected(selectedTracks.contains(track));
        setupMouseClickHandler(node, track, controller, index);
        setupDragHandler(node, track, controller, index);
        attachContextMenu(node, track);
        return Cell.<Track, HBox>wrapNode(node).beforeDispose(controller::dispose);
    }

    private void setupMouseClickHandler(HBox node, Track track, ListItemController controller, int index) {
        node.setOnMouseClicked(event -> {
            nodeSelection(track, controller, index, event);
        });
    }

    private void nodeSelection(Track track, ListItemController controller, int index, MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) return;
        if (event.isControlDown()) {
            if (controller.isSelected()) {
                selectedTracks.remove(track);
            } else {
                selectedTracks.add(track);
            }
            refreshVisibleSelectionStates();
        } else if (event.isShiftDown() && lastSelectedIndex != -1) {
            applyShiftSelection(index);
        } else {
            selectOnlyThis(track);
        }
        lastSelectedIndex = index;
    }

    private void applyShiftSelection(int currentIndex) {
        selectedTracks.clear();
        int from = Math.min(lastSelectedIndex, currentIndex);
        int to = Math.max(lastSelectedIndex, currentIndex);
        for (int i = from; i <= to; i++) selectedTracks.add(currentTracks.get(i));
        refreshVisibleSelectionStates();
    }

    private void selectOnlyThis(Track track) {
        selectedTracks.setAll(track);
        refreshVisibleSelectionStates();
    }

    private void clearAllSelections() {
        selectedTracks.clear();
        refreshVisibleSelectionStates();
    }

    private void refreshVisibleSelectionStates() {
        int last = Math.min(bf.getLastVisibleIndex(), visibleTracks.size() - 1);
        for (int i = Math.max(0, bf.getFirstVisibleIndex()); i <= last; i++) {
            final int visibleIndex = i;
            bf.getCellIfVisible(visibleIndex).ifPresent(cell -> {
                HBox node = (HBox) cell.getNode();
                ListItemController controller = (ListItemController) node.getProperties().get(CONTROLLER);
                controller.setSelected(selectedTracks.contains(visibleTracks.get(visibleIndex)));
            });
        }
    }

    private void setupDragHandler(HBox node, Track track, ListItemController controller, int index) {
        node.setOnDragDetected(event -> {
            if (!selectedTracks.contains(track)) {
                selectOnlyThis(track);
                lastSelectedIndex = index;
            }
            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
            db.setContent(getSelectedClipboardContent());
            event.consume();
        });
    }

    private ClipboardContent getSelectedClipboardContent() {
        try {
            List<String> uuids = Collections.singletonList(selectedTracks.stream().map(Track::getTrack_id).toList().toString());
            String json = objectMapper.writeValueAsString(uuids);
            content.clear();
            content.putString(json);
            return content;
        } catch (Exception _) {
            return null;
        }
    }

    private void attachContextMenu(HBox node, Track track) {
        ContextMenu menu = new ContextMenu();
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(e -> System.out.println(getSelectedClipboardContent()));
        MenuItem queue = new MenuItem("Queue");
        queue.setOnAction(e -> PlaylistNavigationService.getInstance().addToPlayNextQueue(track));
        menu.getItems().addAll(copy, queue);
        node.setOnContextMenuRequested(e -> menu.show(node, e.getScreenX(), e.getScreenY()));
    }

    public void clearSelection() {
        clearAllSelections();
        lastSelectedIndex = -1;
    }

    public VirtualizedScrollPane<VirtualFlow<Track, ?>> getVf() {
        return vf;
    }
}