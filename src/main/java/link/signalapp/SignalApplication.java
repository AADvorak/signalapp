package link.signalapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SignalApplication {

    public static String FRONTEND_OLD_PATH = "frontend-old/";

    public static void main(String[] args) {
        if (args.length > 0) {
            FRONTEND_OLD_PATH = args[0];
        }
        SpringApplication.run(SignalApplication.class, args);
    }

}
