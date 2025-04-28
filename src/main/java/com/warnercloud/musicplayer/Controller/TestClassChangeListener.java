package com.warnercloud.musicplayer.Controller;

import com.warnercloud.musicplayer.Model.Track;
import com.warnercloud.musicplayer.Service.MediaService;
import com.warnercloud.musicplayer.Utils.TimeUtils;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class TestClassChangeListener {

   public TestClassChangeListener() {
        MediaService.getInstance().addTrackChangeListener(this::onTrackChanged);
        System.out.println("hello");
   }

    private void onTrackChanged(Track track) {
        System.out.println(String.format("Title: %s, Artist: %s Duration: %s", track.getTitle(), track.getArtist(), TimeUtils.formatDuration(track.getDuration())));
    }
}
