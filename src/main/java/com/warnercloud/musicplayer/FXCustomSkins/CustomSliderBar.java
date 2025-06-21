package com.warnercloud.musicplayer.FXCustomSkins;

import javafx.scene.control.Slider;
import javafx.scene.control.skin.SliderSkin;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class CustomSliderBar extends SliderSkin {
    private final Rectangle filledTrack = new Rectangle();
    private Region track;

    public CustomSliderBar(Slider slider) {
        super(slider);

        // Access the default track from the skin
        for (javafx.scene.Node node : getChildren()) {
            if (node.getStyleClass().contains("track") && node instanceof Region) {
                track = (Region) node;
                break;
            }
        }

        if (track != null) {
            // Insert the filled track *right after* the base track
            int trackIndex = getChildren().indexOf(track);
            filledTrack.setFill(Paint.valueOf("1D6E85"));
            filledTrack.setArcHeight(4);
            filledTrack.setArcWidth(4);
            filledTrack.setHeight(8);

            getChildren().add(trackIndex + 1, filledTrack);
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);

        Slider slider = getSkinnable();

        if (track == null) return;

        double trackStart = track.getLayoutX();
        double trackWidth = track.getWidth();
        double trackY = track.getLayoutY() + track.getHeight() / 2.0 - filledTrack.getHeight() / 2.0;

        double min = slider.getMin();
        double max = slider.getMax();
        double value = slider.getValue();

        double valueRatio = (value - min) / (max - min);
        double filledWidth = trackWidth * valueRatio;

        filledTrack.setX(trackStart);
        filledTrack.setY(trackY);
        filledTrack.setWidth(filledWidth);
    }
}
