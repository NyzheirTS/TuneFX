module com.warnercloud.musicplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires javafx.media;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.natives;
    requires javafx.swing;
    requires tools.jackson.databind;
    requires org.fxmisc.flowless;
    requires java.sql;
    requires java.net.http;
    requires okhttp3;


    opens com.warnercloud.musicplayer to javafx.fxml;
    exports com.warnercloud.musicplayer;
    exports com.warnercloud.musicplayer.Controller;
    opens com.warnercloud.musicplayer.Controller to javafx.fxml;
    opens com.warnercloud.musicplayer.Utils to tools.jackson.databind;
    exports com.warnercloud.musicplayer.Utils;
    opens com.warnercloud.musicplayer.Model to tools.jackson.databind, javafx.fxml;
    exports com.warnercloud.musicplayer.Model;
    exports com.warnercloud.musicplayer.FXCustomSkins;
    opens com.warnercloud.musicplayer.FXCustomSkins to javafx.fxml;
}