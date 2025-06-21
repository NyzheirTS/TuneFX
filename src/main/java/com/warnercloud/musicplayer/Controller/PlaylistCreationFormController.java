package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Model.PlaylistData;
import javafx.event.ActionEvent;
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
    @FXML public Button cancelFormButton;
    @FXML public Button submitFormButton;
    @FXML public Label descriptionErrMSG;

    private SideBarController sidebarController;
    private Runnable closeForm;


    public void initialize(){

    }

    public PlaylistData getPlaylistData() {
        return new PlaylistData();
    }

    @FXML
    public void submitForm(ActionEvent actionEvent) {

    }

    @FXML
    public void cancelForm(ActionEvent actionEvent) {
        closeForm.run();
    }


    public void setSidebarController(SideBarController sidebarController) {
        this.sidebarController = sidebarController;
    }

    public void closeForm(Runnable action){
        this.closeForm = action;
    }

}
