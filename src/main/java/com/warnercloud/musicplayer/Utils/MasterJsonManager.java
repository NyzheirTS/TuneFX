package com.warnercloud.musicplayer.Utils;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.util.*;

public class MasterJsonManager extends JsonManager<MasterJsonManager.MasterCRUD> {
    private static final File APP_DIRECTORY = new File("LOCAL_STORAGE/Master");
    private static final String MASTER_NAME = "Home.json";

    public MasterJsonManager() {
        super(new File(APP_DIRECTORY, MASTER_NAME));
    }

    @Override
    protected TypeReference<List<MasterCRUD>> getTrackTypeReference() {
        return new TypeReference<List<MasterCRUD>>() {};
    }

    public void addEntry(String filePath){
        MasterCRUD masterCRUD = new MasterCRUD(UUID.randomUUID().toString(), dateFormat.format(new Date()), filePath, 0);
        addEntry(masterCRUD);
    }

    public void updatePlaycount(String id, int playcount) {
        List<MasterCRUD> entries = getAllEntries();


        for (MasterCRUD entry : entries) {
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

        List<MasterCRUD> currentEntries = getAllEntries();
        Set<String> existingPaths = new HashSet<>();
        for (MasterCRUD entry : currentEntries) {
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
                currentEntries.add(new MasterCRUD(UUID.randomUUID().toString(), dateFormat.format(new Date()), actualPath, 0));
                changed = true;
            }
        }

        // Remove entries that no longer exist
        List<MasterCRUD> updatedEntries = new ArrayList<>();
        for (MasterCRUD entry : currentEntries) {
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

        /*currentEntries.clear();
        existingPaths.clear();
        actualFilePaths.clear();
        updatedEntries.clear();*/
    }


    public static class MasterCRUD{
        private String uuid;
        private String date;
        private String filePath;
        private Integer playCount;

        public MasterCRUD() {}

        public MasterCRUD(String uuid, String date, String filePath, Integer playCount) {
            this.date = date;
            this.uuid = uuid;
            this.filePath = filePath;
            this.playCount = playCount;
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

        public Integer getPlayCount() {
            return playCount;
        }

        public void setPlayCount(Integer playCount) {
            this.playCount = playCount;
        }

        @Override
        public String toString() {
            return "Entry{" + "uuid='" + uuid + '\'' + ", filePath='" + filePath + '\'' + '}';
        }
    }

}
