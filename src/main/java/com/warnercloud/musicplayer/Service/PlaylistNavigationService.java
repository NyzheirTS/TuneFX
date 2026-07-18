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
    private final List<Consumer<Integer>> indexListeners = new ArrayList<>();

    private Integer currentTrackId = null;
    private String currentPlaylistName = null;

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
        notifyQueueConsumers();
    }

    public void startPlaybackFrom(Track clickedTrack) {
        if (clickedTrack == null) return;

        int startIndex = indexOfInFilteredTracks(clickedTrack);
        if (startIndex == -1) return;

        activePlaylist.clear();
        activePlaylist.addAll(filteredTracks);

        if (isShuffled.get()) {
            isShuffleApplied = true;
            List<Track> tail = new ArrayList<>(activePlaylist.subList(startIndex, activePlaylist.size()));
            Collections.shuffle(tail);
            setPlaybackQueue(tail);
        } else {
            isShuffleApplied = false;
            setPlaybackQueue(new ArrayList<>(activePlaylist));
        }

        currentTrackId = clickedTrack.getTrack_id();
        MediaService.getInstance().loadTrack(clickedTrack);
    }

    public void setIsShuffled(boolean shuffled){
        isShuffled.set(shuffled);
        regenerateQueueBasedOnShuffle();
    }

    private void regenerateQueueBasedOnShuffle(){
        if(activePlaylist.isEmpty()) return;
        Track current = getCurrentTrack();
        if(isShuffled.get()) shuffleOn(current);
        else shuffleOff(current);
    }

    private void shuffleOn(Track current){
        List<Track> queue = new ArrayList<>();
        List<Track> rest = new ArrayList<>();

        if(current != null){
            for(Track t: activePlaylist){
                if(t.getTrack_id()!=current.getTrack_id()) rest.add(t);
            }
            Collections.shuffle(rest);
            queue.add(current);
            queue.addAll(rest);
        }else{
            queue.addAll(activePlaylist);
            Collections.shuffle(queue);
        }

        isShuffleApplied=true;
        setPlaybackQueue(queue);
        if(current!=null) currentTrackId=current.getTrack_id();
    }

    private void shuffleOff(Track current){
        isShuffleApplied=false;
        setPlaybackQueue(new ArrayList<>(activePlaylist));
        if(current!=null) currentTrackId=current.getTrack_id();
    }

    private int indexOfInFilteredTracks(Track track){
        for(int i=0;i<filteredTracks.size();i++){
            if(filteredTracks.get(i).getTrack_id()==track.getTrack_id()) return i;
        }
        return -1;
    }

    private int getCurrentIndexInQueue(){
        if(currentTrackId==null) return -1;
        for(int i=0;i<playbackQueue.size();i++){
            if(playbackQueue.get(i).getTrack_id()==currentTrackId) return i;
        }
        return -1;
    }

    public Track getCurrentTrack(){
        if(currentTrackId==null) return null;
        return findTrackById(playbackQueue,currentTrackId);
    }

    private Track findTrackById(List<Track> list,int id){
        for(Track t:list){
            if(t.getTrack_id()==id) return t;
        }
        return null;
    }

    public Track playNext(){
        if(!playNextQueue.isEmpty()) return playNextQueue.pop();
        int index=getCurrentIndexInQueue();
        if(index!=-1 && index+1<playbackQueue.size()){
            Track t=playbackQueue.get(index+1);
            currentTrackId=t.getTrack_id();
            return t;
        }
        return null;
    }

    public Track playPrevious(){
        int index=getCurrentIndexInQueue();
        if(index>0){
            Track t=playbackQueue.get(index-1);
            currentTrackId=t.getTrack_id();
            return t;
        }
        return null;
    }

    public Track peekNext(){
        int i=getCurrentIndexInQueue();
        return (i!=-1 && i+1<playbackQueue.size())?playbackQueue.get(i+1):null;
    }

    public Track peekPrevious(){
        int i=getCurrentIndexInQueue();
        return i>0?playbackQueue.get(i-1):null;
    }

    public void addToPlayNextQueue(Track t){ playNextQueue.add(t); }

    public void filterTracks(Set<Integer> trackIds,String playlistName){
        if(Objects.equals(currentPlaylistName,playlistName)) return;
        filteredTracks.clear();
        for(Track t:allTracks){
            if(trackIds.contains(t.getTrack_id())) filteredTracks.add(t);
        }
        currentPlaylistName=playlistName;
        notifyTrackFilterConsumers();
    }

    public void addTrackToQueue(Track t){
        int index=getCurrentIndexInQueue();
        if(index==-1) playbackQueue.add(t);
        else playbackQueue.add(index+1,t);
    }

    public void resetCursor(){ currentTrackId=null; }

    public void clearPlaybackQueue(){
        playbackQueue.clear();
        resetCursor();
    }

    public boolean isIsShuffled(){ return isShuffled.get(); }

    private void notifyTrackConsumers(){
        List<Track> copy=List.copyOf(filteredTracks);
        for(var c:allTrackListeners) c.accept(copy);
    }

    private void notifyQueueConsumers(){
        List<Track> copy=List.copyOf(playbackQueue);
        for(var c:queueListeners) c.accept(copy);
    }

    private void notifyTrackFilterConsumers(){
        List<Track> copy=List.copyOf(filteredTracks);
        for(var c:trackFilterListeners) c.accept(copy);
    }

    private void notifyIndexConsumers(Integer index){
        for(var c:indexListeners) c.accept(index);
    }

    public void addIndexListiner(Consumer<Integer> c){ indexListeners.add(c);}
    public void addListFullListener(Consumer<List<Track>> c){ allTrackListeners.add(c);}
    public void addQueueListeners(Consumer<List<Track>> c){ queueListeners.add(c);}
    public void addTrackFilterListener(Consumer<List<Track>> c){ trackFilterListeners.add(c);}
}


