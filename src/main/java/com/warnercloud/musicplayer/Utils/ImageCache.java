package com.warnercloud.musicplayer.Utils;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImageCache {
    private static final Map<String, Image> cache = new ConcurrentHashMap<>();

    private ImageCache() {}

    public static Image get(String url) {

        Image image = cache.computeIfAbsent(url,
                key -> new Image(key, true));

        if(image.isError()){
            image.getException().printStackTrace();
        }

        image.errorProperty().addListener((obs, old, error) -> {
            if (error) {
                cache.remove(url);
            }
        });

        return image;
    }

    public static void clear(){
        cache.clear();
    }
}
