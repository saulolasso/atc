package com.slb.atc.subscription;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class SubscriptionApplication {

  public static void main(String[] args) {
    SpringApplication.run(SubscriptionApplication.class, args);
  }
}
