package com.example.signalapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SignalApplication {

    public static String RESOURCES_PATH = "signalappres/";
    public static String DATA_PATH = "signalappdata/";
    public static String FRONTEND_PATH = "signalapp-frontend/";

    public static void main(String[] args) {
        if (args.length > 0) {
            RESOURCES_PATH = args[0];
        }
        if (args.length > 1) {
            DATA_PATH = args[1];
        }
        if (args.length > 2) {
            FRONTEND_PATH = args[2];
        }
        SpringApplication.run(SignalApplication.class, args);
    }

}
