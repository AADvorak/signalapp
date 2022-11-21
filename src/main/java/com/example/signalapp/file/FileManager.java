package com.example.signalapp.file;

import com.example.signalapp.SignalApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileManager {

    private static final String MODULES_PATH = "modules/";
    private static final String SIGNALS_PATH = "signals/";

    public void writeModuleToFile(String module, String extension, String data) throws IOException {
        String moduleLowerCase = module.toLowerCase();
        String moduleDir = SignalApplication.RESOURCES_PATH + MODULES_PATH + moduleLowerCase;
        createDirIfNotExists(moduleDir);
        writeStringToFile(data, moduleDir + "/" + moduleLowerCase + "." + extension);
    }

    public String readModuleFromFile(String module, String extension) throws IOException {
        return readStringFromFile(SignalApplication.RESOURCES_PATH + MODULES_PATH +
                module + "/" + module + "." + extension);
    }

    public byte[] readWavFromFile(int userId, int signalId) throws IOException {
        String wavFile = getWavDir(userId, signalId) + "/" + signalId + ".wav";
        if (!Files.exists(Paths.get(wavFile))) {
            return new byte[0];
        }
        return readBytesFromFile(wavFile);
    }

    public void writeWavToFile(int userId, int signalId, byte[] data) throws IOException {
        String wavDir = getWavDir(userId, signalId);
        createDirIfNotExists(wavDir);
        writeBytesToFile(data, wavDir + "/" + signalId + ".wav");
    }

    public void writeBytesToWavFile(int userId, int signalId, AudioFormat format, byte[] bytes) throws IOException {
        String wavDir = getWavDir(userId, signalId);
        createDirIfNotExists(wavDir);
        AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(bytes), format, bytes.length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(wavDir + "/" + signalId + ".wav"));
    }

    public void deleteSignalData(int userId, int signalId) {
        deleteDirRecursively(new File(getWavDir(userId, signalId)));
    }

    private void deleteDirRecursively(File dir) {
        FileSystemUtils.deleteRecursively(dir);
    }

    private void writeStringToFile(String string, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(string);
        }
    }

    private void writeBytesToFile(byte[] bytes, String fileName) throws IOException {
        Files.write(Paths.get(fileName), bytes);
    }

    private String readStringFromFile(String fileName) throws IOException {
        return new String(readBytesFromFile(fileName), StandardCharsets.UTF_8);
    }

    private byte[] readBytesFromFile(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }

    private String getWavDir(int userId, int signalId) {
        return SignalApplication.DATA_PATH + SIGNALS_PATH + userId + "/" + signalId;
    }

    private void createDirIfNotExists(String dir) throws IOException {
        Path path = Paths.get(dir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

}
