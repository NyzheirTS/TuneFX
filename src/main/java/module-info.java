module com.warnercloud.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.media;
    requires mp3agic;

    opens com.warnercloud.musicplayer to javafx.fxml;
    exports com.warnercloud.musicplayer;
    exports com.warnercloud.musicplayer.Controller;
    opens com.warnercloud.musicplayer.Controller to javafx.fxml;
}