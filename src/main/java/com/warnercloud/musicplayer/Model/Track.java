package com.warnercloud.musicplayer.Model;

import javafx.util.Duration;

public class Track {
    private String UUID;
    private String title;
    private String artist;
    private String album;
    private String genre;
    private Duration duration;
    private String coverPath;
    private final String filePath;
    private int playCount;

    public Track(String filePath, String index, int playCount) {
        this.filePath = filePath;
        this.UUID = index;
        this.playCount = playCount;
    }

    public Track(String filePath) {
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

    public String getFilePath() { return filePath; }

    public String getUUID() {
        return UUID;
    }

    public void setIndex(String index) {
        this.UUID = index;
    }

    public void incrementPlayCount() {playCount++;}
    public Integer getPlayCount() { return playCount; }

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
