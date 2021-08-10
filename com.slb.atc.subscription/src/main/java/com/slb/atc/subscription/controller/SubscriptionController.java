package com.slb.atc.subscription.controller;

import com.slb.atc.subscription.dto.ErrorMessageDto;
import com.slb.atc.subscription.dto.SubscriptionDto;
import com.slb.atc.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscription")
@CrossOrigin(origins = "http://localhost:9000")
@Validated
public class SubscriptionController {

  @Autowired SubscriptionService subscriptionService;

  @GetMapping(value = "/getById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      operationId = "getById",
      summary = "Get by id",
      description = "Return a subscription by id")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request: Invalid subscription id",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found: Subscription not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            })
      })
  public ResponseEntity<SubscriptionDto> getById(@PathVariable("id") long id) {
    SubscriptionDto subscriptionDto = subscriptionService.getById(id);
    return new ResponseEntity(subscriptionDto, HttpStatus.OK);
  }

  @GetMapping(value = "/listByEmail/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      operationId = "listByEmail",
      summary = "List by email",
      description = "List all subscrptions for a given email")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request: Invalid email",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found: No subscriptions found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            })
      })
  public ResponseEntity<List<SubscriptionDto>> listByEmail(
      @PathVariable("email") @NotBlank @Email String email) {
    List<SubscriptionDto> subscriptionDtos = subscriptionService.listByEmail(email);
    return new ResponseEntity(subscriptionDtos, HttpStatus.OK);
  }

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      operationId = "create",
      summary = "Create subscription",
      description = "Create new subscription")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = SubscriptionDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request: Invalid subscription data or flagForConsent false",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found: Subscription not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            })
      })
  public ResponseEntity<?> create(@Valid @RequestBody SubscriptionDto subscriptionDto) {
    //		if (!subscriptionDto.getFlagForConsent()) {
    //			throw new BadRequestException("Only requests with fieldFlagForConsent are accepted.");
    //		}
    SubscriptionDto subscriptionDtoWithId = subscriptionService.create(subscriptionDto);
    return new ResponseEntity(subscriptionDtoWithId, HttpStatus.CREATED);
  }

  // TODO: Consider using PUT instead of GET
  @GetMapping(value = "/cancel/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      operationId = "cancel",
      summary = "Cancel subscription",
      description = "Cancel a subscription")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "OK: Subscription successfully cancelled"),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request: Invalid subscription id or subscription already cancelled",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found: Subscription not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ErrorMessageDto.class))
            })
      })
  public ResponseEntity<?> cancel(@PathVariable("id") long id) {
    subscriptionService.cancel(id);
    return new ResponseEntity(HttpStatus.OK);
  }
}
