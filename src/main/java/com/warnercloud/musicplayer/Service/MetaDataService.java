package com.warnercloud.musicplayer.Service;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.warnercloud.musicplayer.Model.Track;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MetaDataService {

    private final ExecutorService executor = Executors.newFixedThreadPool(4);  // Keep alive across calls


    public void extractBasicMetadataAsync(List<Track> tracks, Runnable onComplete) {
        CountDownLatch latch = new CountDownLatch(tracks.size());
        for (Track track : tracks) {
            executor.submit(() -> {
                try {
                    Mp3File mp3File = new Mp3File(track.getFilePath());
                    if (mp3File.hasId3v2Tag()) {
                        ID3v2 tag = mp3File.getId3v2Tag();

                        track.setTitle(tag.getTitle());
                        track.setArtist(tag.getArtist());
                        track.setAlbum(tag.getAlbum());
                        track.setGenre(tag.getGenreDescription());
                        track.setDuration(Duration.millis(mp3File.getLengthInMilliseconds()));

                        byte[] imageData = tag.getAlbumImage();
                        if (imageData != null) {
                            // Use a hash or ID to uniquely name the cache file
                            String imageHash = Integer.toHexString(Arrays.hashCode(imageData));
                            File imgFile = new File("LOCAL_STORAGE/cover-cache/" + imageHash + ".jpg");

                            if (!imgFile.exists()) {
                                BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageData));
                                BufferedImage resized = resizeTo93x93(original);
                                ImageIO.write(resized, "jpg", imgFile);
                            }

                            track.setCover(imgFile.toURI().toString());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Metadata error: " + track.getFilePath() + " - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        new Thread(() -> {
            try{
                latch.await();
                Platform.runLater(onComplete);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public static BufferedImage resizeTo93x93(BufferedImage originalImage) {
        int targetWidth = 93;
        int targetHeight = 93;

        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();

        // Enable high quality rendering
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        return resizedImage;
    }


    public void shutdown() {
        executor.shutdown();
    }
}
