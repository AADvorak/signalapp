/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.signalapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 *
 * @author anton
 */
@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {
    
    private static final String[] RESOURCES_FOLDERS = {"pages", "js", "html", "img", "css", "lib"};
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        for (String folder: RESOURCES_FOLDERS) {
            registry
                .addResourceHandler("/" + folder + "/**")
                .addResourceLocations("file:" + SignalApplication.RESOURCES_PATH + folder + "/");
        }

        registry
                .addResourceHandler("/")
                .addResourceLocations("file:" + SignalApplication.RESOURCES_PATH + "pages/index.html");
        
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:3000")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }

}
