package com.warnercloud.musicplayer.Model;

import javafx.scene.image.Image;

import javafx.util.Duration;

public class Track {
    private String ID;
    private String title;
    private String artist;
    private String album;
    private String albumArtist;
    private String genre;
    private String year;
    private String trackNumber;
    private String trackCount;
    private Integer discNumber;
    private Integer discCount;
    private Duration duration;
    private Image cover;
    private final String filePath;
    private Integer playCount = 0;

    public Track(String filePath, String index) {
        this.filePath = filePath;
        this.ID = index;
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

    public String getAlbumArtist() { return albumArtist; }
    public void setAlbumArtist(String albumArtist) { this.albumArtist = albumArtist; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getTrackNumber() { return trackNumber; }
    public void setTrackNumber(String trackNumber) { this.trackNumber = trackNumber; }

    public String getTrackCount() { return trackCount; }
    public void setTrackCount(String trackCount) { this.trackCount = trackCount; }

    public Integer getDiscNumber() { return discNumber; }
    public void setDiscNumber(Integer discNumber) { this.discNumber = discNumber; }

    public Integer getDiscCount() { return discCount; }
    public void setDiscCount(Integer discCount) { this.discCount = discCount; }

    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }

    public Image getCover() { return cover; }
    public void setCover(Image cover) { this.cover = cover; }

    public String getFilePath() { return filePath; }

    public String getIndex() {
        return ID;
    }

    public void setIndex(String index) {
        this.ID = index;
    }

    public void incrementPlayCount() {playCount++;}
    public Integer getPlayCount() { return playCount; }

    // Optional toString() for debugging
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", artist, title, album);
    }
}
