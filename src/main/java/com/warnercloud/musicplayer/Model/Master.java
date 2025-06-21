package com.warnercloud.musicplayer.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Master {
    private String checkSum;
    private String uuid;
    private String date;
    private String filePath;
    private final IntegerProperty playCount = new SimpleIntegerProperty();

    public Master() {
    }

    public Master(String uuid, String date, String filePath, int playCount, String checkSum) {
        this.date = date;
        this.uuid = uuid;
        this.filePath = filePath;
        this.playCount.set(playCount);
        this.checkSum = checkSum;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPlayCount() {
        return playCount.get();
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public void setPlayCount(int playCount) {
        this.playCount.set(playCount);
    }
    public IntegerProperty playCountProperty() {return playCount;}

    @Override
    public String toString() {
        return "Entry{" + "uuid='" + uuid + '\'' + ", filePath='" + filePath + '\'' + '}';
    }
}
