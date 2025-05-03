package com.warnercloud.musicplayer.Service;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.scene.image.Image;
import com.warnercloud.musicplayer.Model.Track;
import javafx.util.Duration;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.function.Consumer;

public class MediaService {

    private static MediaService instance; // Singleton

    private final MediaPlayerFactory mediaPlayerFactory;
    private MediaPlayer mediaPlayer;
    private Track currentTrack;
    private final List<Consumer<Track>> trackChangeListeners = new ArrayList<>();
    private long lastLoadTime = 0;
    private static final long DEBOUNCE_INTERVAL_MS = 350;
    private boolean isTrackLoading = false;
    private Timer timer;


    private MediaService() {
        mediaPlayerFactory = new MediaPlayerFactory();
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();
        playNextTrack();
    }

    public static MediaService getInstance() {
        if (instance == null) {
            instance = new MediaService();
        }
        return instance;
    }

    public void loadTrack(Track track, Consumer<Track> onMetadataReady) {
       /* if (isTrackLoading) return;
        isTrackLoading = true;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLoadTime < DEBOUNCE_INTERVAL_MS) {
            isTrackLoading = false;
            return;
        }
        lastLoadTime = currentTime;*/

        try {
            // Extract metadata using mp3agic
            Mp3File mp3File = new Mp3File(track.getFilePath());
            extractMetadata(mp3File, track, onMetadataReady);

            // VLCJ player setup
            if (mediaPlayer != null) {
                mediaPlayer.controls().stop();
            }

            boolean success = mediaPlayer.media().play(track.getFilePath().toString());

            if (!success) {
                System.err.println("Failed to play track: " + track.getFilePath());
            }

            isTrackLoading = false;
            currentTrack = track;
            notifyTrackChangeListeners(track);
        } catch (UnsupportedTagException | InvalidDataException | IOException e) {
            isTrackLoading = false;
            System.err.println("Error loading track" + e.getMessage());
        }
    }

    private void playNextTrack() {
        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(final MediaPlayer mediaPlayer) {
                Track nextTrack = PlaylistNavigationService.getInstance().playNext();
                if (nextTrack != null && !mediaPlayer.controls().getRepeat()) {
                    mediaPlayer.submit(() -> loadTrack(nextTrack, track -> play()));
                }
            }
        });
    }

    private void extractMetadata(Mp3File file, Track track, Consumer<Track> onMetadataReady) {
        if (file.hasId3v2Tag()) {
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
                track.setCover(new Image(bis, 93, 93, true, true));
            }
        }
        // Fire update callback
        onMetadataReady.accept(track);
    }

    public void seek(long millis) {
        if (mediaPlayer != null) {
            mediaPlayer.controls().setTime(millis);
        }
    }

    public long getCurrentTime() {
        return mediaPlayer != null ? mediaPlayer.status().time() : 0;
    }

    public long getMediaDuration() {
        return mediaPlayer != null ? mediaPlayer.media().info().duration() : 0;
    }

    public void setRepeat(boolean repeat) {
        if (mediaPlayer != null) {
            mediaPlayer.controls().setRepeat(repeat);
        }
    }

    public boolean repeatEnabled(){
        return mediaPlayer.controls().getRepeat();
    }

    public String printVolume(){
       return String.valueOf(mediaPlayer.audio().volume());
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.controls().play();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.controls().pause();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.status().isPlaying();
    }

    public void addTrackChangeListener(Consumer<Track> listener) {
        trackChangeListeners.add(listener);
        if (currentTrack != null) {
            listener.accept(currentTrack);
        }
    }

    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.controls().stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaPlayerFactory != null) {
            mediaPlayerFactory.release();
        }
    }

    public void setCurrentVolume(int currentVolume) {
        if (mediaPlayer != null) {
            mediaPlayer.audio().setVolume(currentVolume);
        }
    }

    private void notifyTrackChangeListeners(Track track) {
        for (Consumer<Track> listener : trackChangeListeners) {
            listener.accept(track);
        }
    }

    public boolean isMuted(){
        return mediaPlayer.audio().isMute();
    }

    public void mute() {
        mediaPlayer.audio().setMute(true);
    }

    public void unmute() {
        mediaPlayer.audio().setMute(false);
    }

}
