package com.warnercloud.musicplayer.Service;

import com.warnercloud.musicplayer.Model.Track;

import java.util.*;

public class PlaylistNavigationService {
    private static PlaylistNavigationService instance;

    private  List<Track> playlists = new ArrayList<>();
    private  List<Track> playbackQueue = new ArrayList<>();
    private int currentIndex = -1; // -1 means no track is selected yet
    private int queueIndex = 0;
    private PlaylistNavigationService() {}

    public static PlaylistNavigationService getInstance() {
        if (instance == null) {
            instance = new PlaylistNavigationService();
        }
        return instance;
    }

    // Move to next track and return it
    public Track playNext() {
        if (currentIndex + 1 < playlists.size()) {
            currentIndex++;
            return playlists.get(currentIndex);
        }
        return null;
    }

    // Move to previous track and return it
    public Track playPrevious() {
        if (currentIndex - 1 >= 0) {
            currentIndex--;
            return playlists.get(currentIndex);
        }
        return null;
    }

    // Peek at the next track without moving the cursor
    public Track peekNext() {
        if (currentIndex + 1 < playlists.size()) {
            return playlists.get(currentIndex + 1);
        }
        return null;
    }

    // Peek at the previous track without moving the cursor
    public Track peekPrevious() {
        if (currentIndex - 1 >= 0) {
            return playlists.get(currentIndex - 1);
        }
        return null;
    }

    // Add a single track (e.g., queued manually)
    public void addTrack(Track track) {
        playlists.add(track);
    }

    // Replace the entire playlist
    public void setTracks(List<Track> tracks) {
        playlists.clear();
        playlists.addAll(tracks);
        resetCursor();
    }

    public void addTrackAfterCurrent(Track track) {
        if (currentIndex >= 0 && currentIndex < playlists.size()) {
            // Insert the track right after the current track
            playlists.add(currentIndex + 1, track);
            currentIndex++;  // Update currentIndex to the new track's position
        } else {
            // If no track is playing, add it to the end
            playlists.add(track);
            currentIndex = playlists.size() - 1; // Make it the current track
        }
    }

    public void startPlaybackFrom(Track clickedTrack) {
        /*int startIndex = clickedTrack.getIndex(); //TODO: Add index to json so can start que from specific point also need to add songes before index to end of que if mix is on
        System.out.println("Starting playback from " + clickedTrack.getIndex());
        if (startIndex == -1) return;

        playbackQueue.clear();
        playbackQueue.addAll(playlists.subList(startIndex, playlists.size()));
        queueIndex = 0;
        setTracks(playbackQueue);*/
    }

    // Clear all tracks
    public void clear() {
        playlists.clear();
        resetCursor();
    }

    // Reset current index
    public void resetCursor() {
        currentIndex = -1;
    }

    // Get all tracks (unmodifiable)
    public List<Track> getAllTracks() {
        return Collections.unmodifiableList(playlists);
    }

    // Get the currently selected/playing track
    public Track getCurrentTrack() {
        if (currentIndex >= 0 && currentIndex < playlists.size()) {
            return playlists.get(currentIndex);
        }
        return null;
    }
}


