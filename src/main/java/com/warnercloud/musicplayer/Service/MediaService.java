package com.warnercloud.musicplayer.Service;

import com.warnercloud.musicplayer.Model.Track;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MediaService {

    private static MediaService instance; // Singleton

    private final MediaPlayerFactory mediaPlayerFactory;
    private MediaPlayer mediaPlayer;
    private Track currentTrack;
    private final List<Consumer<Track>> trackChangeListeners = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> playCountTask;
    private boolean playCounted = false;


    private MediaService() {
        mediaPlayerFactory = new MediaPlayerFactory();
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();
        mediaPlayer.events().addMediaPlayerEventListener(mediaPlayerEventListener);
    }

    public static MediaService getInstance() {
        if (instance == null) {
            instance = new MediaService();
        }
        return instance;
    }

    public void loadTrack(Track track) {
        // VLCJ player setup
        if (mediaPlayer != null) {
            mediaPlayer.controls().stop();
        }
        if (playCountTask != null && !playCountTask.isDone()) {
            playCountTask.cancel(true);
        }

        playCounted = false;
        currentTrack = track;
        notifyTrackChangeListeners(track);

        boolean success = mediaPlayer.media().play(track.getFilePath().toString());
        if (!success) {
            System.err.println("Failed to play track: " + track.getFilePath());
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


    private final MediaPlayerEventListener mediaPlayerEventListener = new MediaPlayerEventAdapter() {
        @Override
        public void finished(final MediaPlayer mediaPlayer) {
            Track nextTrack = PlaylistNavigationService.getInstance().playNext();
            if (nextTrack != null && !mediaPlayer.controls().getRepeat()) {
                mediaPlayer.submit(() -> loadTrack(nextTrack));
            }
        }

        @Override
        public void playing(final MediaPlayer mediaPlayer) {
            onTrackStarted();
        }


        private void onTrackStarted(){
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
                        playCounted = true;
                        System.out.println("Play Counted: " + currentTrack.getTitle() + " Count: " + currentTrack.getPlayCount());
                    } else {
                        System.out.println("Track was not playing at 50s mark.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 50, TimeUnit.SECONDS);
        }
    };
}
