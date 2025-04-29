package com.warnercloud.musicplayer.Service;

import com.warnercloud.musicplayer.Model.Track;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileWorkerService {
    private final File playlistFile = new File("C:/Users/eshas/OneDrive/Desktop/Kpop/Playlist/");
    private PlaylistNavigationService playlistNavigationService;

    public void getPlaylists(File playlistFile) {
        for (final File fileEntry : Objects.requireNonNull(playlistFile.listFiles())) {
            if (fileEntry.isDirectory()) {
                getPlaylists(fileEntry);
            } else {
                playlistNavigationService.addTrack(new Track(fileEntry));
            }
        }
        playlistNavigationService.getAllTracks().forEach(playlistNavigationService::addTrack);
    }

}
