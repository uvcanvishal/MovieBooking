package com.moviebooking.movie_booking_monolith.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI movieBookingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Booking API")
                        .description("Backend APIs for Movie/Theater/Show/Seat/Booking")
                        .version("v1.0"))
                .externalDocs(new ExternalDocumentation()
                        .description("Source Code")
                        .url("https://example.com"));
    }
}
