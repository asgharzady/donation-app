package com.donation.donation_app.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://aml.appopay.com",
                        "https://aml-backend.appopay.com",
                        "https://aml-backend.chenchenapp.com",
                        "https://aml.chenchenapp.com",
                        "http://localhost:8085"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);  // If you're sending credentials like cookies or Authorization tokens
    }
}
