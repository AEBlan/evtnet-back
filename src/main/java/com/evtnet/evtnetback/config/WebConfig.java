package com.evtnet.evtnetback.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Value("${app.uploads.base-dir}")
  private String baseDir;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Todo lo que esté en <baseDir>/ será accesible como /uploads/**
    String location = "file:" + (baseDir.endsWith("/") ? baseDir : baseDir + "/");
    registry.addResourceHandler("/uploads/**")
            .addResourceLocations(location);
  }
}
