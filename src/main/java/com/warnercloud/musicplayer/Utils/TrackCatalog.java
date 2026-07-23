package com.warnercloud.musicplayer.Utils;

import com.warnercloud.musicplayer.Model.Track;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TrackCatalog {
    private static final TrackCatalog instance = new TrackCatalog();

    private final Map<Integer, Track> trackIdMap = new HashMap<>();

    private TrackCatalog() {}

    public static TrackCatalog getInstance() {
        return instance;
    }

    public void CatalogTracks(Collection<Track> tracks) {
        for(Track track : tracks) {
            trackIdMap.put(track.getTrack_id(), track);
        }
    }

    public Optional<Track> findById(int id) {
        return Optional.ofNullable(trackIdMap.get(id));
    }
}
