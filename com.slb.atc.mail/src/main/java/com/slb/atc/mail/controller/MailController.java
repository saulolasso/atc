package com.slb.atc.mail.controller;

import com.slb.atc.mail.dto.ErrorMessageDto;
import com.slb.atc.mail.service.MailService;
import com.slb.atc.subscription.dto.SubscriptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: This REST API is not a requirement, but has been implemented for testing and documentation
@RestController
@RequestMapping("/mail")
@Validated
public class MailController {

  @Autowired MailService subscriptionService;

  @PostMapping(value = "/sendSubscriptionCreatedNotification")
  @Operation(
      operationId = "sendSubscriptionCreatedNotification",
      summary = "Send notification for new subscription",
      description = "Send notification when a subscription to a newsletter is created")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubscriptionDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request: Invalid subscription data",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            })
      })
  public ResponseEntity<?> sendSubscriptionCreatedNotification(
      @Valid @RequestBody SubscriptionDto subscriptionDto) {
    subscriptionService.sendSubscriptionCreatedNotification(subscriptionDto);
    return new ResponseEntity(HttpStatus.OK);
  }

  @PostMapping(value = "/sendSubscriptionCancelledNotification")
  @Operation(
      operationId = "sendSubscriptionCancelledNotification",
      summary = "Send notification for cancelled subscription",
      description = "Send notification when a subscription to a newsletter is cancelled")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubscriptionDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request: Invalid subscription data",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            })
      })
  public ResponseEntity<?> sendSubscriptionCancelledNotification(
      @Valid @RequestBody SubscriptionDto subscriptionDto) {
    subscriptionService.sendSubscriptionCancelledNotification(subscriptionDto);
    return new ResponseEntity(HttpStatus.OK);
  }
}
