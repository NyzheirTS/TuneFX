package com.warnercloud.musicplayer.Model;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

public class Track {
    private final String UUID;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private Duration duration;
    private String coverPath;
    private String date;
    private final String filePath;
    private final IntegerProperty playCount = new SimpleIntegerProperty(0);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);

    public Track(String filePath, String index, int playCount, String date) {
        this.filePath = filePath;
        this.UUID = index;
        this.date = date;
        this.playCount.set(playCount);
    }

    public Track(String uuid, String filePath) {
        UUID = uuid;
        this.filePath = filePath;
    }

    // --- Getters and Setters ---

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }

    public String getCover() { return coverPath; }
    public void setCover(String cover) { this.coverPath = cover; }

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    public String getFilePath() { return filePath; }

    public String getUUID() {
        return UUID;
    }



    public BooleanProperty playingProperty() {return playing;}
    public boolean isPlaying() {return playing.get();}
    public void setPlaying(boolean playing) {this.playing.set(playing);}

    public IntegerProperty playCountProperty() {return playCount;}
    public void incrementPlayCount() {Platform.runLater(() -> playCount.set(playCount.get() + 1));}
    public int getPlayCount() { return playCount.get(); }

    @Override
    public String toString() {
        return "Track{" +
                "UUID='" + UUID + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", cover=" + coverPath +
                ", filePath='" + filePath + '\'' +
                ", playCount=" + playCount +
                '}';
    }
}
