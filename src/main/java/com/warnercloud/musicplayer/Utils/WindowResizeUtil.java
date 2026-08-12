package com.warnercloud.musicplayer.Utils;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowResizeUtil {

    public static void makeResizable(Stage stage, Scene scene) {

        final int RESIZE_MARGIN = 6;

        final double[] startX = new double[1];
        final double[] startY = new double[1];

        final double[] startWidth = new double[1];
        final double[] startHeight = new double[1];

        final double[] startStageX = new double[1];
        final double[] startStageY = new double[1];

        final javafx.scene.Cursor[] resizeCursor = new javafx.scene.Cursor[1];


        // --------------------------------------------------
        // Change mouse cursor when near an edge
        // --------------------------------------------------

        scene.setOnMouseMoved(event -> {

            if (stage.isMaximized()) {
                scene.setCursor(javafx.scene.Cursor.DEFAULT);
                resizeCursor[0] = javafx.scene.Cursor.DEFAULT;
                return;
            }

            double x = event.getSceneX();
            double y = event.getSceneY();

            double width = scene.getWidth();
            double height = scene.getHeight();

            boolean left = x <= RESIZE_MARGIN;
            boolean right = x >= width - RESIZE_MARGIN;
            boolean top = y <= RESIZE_MARGIN;
            boolean bottom = y >= height - RESIZE_MARGIN;


            if (left && top) {
                resizeCursor[0] = javafx.scene.Cursor.NW_RESIZE;
            }
            else if (right && top) {
                resizeCursor[0] = javafx.scene.Cursor.NE_RESIZE;
            }
            else if (left && bottom) {
                resizeCursor[0] = javafx.scene.Cursor.SW_RESIZE;
            }
            else if (right && bottom) {
                resizeCursor[0] = javafx.scene.Cursor.SE_RESIZE;
            }
            else if (left) {
                resizeCursor[0] = javafx.scene.Cursor.W_RESIZE;
            }
            else if (right) {
                resizeCursor[0] = javafx.scene.Cursor.E_RESIZE;
            }
            else if (top) {
                resizeCursor[0] = javafx.scene.Cursor.N_RESIZE;
            }
            else if (bottom) {
                resizeCursor[0] = javafx.scene.Cursor.S_RESIZE;
            }
            else {
                resizeCursor[0] = javafx.scene.Cursor.DEFAULT;
            }

            scene.setCursor(resizeCursor[0]);
        });


        // --------------------------------------------------
        // Store starting position when mouse is pressed
        // --------------------------------------------------

        scene.setOnMousePressed(event -> {

            if (resizeCursor[0] == null ||
                    resizeCursor[0] == javafx.scene.Cursor.DEFAULT) {
                return;
            }

            startX[0] = event.getScreenX();
            startY[0] = event.getScreenY();

            startWidth[0] = stage.getWidth();
            startHeight[0] = stage.getHeight();

            startStageX[0] = stage.getX();
            startStageY[0] = stage.getY();
        });


        // --------------------------------------------------
        // Resize while dragging
        // --------------------------------------------------

        scene.setOnMouseDragged(event -> {

            if (stage.isMaximized()) {
                return;
            }

            if (resizeCursor[0] == null ||
                    resizeCursor[0] == javafx.scene.Cursor.DEFAULT) {
                return;
            }

            double deltaX =
                    event.getScreenX() - startX[0];

            double deltaY =
                    event.getScreenY() - startY[0];


            // -------------------------
            // RIGHT EDGE
            // -------------------------

            if (resizeCursor[0] == javafx.scene.Cursor.E_RESIZE ||
                    resizeCursor[0] == javafx.scene.Cursor.NE_RESIZE ||
                    resizeCursor[0] == javafx.scene.Cursor.SE_RESIZE) {

                double newWidth =
                        startWidth[0] + deltaX;

                if (newWidth >= stage.getMinWidth()) {
                    stage.setWidth(newWidth);
                }
            }


            // -------------------------
            // LEFT EDGE
            // -------------------------

            if (resizeCursor[0] == javafx.scene.Cursor.W_RESIZE ||
                    resizeCursor[0] == javafx.scene.Cursor.NW_RESIZE ||
                    resizeCursor[0] == javafx.scene.Cursor.SW_RESIZE) {

                double newWidth =
                        startWidth[0] - deltaX;

                if (newWidth >= stage.getMinWidth()) {

                    stage.setWidth(newWidth);

                    stage.setX(
                            startStageX[0] + deltaX
                    );
                }
            }


            // -------------------------
            // BOTTOM EDGE
            // -------------------------

            if (resizeCursor[0] == javafx.scene.Cursor.S_RESIZE ||
                    resizeCursor[0] == javafx.scene.Cursor.SE_RESIZE ||
                    resizeCursor[0] == javafx.scene.Cursor.SW_RESIZE) {

                double newHeight =
                        startHeight[0] + deltaY;

                if (newHeight >= stage.getMinHeight()) {
                    stage.setHeight(newHeight);
                }
            }


            // -------------------------
            // TOP EDGE
            // -------------------------

            if (resizeCursor[0] == javafx.scene.Cursor.N_RESIZE ||
                    resizeCursor[0] == javafx.scene.Cursor.NE_RESIZE ||
                    resizeCursor[0] == javafx.scene.Cursor.NW_RESIZE) {

                double newHeight =
                        startHeight[0] - deltaY;

                if (newHeight >= stage.getMinHeight()) {

                    stage.setHeight(newHeight);

                    stage.setY(
                            startStageY[0] + deltaY
                    );
                }
            }
        });
    }
}
