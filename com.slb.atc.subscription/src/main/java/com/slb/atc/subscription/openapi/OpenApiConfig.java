package com.slb.atc.subscription.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Subscription REST API")
                .description("Subscription REST API documented with OpenAPI 3.")
                .version("0.0.1")
                .contact(new Contact().name("Saulo Lasso").url("https://github.com/saulolasso"))
                .license(
                    new License()
                        .name("Apache 2.0")
                        .url("https://github.com/saulolasso/atc/blob/main/LICENSE")));
  }
}
