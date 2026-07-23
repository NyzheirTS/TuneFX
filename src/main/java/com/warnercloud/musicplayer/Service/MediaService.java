package com.warnercloud.musicplayer.Service;

import javafx.application.Platform;
import javafx.beans.property.*;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class MediaService {

    private static MediaService instance; // Singleton

    private final MediaPlayerFactory mediaPlayerFactory;
    private MediaPlayer mediaPlayer;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> playCountTask;
    private static final String BASE_STREAM_URL = "https://api.warnercloud.com/api/stream/";
    private final IntegerProperty currentTrackId = new SimpleIntegerProperty(this, "currentTrackId", -1);
    private final LongProperty mediaDuration = new SimpleLongProperty(this, "mediaDuration", 0);
    public static final long TRACK_LENGTH = 30_000L;
    public static final int TRACK_LENGTH_SECONDS = 30;
    //private boolean playCounted = false;


    private MediaService() {
        mediaPlayerFactory = new MediaPlayerFactory();
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();
        MediaPlayerEventListener mediaPlayerEventListener = new MediaPlayerEventAdapter() {
            @Override
            public void finished(final MediaPlayer mediaPlayer) {
                if(!mediaPlayer.controls().getRepeat()) {
                    Platform.runLater(() -> PlaylistNavigationService.getInstance().playNext());
                }
            }

            @Override
            public void lengthChanged(MediaPlayer mediaPlayer, long length) {
                Platform.runLater(() -> mediaDuration.set(length));
            }
        };
        mediaPlayer.events().addMediaPlayerEventListener(mediaPlayerEventListener);
    }

    public static MediaService getInstance() {
        if (instance == null) {
            instance = new MediaService();
        }
        return instance;
    }


    public void loadTrack(int trackId) {
        if (mediaPlayer == null) return;

        mediaPlayer.controls().stop();
        mediaDuration.set(0);

        /* TODO: playcount task setup */

        publishTrack(trackId);

        boolean success = mediaPlayer.media().play(BASE_STREAM_URL + trackId);

        if (!success) {
            System.err.println("Failed to play track: " + trackId);
        }
    }

    private void publishTrack(int trackId) { // set the current track id to update subscribers
        if (Platform.isFxApplicationThread()) {
            currentTrackId.set(trackId);
        } else {
            Platform.runLater(() -> currentTrackId.set(trackId));
        }
    }


    public void seek(long millis) {
        if (mediaPlayer != null) {
            mediaPlayer.controls().setTime(millis);
        }
    }

    public long getCurrentTime() {
        return mediaPlayer != null ? mediaPlayer.status().time() : 0;
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


    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.controls().stop();
            mediaPlayer.release();
            //mediaPlayer.events().removeMediaPlayerEventListener(mediaPlayerEventListener);
            mediaPlayer = null;
        }
        if (mediaPlayerFactory != null) {
            mediaPlayerFactory.release();
        }
        if(playCountTask != null && !playCountTask.isDone()) {
            playCountTask.cancel(true);
        }
        scheduler.shutdownNow();
    }

    public void setCurrentVolume(int currentVolume) {
        if (mediaPlayer != null) {
            mediaPlayer.audio().setVolume(currentVolume);
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

    public ReadOnlyIntegerProperty currentTrackIdProperty() {
        return currentTrackId;
    }

    public int getCurrentTrackId() {
        return currentTrackId.get();
    }

    public ReadOnlyLongProperty mediaDurationProperty() {
        return mediaDuration;
    }

    public long getMediaDuration(){
        return mediaDuration.get();
    }

    public long getTrackLength(){
        return TRACK_LENGTH;
    }


   /* private void onTrackStarted(){
        //System.out.println("Playback confirmed started.");

        if (currentTrack == null || playCounted) return;

        // Cancel existing scheduled task
        if (playCountTask != null && !playCountTask.isDone()) {
            playCountTask.cancel(true);
        }

        // Schedule task to increment play count after 50 seconds
        playCountTask = scheduler.schedule(() -> {
            try {
                if (mediaPlayer.status().isPlaying()) {
                    currentTrack.incrementPlayCount();
                    masterJsonManager.updatePlaycount(currentTrack.getUUID(), 1);
                    playCounted = true;
                    System.out.println("Play Counted: " + currentTrack.getTitle() + " Count: " + currentTrack.getPlayCount());
                } else {
                    System.out.println("Track was not playing at 50s mark.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 50, TimeUnit.SECONDS);
    }*/
}
