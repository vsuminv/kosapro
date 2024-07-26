package com.example.jsonviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@SpringBootApplication
public class JsonViewerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(JsonViewerApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JsonViewerApplication.class);
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> startupListener(Environment environment) {
        return event -> System.out.println("Server port: " + environment.getProperty("server.port"));
    }
}
