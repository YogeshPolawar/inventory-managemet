package com.quarks.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(
            new Info()
                .title("Quarks Inventory Management")
                .version("1.0")
                .description("API documentation for inventory operations")
        );
    }
}
