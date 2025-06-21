package com.warnercloud.musicplayer.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.flowless.VirtualizedScrollPane;

public class QueueListController {
    public BorderPane queueListContainer;

    private ObservableList<HBox> items = FXCollections.observableArrayList();

    private final VirtualFlow<HBox, ?> bf = VirtualFlow.createVertical(items, this::regionCell);
    private final VirtualizedScrollPane<VirtualFlow<HBox, ?>> vf = new VirtualizedScrollPane<>(bf, ScrollPane.ScrollBarPolicy.NEVER, ScrollPane.ScrollBarPolicy.ALWAYS);


    private Cell<HBox, ?> regionCell(HBox box){return Cell.wrapNode(box);}
}
