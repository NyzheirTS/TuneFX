package com.warnercloud.musicplayer.Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppJsonManager {
    private static final File APP_DIRECTORY = new File("LOCAL_STORAGE/Master");
    private static final String MASTER_NAME = "Home.json";
    private final File masterFile;
    private final ObjectMapper mapper;

    static {
        try{
            if (!APP_DIRECTORY.exists()){
                boolean isCreated = APP_DIRECTORY.mkdirs();
                if (!isCreated){
                    System.out.println("Failed to create master directory");
                }
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public AppJsonManager() {
        this.masterFile = new File(APP_DIRECTORY, MASTER_NAME);
        this.mapper = new ObjectMapper();
        mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
        confirmMasterFile();
    }

    public void confirmMasterFile(){
        try{
            if (!masterFile.exists()){
                try(FileWriter fileWriter = new FileWriter(masterFile)){
                    fileWriter.write("[]");
                }
            }
        } catch( IOException e){
            throw new RuntimeException("Error creating main.json", e);
        }
    }

    public void addEntry(String filePath) {
        try {
            List<MasterCRUD> entries = readEntries();
            entries.add(new MasterCRUD(UUID.randomUUID().toString(), filePath));
            mapper.writeValue(masterFile, entries);
        } catch (IOException e) {
            throw new RuntimeException("Error updating main.json", e);
        }
    }

    public List<MasterCRUD> getAllEntries(){
        try{
            return readEntries();
        } catch (IOException e) {
            throw new RuntimeException("Error reading entries from main.json", e);
        }
    }


    private List<MasterCRUD> readEntries() throws IOException {
        if (!masterFile.exists() || masterFile.length() == 0) {
            return new ArrayList<>();
        }
        return mapper.readValue(masterFile, new TypeReference<List<MasterCRUD>>() {});
    }

    public void syncWithDirectory(File sourceDirectory) {
        if (!sourceDirectory.exists() || !sourceDirectory.isDirectory()) {
            System.out.println("Invalid directory: " + sourceDirectory.getAbsolutePath());
            return;
        }

        try {
            List<MasterCRUD> currentEntries = readEntries();
            List<String> existingPaths = new ArrayList<>();
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
                    currentEntries.add(new MasterCRUD(UUID.randomUUID().toString(), actualPath));
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
                mapper.writeValue(masterFile, updatedEntries);
                System.out.println("Synced entries with directory: " + sourceDirectory.getName());
            } else {
                System.out.println("No changes detected during sync.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Error syncing entries with directory", e);
        }
    }


    public static class MasterCRUD{
        private String uuid;
        private String filePath;

        public MasterCRUD() {}

        public MasterCRUD(String uuid, String filePath) {
            this.uuid = uuid;
            this.filePath = filePath;
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

        @Override
        public String toString() {
            return "Entry{" + "uuid='" + uuid + '\'' + ", filePath='" + filePath + '\'' + '}';
        }
    }

}
