package com.warnercloud.musicplayer.Service;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import com.warnercloud.musicplayer.Model.Track;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MediaService {

    private static MediaService instance; // Singleton

    private MediaPlayer player;
    private Track currentTrack;
    private final List<Consumer<Track>> trackChangeListeners = new ArrayList<>();


    private MediaService() { }

    public static MediaService getInstance() {
        if (instance == null) {
            instance = new MediaService();
        }
        return instance;
    }

    public void loadTrack(Track track, Consumer<Track> onMetadataReady) {
        if (player != null) player.dispose(); // Clean up existing player

        Media media = new Media(track.getFilePath().toURI().toString());
        player = new MediaPlayer(media);
        player.setOnEndOfMedia(this::playNextTrack);
        try{
            Mp3File mp3File = new Mp3File(track.getFilePath());
            extractMetadata(mp3File, track, onMetadataReady);
        } catch (UnsupportedTagException | InvalidDataException | IOException _) {}
    }

    private void playNextTrack() {
        Track nextTrack = PlaylistNavigationService.getInstance().playNext();
        if (nextTrack != null) {
            loadTrack(nextTrack, track -> play());
        }
    }

    private void extractMetadata(Mp3File file, Track track, Consumer<Track> onMetadataReady) {
        if (file.hasId3v2Tag() || file.hasId3v1Tag()) {
            ID3v2 id3v2 = file.getId3v2Tag();

            track.setTitle(id3v2.getTitle());
            track.setArtist(id3v2.getArtist());
            track.setAlbum(id3v2.getAlbum());
            track.setAlbumArtist(id3v2.getAlbumArtist());
            track.setGenre(id3v2.getGenreDescription());
            track.setYear(id3v2.getYear());
            track.setTrackNumber(id3v2.getTrack().replaceAll("^([0-9]+)/(.*)$", "$1"));
            track.setTrackCount(id3v2.getTrack().replaceAll("^([0-9]+)/(.*)$", "$2"));
            track.setDuration(Duration.millis(file.getLengthInMilliseconds()));

            byte[] imageData = id3v2.getAlbumImage();
            if (imageData != null) {
                ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
                track.setCover(new Image(bis));
            }
            // Fire update callback
            onMetadataReady.accept(track);
            currentTrack = track;
            notifyTrackChangeListeners(track);
        }

    }


    public void play() {
        if (player != null) player.play();
    }

    public void pause() {
        if (player != null){
            player.setRate(0.0);
            player.pause();
            player.setRate(1.0);
        }
    }

    public void stop() {
        if (player != null) player.stop();
    }

    public boolean isPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public boolean inProgress(){
        return player != null && player.getBufferProgressTime() != javafx.util.Duration.millis(0);
    }

    public void resetTrack(){
        player.setRate(0.0);
        player.seek(javafx.util.Duration.millis(0));
        player.setRate(1.0);
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void addTrackChangeListener(Consumer<Track> listener) {
        trackChangeListeners.add(listener);
        if (currentTrack != null) {
            listener.accept(currentTrack);
        }
    }

    private void notifyTrackChangeListeners(Track track) {
        for (Consumer<Track> listener : trackChangeListeners) {
            listener.accept(track);
        }
    }

    public void dispose() {
        if (player != null) {
            player.dispose();
            player = null;
        }
    }
}
