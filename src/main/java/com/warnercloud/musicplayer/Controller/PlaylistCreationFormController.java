package com.warnercloud.musicplayer.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PlaylistCreationFormController {

    @FXML public VBox parent;
    @FXML public ImageView playlistImg;
    @FXML public Button openFilePicker;
    @FXML public Label imgFileErrorMSG;
    @FXML public TextField playListNameField;
    @FXML public Label playListNameErrorMSG;
    @FXML public TextField descriptionField;
    @FXML public Label playListNameErrorMSG1;

    public void initialize(Runnable runnable){

    }
}
