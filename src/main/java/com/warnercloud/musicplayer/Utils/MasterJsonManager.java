package com.warnercloud.musicplayer.Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.warnercloud.musicplayer.Model.Master;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class MasterJsonManager extends JsonManager<Master> {
    private static final File APP_DIRECTORY = new File("LOCAL_STORAGE/Master");
    private static final String MASTER_NAME = "Home.json";
    private Set<String> seenHashes = new HashSet<>();

    public MasterJsonManager() {
        super(new File(APP_DIRECTORY, MASTER_NAME));
    }

    @Override
    protected TypeReference<List<Master>> getTrackTypeReference() {
        return new TypeReference<List<Master>>() {};
    }

    public void addEntry(String filePath){
        Master masterCRUD = new Master(UUID.randomUUID().toString(), dateFormat.format(new Date()), filePath, 0, getFileMD5(filePath));
        addEntry(masterCRUD);
    }

    public void updatePlaycount(String id, int playcount) {
        List<Master> entries = getAllEntries();


        for (Master entry : entries) {
            if (entry.getUuid().equals(id)) {
                entry.setPlayCount(playcount + entry.getPlayCount());
                break;
            }
        }
        try {
            mapper.writeValue(file, entries);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void syncWithDirectory(File sourceDirectory) {
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            System.out.println("Invalid directory: " + sourceDirectory.getAbsolutePath());
            return;
        }

        List<Master> currentEntries = getAllEntries();
        Set<String> existingPaths = new HashSet<>();
        for (Master entry : currentEntries) {
            existingPaths.add(entry.getFilePath());
        }

        File[] files = sourceDirectory.listFiles();
        if (files == null) files = new File[0];

        List<String> actualFilePaths = new ArrayList<>();
        for (File file : files) {
            actualFilePaths.add(file.getAbsolutePath());
        }

        boolean changed = false;

        // Add new files not in JSON
        for (String actualPath : actualFilePaths) {
            if (!existingPaths.contains(actualPath)) {
                currentEntries.add(new Master(UUID.randomUUID().toString(), dateFormat.format(new Date()), actualPath, 0, getFileMD5(actualPath)));
                changed = true;
            }
        }

        // Remove entries that no longer exist
        List<Master> updatedEntries = new ArrayList<>();
        for (Master entry : currentEntries) {
            if (actualFilePaths.contains(entry.getFilePath())) {
                updatedEntries.add(entry);
            } else {
                changed = true;
            }
        }

        if (changed) {
            try {
                mapper.writeValue(file, updatedEntries);
                System.out.println("Synced Master Library with directory: " + sourceDirectory.getName());
            } catch (Exception e){
                throw new RuntimeException("Failed to sync Master Library", e);
            }
        } else {
            System.out.println("No changes detected during sync.");
        }
    }

}
