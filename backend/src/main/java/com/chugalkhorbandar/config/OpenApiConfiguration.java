package com.chugalkhorbandar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI chugalkhorOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chugalkhor Bandar API")
                        .description("Read-only API for observing the compiled runtime world")
                        .version("1.0"));
    }
}
