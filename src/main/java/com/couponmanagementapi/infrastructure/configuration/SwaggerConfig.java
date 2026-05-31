package com.couponmanagementapi.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI couponManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coupon Management API")
                        .description("API para gerenciamento de cupons de desconto")
                        .version("v1.0.0"));
    }
}
