package com.slb.atc.mail;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class MailApplication {

  public static void main(String[] args) {
    SpringApplication.run(MailApplication.class, args);
  }
}
