package com.warnercloud.musicplayer.Factory;

import com.warnercloud.musicplayer.Controller.PlaylistCreationFormController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class PlaylistCreationFormFactory {

    private PlaylistCreationFormFactory() {}

    public static VBox createForm(){
        try {
            FXMLLoader loader = new FXMLLoader(PlaylistCreationFormFactory.class.getResource("/com/warnercloud/musicplayer/Views/playlist-creation-form.fxml"));
            VBox root = loader.load();
            PlaylistCreationFormController controller = loader.getController();
            controller.initialize();
            root.getProperties().put("controller", controller);
            return root;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void showForm(Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(PlaylistCreationFormFactory.class.getResource("/com/warnercloud/musicplayer/Views/playlist-creation-form.fxml"));
            Parent root = loader.load();
            PlaylistCreationFormController controller = loader.getController();

            Scene scene = new Scene(root);
            scene.setFill(null);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(owner);
            stage.setScene(scene);
            //stage.setAlwaysOnTop(true);

            //expose controller for close logic
            //controller.setStage(stage);

            /*stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue){
                    stage.close();
                }
            });*/

            double offsetX = 200;
            double offsetY = 130;

            stage.setX(owner.getX() + offsetX);
            stage.setY(owner.getY() + offsetY);

            // Bind movement
            owner.xProperty().addListener((observable, oldValue, newValue) -> {
                stage.setX(newValue.doubleValue() + offsetX);
            });
            owner.yProperty().addListener((observable, oldValue, newValue) -> {
                stage.setY(newValue.doubleValue() + offsetY);
            });

            stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    PauseTransition pause = new PauseTransition(Duration.millis(150));
                    pause.setOnFinished(e -> {
                        if (!owner.isFocused()) {
                            stage.close();
                        }
                    });
                    pause.play();
                }
            });

            owner.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                if (!stage.isFocused()) {
                    stage.close();
                }
            });

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

