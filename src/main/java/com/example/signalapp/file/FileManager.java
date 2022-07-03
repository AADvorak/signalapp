package com.example.signalapp.file;

import com.example.signalapp.SignalApplication;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileManager {

    private static final String MODULES_PATH = "modules/";

    public void writeModuleToFile(String module, String extension, String data) throws IOException {
        String moduleLowerCase = module.toLowerCase();
        String moduleDir = SignalApplication.RESOURCES_PATH + MODULES_PATH + moduleLowerCase;
        Path path = Paths.get(moduleDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        writeStringToFile(data, moduleDir + "/" + moduleLowerCase + "." + extension);
    }

    public String readModuleFromFile(String module, String extension) throws IOException {
        return readStringFromFile(SignalApplication.RESOURCES_PATH + MODULES_PATH +
                module + "/" + module + "." + extension);
    }

    private void writeStringToFile(String string, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(string);
        }
    }

    private String readStringFromFile(String fileName) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(fileName));
        return new String(encoded, StandardCharsets.UTF_8);
    }

}
