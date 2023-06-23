/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package link.signalapp;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @author anton
 */
@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final ApplicationProperties properties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (properties.isDebug()) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:3000")
                    .allowCredentials(true)
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*");
        }
    }

}
