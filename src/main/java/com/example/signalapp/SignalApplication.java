package com.example.signalapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SignalApplication {
    
        public static String RESOURCES_PATH = "signalappres/";
        
	public static void main(String[] args) {
                if (args.length > 0) RESOURCES_PATH = args[0];
                SpringApplication.run(SignalApplication.class, args);
	}

}
