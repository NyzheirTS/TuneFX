package com.warnercloud.musicplayer.Service;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.warnercloud.musicplayer.Model.Track;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MetaDataService {

    private final ExecutorService executor = Executors.newFixedThreadPool(4);  // Keep alive across calls

    public void extractBasicMetadataAsync(List<Track> tracks) {
        for (Track track : tracks) {
            executor.submit(() -> {
                try {
                    Mp3File mp3File = new Mp3File(track.getFilePath());
                    if (mp3File.hasId3v2Tag()) {
                        ID3v2 tag = mp3File.getId3v2Tag();

                        track.setTitle(tag.getTitle());
                        track.setArtist(tag.getArtist());
                        track.setAlbum(tag.getAlbum());
                        track.setAlbumArtist(tag.getAlbumArtist());
                        track.setGenre(tag.getGenreDescription());
                        track.setYear(tag.getYear());
                        track.setTrackNumber(tag.getTrack().replaceAll("^([0-9]+)/(.*)$", "$1"));
                        track.setTrackCount(tag.getTrack().replaceAll("^([0-9]+)/(.*)$", "$2"));
                        track.setDuration(Duration.millis(mp3File.getLengthInMilliseconds()));

                        byte[] imageData = tag.getAlbumImage();
                        if (imageData != null) {
                            ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
                            track.setCover(new Image(bis, 93, 93, true, true));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Metadata error: " + track.getFilePath() + " - " + e.getMessage());
                }
            });
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
