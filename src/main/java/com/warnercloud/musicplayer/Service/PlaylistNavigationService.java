package com.warnercloud.musicplayer.Service;

import com.warnercloud.musicplayer.Model.Track;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.*;
import java.util.function.Consumer;

public class PlaylistNavigationService {
    private static PlaylistNavigationService instance;

    private final List<Track> allTracks = new ArrayList<>();
    private final List<Track> playbackQueue = new ArrayList<>();
    private final List<Track> filteredTracks = new ArrayList<>();
    private final List<Track> activePlaylist = new ArrayList<>();
    private final Deque<Track> playNextQueue = new LinkedList<>();
    private final List<Consumer<List<Track>>> allTrackListeners = new ArrayList<>();
    private final List<Consumer<List<Track>>> queueListeners = new ArrayList<>();
    private final List<Consumer<List<Track>>> trackFilterListeners = new ArrayList<>();

    private String currentTrackUUID = null;
    private String currentPlaylistUUID = null;
    private final BooleanProperty isShuffled = new SimpleBooleanProperty(false);
    private boolean isShuffleApplied = false;

    private PlaylistNavigationService() {}

    public static PlaylistNavigationService getInstance() {
        if (instance == null) {
            instance = new PlaylistNavigationService();
        }
        return instance;
    }

    public void initPlaylistService(List<Track> tracks) {
        allTracks.clear();
        allTracks.addAll(tracks);

        filteredTracks.clear();
        filteredTracks.addAll(allTracks);

        notifyTrackConsumers();
    }

    public void setPlaybackQueue(List<Track> tracks) {
        playbackQueue.clear();
        playbackQueue.addAll(tracks);
        resetCursor();
        //notifyQueueConsumers();
    }

    public void startPlaybackFrom(Track clickedTrack) {
        if (clickedTrack.getUUID().equals(currentTrackUUID)) return;

        int startIndex = indexOfInFilteredTracks(clickedTrack);
        if (startIndex == -1) return;

        activePlaylist.clear();
        activePlaylist.addAll(filteredTracks); // snapshotting for playback to avoid bad updates when browsing playlists

        if (isShuffled.get()) {
            isShuffleApplied = true;

            List<Track> tail = new ArrayList<>(activePlaylist.subList(startIndex, activePlaylist.size()));
            Collections.shuffle(tail);

            List<Track> newQueue = new ArrayList<>(tail);
            setPlaybackQueue(newQueue);
        } else {
            isShuffleApplied = false;
            setPlaybackQueue(activePlaylist);
        }
        //print();
        currentTrackUUID = clickedTrack.getUUID();
        MediaService.getInstance().loadTrack(clickedTrack);
    }


    public void print() {
        System.out.println("Queue Status -> Shuffled = " + isShuffleApplied + ", Size = " + playbackQueue.size());
        for (int i = 0; i < playbackQueue.size(); i++) {
            Track t = playbackQueue.get(i);
            System.out.println((i + 1) + ". " + t.getTitle() + " [" + t.getUUID() + "]");
        }
    }


    public void setIsShuffled(boolean isShuffled) {
        this.isShuffled.set(isShuffled);
        regenerateQueueBasedOnShuffle();
    }

    private void regenerateQueueBasedOnShuffle() {
        Track currentTrack = getCurrentTrack();
        if (activePlaylist.isEmpty()) return;
        if (isShuffled.get()) {
            // SHUFFLE ON
            shuffleON(currentTrack);
        } else {
            // SHUFFLE OFF
            shuffleOFF(currentTrack);
        }
        //print();
    }

    private void shuffleON(Track currentTrack){
        List<Track> toShuffle = new ArrayList<>();
        List<Track> newQueue = new ArrayList<>();
        if (currentTrack != null) {
            for (Track track : activePlaylist) {
                if (!track.getUUID().equals(currentTrack.getUUID())) {
                    toShuffle.add(track);
                }
            }
            Collections.shuffle(toShuffle);

            Track playingTrack = findTrackInListByUUID(activePlaylist, currentTrack.getUUID());

            if (playingTrack != null) {
                newQueue.add(playingTrack);
                newQueue.addAll(toShuffle);
                setPlaybackQueue(newQueue);
                currentTrackUUID = playingTrack.getUUID(); // update to ensure match in new queue
            } else {
                System.err.println("Error: currentTrack not found in activePlaylist.");
                return;
            }
        } else {
            List<Track> shuffled = new ArrayList<>(activePlaylist);
            Collections.shuffle(shuffled);
            setPlaybackQueue(shuffled);
        }
        isShuffleApplied = true;
    }

    private void shuffleOFF(Track currentTrack){
        // SHUFFLE OFF
        List<Track> newQueue = new ArrayList<>(activePlaylist);
        setPlaybackQueue(newQueue);
        isShuffleApplied = false;

        if (currentTrack != null) {
            Track playingTrack = findTrackInListByUUID(activePlaylist, currentTrack.getUUID());
            if (playingTrack != null) {
                currentTrackUUID = playingTrack.getUUID();
            }
        }
    }


    private Track findTrackInListByUUID(List<Track> list, String uuid) {
        return list.stream()
                .filter(track -> track.getUUID().equals(uuid))
                .findFirst()
                .orElse(null);
    }



    public void addToPlayNextQueue(Track track) {
        playNextQueue.add(track);
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
        if (!playNextQueue.isEmpty()) {
            return playNextQueue.pop();
        } else {
            if (index != -1 && index + 1 < playbackQueue.size()) {
                Track nextTrack = playbackQueue.get(index + 1);
                currentTrackUUID = nextTrack.getUUID();
                System.out.println("Next track: " + nextTrack.getTitle() + " - Position " + (index + 1) + "/" + playbackQueue.size());
                return nextTrack;
            }
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

    public void filterTracks(Set<String> playlistUUIDs, String playlistName) {
        if (currentTrackUUID.equals(playlistName)) return;
        filteredTracks.clear();
        for (Track track : allTracks) {
            if (playlistUUIDs.contains(track.getUUID())) {
                filteredTracks.add(track);
            }
        }
        currentPlaylistUUID = playlistName;
        notifyTrackFilterConsumers();
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

    public boolean isIsShuffled() {
        return isShuffled.get();
    }

    private void notifyTrackConsumers() {
        List<Track> ss = List.copyOf(filteredTracks);
        for (Consumer<List<Track>> consumer : allTrackListeners) {
            consumer.accept(ss);
        }
    }


    private void notifyQueueConsumers() {
        for (Consumer<List<Track>> consumer : queueListeners) {
            consumer.accept(new ArrayList<>(playbackQueue));
        }
    }

    private void notifyTrackFilterConsumers() {
        List<Track> ss = List.copyOf(filteredTracks);
        for (Consumer<List<Track>> consumer : trackFilterListeners) {
            consumer.accept(ss);
        }
    }

    public void addListFullListener(Consumer<List<Track>> consumer) {
        allTrackListeners.add(consumer);
    }

    public void addQueueFullListener(Consumer<List<Track>> consumer) {
        queueListeners.add(consumer);
    }

    public void addTrackFilterListener(Consumer<List<Track>> consumer) {
        trackFilterListeners.add(consumer);
    }
}


