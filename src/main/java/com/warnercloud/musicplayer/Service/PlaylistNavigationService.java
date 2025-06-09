package com.warnercloud.musicplayer.Service;

import com.warnercloud.musicplayer.Model.Track;

import java.util.*;
import java.util.function.Consumer;

public class PlaylistNavigationService {
    private static PlaylistNavigationService instance;

    private final List<Track> allTracks = new ArrayList<>();     //list of available songs
    private final List<Track> playbackQueue = new ArrayList<>(); // playback queue
    private final List<Track> filteredTracks = new ArrayList<>();
    private final List<Consumer<List<Track>>> allTrackListeners = new ArrayList<>();
    private final List<Consumer<List<Track>>> queueListeners = new ArrayList<>();

    private String currentTrackUUID = null;

    private PlaylistNavigationService() {}

    public static PlaylistNavigationService getInstance() {
        if (instance == null) {
            instance = new PlaylistNavigationService();
        }
        return instance;
    }

    // Load all tracks from selected playlist
    public void loadPlaylist(List<Track> tracks) {
        allTracks.clear();
        allTracks.addAll(tracks);

        filteredTracks.clear();
        filteredTracks.addAll(allTracks);

        //resetCursor();
        notifyTrackConsumers();
    }

    // Set active playback queue
    public void setPlaybackQueue(List<Track> tracks) {
        playbackQueue.clear();
        playbackQueue.addAll(tracks);
        resetCursor();
        notifyQueueConsumers();
    }

    public void startPlaybackFrom(Track clickedTrack) {
        if(clickedTrack.getUUID().equals(currentTrackUUID)){
            return;
        }
        int startIndex = indexOfInFilteredTracks(clickedTrack);
        if (startIndex == -1) return;

        List<Track> newQueue = new ArrayList<>(filteredTracks.subList(startIndex, filteredTracks.size()));
        setPlaybackQueue(newQueue);
        currentTrackUUID = clickedTrack.getUUID();
        MediaService.getInstance().loadTrack(clickedTrack);
    }

    private int indexOfInFilteredTracks(Track track) {
        for (int i = 0; i < filteredTracks.size(); i++) {
            if (filteredTracks.get(i).getUUID().equals(track.getUUID())) {
                return i;
            }
        }
        return -1;
    }


    public Track playNext() {
        int index = getCurrentIndexInQueue();
        if (index != -1 && index + 1 < playbackQueue.size()) {
            Track nextTrack = playbackQueue.get(index + 1);
            currentTrackUUID = nextTrack.getUUID();
            System.out.println("Next track: " + nextTrack.getTitle() + " - Position " + (index + 1) +"/" + playbackQueue.size());
            return nextTrack;
        }
        return null;
    }

    public Track playPrevious() {
        int index = getCurrentIndexInQueue();
        if (index > 0) {
            Track prevTrack = playbackQueue.get(index - 1);
            currentTrackUUID = prevTrack.getUUID();
            return prevTrack;
        }
        return null;
    }

    public Track peekNext() {
        int index = getCurrentIndexInQueue();
        if (index + 1 < playbackQueue.size()) {
            return playbackQueue.get(index + 1);
        }
        return null;
    }

    public Track peekPrevious() {
        int index = getCurrentIndexInQueue();
        if (index - 1 >= 0) {
            return playbackQueue.get(index - 1);
        }
        return null;
    }

    public Track getCurrentTrack() {
        if (currentTrackUUID == null) return null;
        return playbackQueue.stream()
                .filter(t -> t.getUUID().equals(currentTrackUUID))
                .findFirst()
                .orElse(null);
    }

    private int getCurrentIndexInQueue() {
        if (currentTrackUUID == null) return -1;
        for (int i = 0; i < playbackQueue.size(); i++) {
            if (playbackQueue.get(i).getUUID().equals(currentTrackUUID)) {
                return i;
            }
        }
        return -1;
    }


    public void filterTracks(Set<String> playlistUUIDs){
        filteredTracks.clear();
        for (Track track : allTracks) {
            if (playlistUUIDs.contains(track.getUUID())) {
                filteredTracks.add(track);
            }
        }
        //resetCursor();
        notifyTrackConsumers();
    }


    public void addTrackToQueue(Track track) {
        int index = getCurrentIndexInQueue();
        if (index != -1) {
            playbackQueue.add(index + 1, track);
        } else {
            playbackQueue.add(track);
        }
        currentTrackUUID = track.getUUID();
    }

    public void resetCursor() {
        currentTrackUUID = null;
    }

    public void clearPlaybackQueue() {
        playbackQueue.clear();
        resetCursor();
    }

    public List<Track> getAllTracks() {
        return allTracks;
    }

    public List<Track> getPlaybackQueue() {
        return playbackQueue;
    }

    private void notifyTrackConsumers() {
        for (Consumer<List<Track>> consumer : allTrackListeners) {
            consumer.accept(new ArrayList<>(filteredTracks));
        }
    }

    private void notifyQueueConsumers() {
        for (Consumer<List<Track>> consumer : queueListeners) {
            consumer.accept(new ArrayList<>(playbackQueue));
        }
    }

    public void addListFullListener(Consumer<List<Track>> consumer) {
        allTrackListeners.add(consumer);
    }
    public void addQueueFullListener(Consumer<List<Track>> consumer) {allTrackListeners.add(consumer);}
}



