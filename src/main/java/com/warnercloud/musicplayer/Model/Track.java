package com.warnercloud.musicplayer.Model;

import javafx.scene.image.Image;

import javafx.util.Duration;

public class Track {
    private String title;
    private String artist;
    private String album;
    private String albumArtist;
    private String genre;
    private Integer year;
    private Integer trackNumber;
    private Integer trackCount;
    private Integer discNumber;
    private Integer discCount;
    private Duration duration;
    private Image cover;
    private String filePath;

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

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getTrackNumber() { return trackNumber; }
    public void setTrackNumber(Integer trackNumber) { this.trackNumber = trackNumber; }

    public Integer getTrackCount() { return trackCount; }
    public void setTrackCount(Integer trackCount) { this.trackCount = trackCount; }

    public Integer getDiscNumber() { return discNumber; }
    public void setDiscNumber(Integer discNumber) { this.discNumber = discNumber; }

    public Integer getDiscCount() { return discCount; }
    public void setDiscCount(Integer discCount) { this.discCount = discCount; }

    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }

    public Image getCover() { return cover; }
    public void setCover(Image cover) { this.cover = cover; }

    public String getFilePath() { return filePath; }

    // Optional toString() for debugging
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", artist, title, album);
    }
}
