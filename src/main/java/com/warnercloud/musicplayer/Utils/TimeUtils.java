package com.warnercloud.musicplayer.Utils;

import javafx.util.Duration;

public class TimeUtils {
    private TimeUtils() {}


    public static String formatDuration(Duration duration){
        if (duration == null){
            return "0:00";
        }
        int seconds = (int) Math.floor(duration.toSeconds());
        int minutes = seconds / 60;
        int remainder = seconds % 60;

        return String.format("%d:%02d", minutes, remainder);
    }
}
