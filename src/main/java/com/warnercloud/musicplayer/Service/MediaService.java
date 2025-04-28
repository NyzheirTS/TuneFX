package com.warnercloud.musicplayer.Service;

import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import com.warnercloud.musicplayer.Model.Track;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MediaService {

    private static MediaService instance; // Singleton

    private MediaPlayer player;
    private Track currentTrack;
    private final List<Consumer<Track>> trackChangeListeners = new ArrayList<>();
    private static final Set<String> EXPECTED_KEYS = Set.of("title", "artist", "album", "album artist", "genre", "year", "track number", "track count","disc number", "disc count", "duration", "image");

    private MediaService() { }

    public static MediaService getInstance() {
        if (instance == null) {
            instance = new MediaService();
        }
        return instance;
    }

    public void loadTrack(Track track, Consumer<Track> onMetadataReady) {
        if (player != null) {
            player.dispose(); // Clean up existing player
        }
        Media media = new Media(track.getFilePath());
        player = new MediaPlayer(media);
        extractMetadata(media, track, onMetadataReady);
    }

    private void extractMetadata(Media media, Track track, Consumer<Track> onMetadataReady) {
        Set<String> recievedKeys = new HashSet<>();
        media.getMetadata().addListener((MapChangeListener.Change<? extends String, ?> change) -> {
            if (change.wasAdded()) {
                String key = change.getKey();
                Object value = change.getValueAdded();

                switch (key) {
                    case "title" -> track.setTitle((String) value);
                    case "artist" -> track.setArtist((String) value);
                    case "album" -> track.setAlbum((String) value);
                    case "album artist" -> track.setAlbumArtist((String) value);
                    case "genre" -> track.setGenre((String) value);
                    case "year" -> track.setYear((Integer) value);
                    case "track number" -> track.setTrackNumber((Integer) value);
                    case "track count" -> track.setTrackCount((Integer) value);
                    case "disc number" -> track.setDiscNumber((Integer) value);
                    case "disc count" -> track.setDiscCount((Integer) value);
                    case "duration" -> {
                        if (value instanceof Duration duration) {
                            track.setDuration(duration);
                        }
                    }
                    case "image" -> {
                        if (value instanceof Image image) {
                            track.setCover(image);
                        }
                    }
                }
                if (EXPECTED_KEYS.contains(key)) {
                    recievedKeys.add(key);
                }
                if(recievedKeys.containsAll(EXPECTED_KEYS)){
                    // Fire update callback
                    onMetadataReady.accept(track);
                    currentTrack = track;
                    notifyTrackChangeListeners(track);
                }
            }
        });
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
