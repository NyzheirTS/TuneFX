package com.warnercloud.musicplayer.Utils;

import java.util.HashMap;
import java.util.Map;

public class DedupeHelper {
    private final Map<String, String> pool = new HashMap<>();

    public String dedupe(String input) {
        if (input == null) return null;
        return pool.computeIfAbsent(input, t -> t);
    }
}
