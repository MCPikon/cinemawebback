package com.mcpikon.cinemawebback.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${com.mcpikon.cinemawebback.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("javierpiconpastor@gmail.com");
        contact.setName("MCPikon");
        contact.setUrl("https://javier-picon.vercel.app");

        License apacheLicense = new License().name("Apache License 2.0").url("https://choosealicense.com/licenses/apache-2.0/");

        Info info = new Info()
                .title("CinemaWebBack")
                .version("1.1.0")
                .description("An API of movies, series and it's reviews.")
                .contact(contact)
                .license(apacheLicense);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
