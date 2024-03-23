package link.signalapp.file;

import link.signalapp.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class FileManager {

    private static final String SIGNALS_PATH = "signals/";

    private final ApplicationProperties applicationProperties;

    public byte[] readWavFromFile(int userId, int signalId) throws IOException {
        String wavFile = getWavDir(userId, signalId) + "/" + signalId + ".wav";
        if (!Files.exists(Paths.get(wavFile))) {
            return new byte[0];
        }
        return readBytesFromFile(wavFile);
    }

    public void writeWavToFile(int userId, int signalId, byte[] bytes) throws IOException {
        String wavDir = getWavDir(userId, signalId);
        createDirIfNotExists(wavDir);
        writeBytesToFile(bytes, wavDir + "/" + signalId + ".wav");
    }

    public void deleteSignalData(int userId, int signalId) {
        deleteDirRecursively(new File(getWavDir(userId, signalId)));
    }

    public void deleteAllUserData(int userId) {
        deleteDirRecursively(new File(applicationProperties.getDataPath() + SIGNALS_PATH + userId));
    }

    public void deleteDirRecursively(File dir) {
        FileSystemUtils.deleteRecursively(dir);
    }

    private void writeBytesToFile(byte[] bytes, String fileName) throws IOException {
        Files.write(Paths.get(fileName), bytes);
    }

    private byte[] readBytesFromFile(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }

    private String getWavDir(int userId, int signalId) {
        return applicationProperties.getDataPath() + SIGNALS_PATH + userId + "/" + signalId;
    }

    private void createDirIfNotExists(String dir) throws IOException {
        Path path = Paths.get(dir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }
}
