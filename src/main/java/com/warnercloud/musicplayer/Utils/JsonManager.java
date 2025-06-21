package com.warnercloud.musicplayer.Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class JsonManager<T> {

    protected final ObjectMapper mapper;
    protected final File file;
    protected final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

    public JsonManager(final File file) {
        this.file = file;
        this.mapper = new ObjectMapper();
        mapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);

    }

    protected abstract TypeReference<List<T>> getTrackTypeReference();

    protected void confirmFile(){
        try {
            if(!file.exists()){
                File parent = new File(file.getParent());
                if(!parent.exists()){
                    parent.mkdirs();
                }
                try(FileWriter fw = new FileWriter(file)){
                    fw.write("[]");
                }
            }
        } catch(IOException e){
            throw new RuntimeException("Error creating" + file.getName(), e);
        }
    }
    public void addEntry(T entry){
        try{
            List<T> entries = readEntries();
            entries.add(entry);
            mapper.writeValue(file, entries);
        } catch (IOException e){
            throw new RuntimeException("Error Updating" + file.getName(), e);
        }
    }

    public List<T> getAllEntries() {
        try {
            return readEntries();
        } catch (IOException e){
            throw new RuntimeException("Error reading entries from " + file.getName(), e);
        }
    }

    protected List<T> readEntries() throws IOException {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        return mapper.readValue(file, getTrackTypeReference());
    }

    public static String getFileMD5(String filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (FileInputStream fis = new FileInputStream(filePath)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, bytesRead);
                }
            }
            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

}
