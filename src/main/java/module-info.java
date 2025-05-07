module com.warnercloud.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.media;
    requires mp3agic;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.natives;
    requires javafx.swing;
    requires com.fasterxml.jackson.databind;

    opens com.warnercloud.musicplayer to javafx.fxml;
    exports com.warnercloud.musicplayer;
    exports com.warnercloud.musicplayer.Controller;
    opens com.warnercloud.musicplayer.Controller to javafx.fxml;
    opens com.warnercloud.musicplayer.Utils to com.fasterxml.jackson.databind;
    exports com.warnercloud.musicplayer.Utils;
}