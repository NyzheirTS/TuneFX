package com.warnercloud.musicplayer.Model;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Date;

public class Track {
    private int track_id;
    private Date date_created;
    private String genre_top;
    private int listens;
    private int duration;
    private String title;
    private String artist_name;
    private String album_title;
    private int playorder;

    private final IntegerProperty playCount = new SimpleIntegerProperty(0);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);



    // --- Getters and Setters ---


    public int getTrack_id() {
        return track_id;
    }

    public void setTrack_id(int track_id) {
        this.track_id = track_id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public String getGenre_top() {
        return genre_top;
    }

    public void setGenre_top(String genre_top) {
        this.genre_top = genre_top;
    }

    public int getListens() {
        return listens;
    }

    public void setListens(int listens) {
        this.listens = listens;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title == null ? "N/A" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist_name() {
        return artist_name == null ? "N/A" : artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public String getAlbum_title() {
        return album_title == null ? "Single" : album_title;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public int getPlayorder() {
        return playorder;
    }

    public void setPlayorder(int playorder) {
        this.playorder = playorder;
    }

    public BooleanProperty playingProperty() {return playing;}
    public boolean isPlaying() {return playing.get();}
    public void setPlaying(boolean playing) {this.playing.set(playing);}

    public IntegerProperty playCountProperty() {return playCount;}
    public void incrementPlayCount() {Platform.runLater(() -> playCount.set(playCount.get() + 1));}
    public int getPlayCount() { return playCount.get(); }

}
