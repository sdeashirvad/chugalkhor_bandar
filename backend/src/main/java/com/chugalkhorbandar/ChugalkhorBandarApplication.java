package com.chugalkhorbandar;

import com.chugalkhorbandar.config.DotEnvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ChugalkhorBandarApplication {

    public static void main(String[] args) {
        DotEnvLoader.applyToSystemProperties();
        SpringApplication.run(ChugalkhorBandarApplication.class, args);
    }
}
