package com.warnercloud.musicplayer.Service;

import com.warnercloud.musicplayer.Model.Track;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import uk.co.caprica.vlcj.player.list.PlaybackMode;

import java.util.*;
import java.util.function.Consumer;

public class PlaylistNavigationService {

    private static PlaylistNavigationService instance;

    private final List<Integer> playbackQueue = new ArrayList<>();
    private final List<Integer> tempSavePlaybackQueue = new ArrayList<>();
    private final Deque<Integer> playNextQueue = new ArrayDeque<>();

    private int currentIndex = -1;
    private int currentTrackId = -1;

    private final BooleanProperty isShuffled = new SimpleBooleanProperty(false);;

    private PlaylistNavigationService() {}

    public static PlaylistNavigationService getInstance() {
        if (instance == null) {
            instance = new PlaylistNavigationService();
        }
        return instance;
    }

    public void startPlaybackFrom(int trackId, List<Integer> trackIds) {
        playbackQueue.clear();
        playbackQueue.addAll(trackIds);
        playNextQueue.clear();

        currentIndex = playbackQueue.indexOf(trackId);

        if (currentIndex == -1) {
            return;
        }

        MediaService.getInstance().loadTrack(trackId);
        System.out.println(playbackQueue.stream().toList());
    }

    public void setIsShuffled(boolean shuffled){
        isShuffled.set(shuffled);
        regenerateQueueBasedOnShuffle();
    }

    private void regenerateQueueBasedOnShuffle(){
        if(isShuffled.get()) shuffleOn();
        else shuffleOff();
    }

    private void shuffleOn() {
        if (playbackQueue.isEmpty()) return;
        tempSavePlaybackQueue.clear();
        tempSavePlaybackQueue.addAll(playbackQueue);

        currentTrackId = playbackQueue.get(currentIndex);
        playbackQueue.remove(currentIndex);
        Collections.shuffle(playbackQueue);
        playbackQueue.addFirst(currentTrackId);
        //System.out.println("Shuffle On: " + playbackQueue.stream().toList());

        currentIndex = playbackQueue.indexOf(currentTrackId);
    }

    private void shuffleOff() {
        if (playbackQueue.isEmpty()) return;
        //int originalIndex = tempSavePlaybackQueue.indexOf(currentTrackId);
        playbackQueue.clear();
        playbackQueue.addAll(tempSavePlaybackQueue);
        //System.out.println("Shuffle Off: " + playbackQueue.stream().toList());
        //System.out.println("Temp SubList: " + tempSavePlaybackQueue.subList(originalIndex, tempSavePlaybackQueue.size()));
        currentIndex = tempSavePlaybackQueue.indexOf(currentTrackId);
    }


    public boolean playNext() {
        if (!playNextQueue.isEmpty()) {
            MediaService.getInstance().loadTrack(playNextQueue.removeFirst());
            return true;
        }

        if (currentIndex + 1 >= playbackQueue.size()) {
            return false;
        }

        currentIndex++;
        currentTrackId = playbackQueue.get(currentIndex);
        MediaService.getInstance().loadTrack(playbackQueue.get(currentIndex));
        return true;
    }


    public boolean playPrevious(){
        if (currentIndex <= 0){
            return false;
        }
        currentIndex--;
        currentTrackId = playbackQueue.get(currentIndex);
        MediaService.getInstance().loadTrack(playbackQueue.get(currentIndex));
        return true;
    }

    public List<Integer> getPlaybackQueue(){
        return List.copyOf(playbackQueue);
    }


    public void addToPlayNextQueue(int TrackId){
        playNextQueue.addLast(TrackId);
    }


    public boolean isIsShuffled(){ return isShuffled.get(); }

}


