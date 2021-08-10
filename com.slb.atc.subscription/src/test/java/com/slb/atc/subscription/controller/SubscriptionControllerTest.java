package com.slb.atc.subscription.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.slb.atc.subscription.dto.SubscriptionDto;
import com.slb.atc.subscription.enums.Gender;
import com.slb.atc.subscription.error.BadRequestException;
import com.slb.atc.subscription.error.NotFoundException;
import com.slb.atc.subscription.service.SubscriptionService;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// TODO: Split into several test classes
@WebMvcTest(SubscriptionController.class)
public class SubscriptionControllerTest {

  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

  @MockBean SubscriptionService subscriptionService;

  @Autowired private MockMvc mvc;

  @Test
  @DisplayName("Test should pass when Http status is 200 and body contains a subscription")
  public void getByIdWithValidId() throws Exception {
    Long id = 1l;
    SubscriptionDto subscriptionDto =
        new SubscriptionDto(
            id,
            "paul@paul.com",
            "paul",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            1L,
            false);
    Mockito.when(subscriptionService.getById(1L)).thenReturn(subscriptionDto);

    mvc.perform(get("/subscription/getById/" + id))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("id", Matchers.is(1)))
        .andExpect(jsonPath("email", Matchers.is("paul@paul.com")))
        .andExpect(jsonPath("firstname", Matchers.is("paul")))
        .andExpect(jsonPath("gender", Matchers.is(Gender.GENDER_MALE.toString())))
        .andExpect(jsonPath("dateOfBirth", Matchers.is("2000-12-31T23:00:00.000+00:00")))
        .andExpect(jsonPath("flagForConsent", Matchers.is(true)))
        .andExpect(jsonPath("newsletterId", Matchers.is(1)))
        .andExpect(jsonPath("cancelled", Matchers.is(false)));
  }

  @Test
  @DisplayName("Test should pass when Http status is 404 and error message is included")
  public void getByIdWithNotExistingId() throws Exception {
    Long id = 4L;
    Mockito.when(subscriptionService.getById(id))
        .thenThrow(new NotFoundException("Subscription with id " + id + " doesn't exist."));

    mvc.perform(get("/subscription/getById/" + id))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("errors[0]", Matchers.is("Subscription with id " + id + " doesn't exist.")));
  }

  @Test
  @DisplayName("Test should pass when Http status is 200 and body contains list of subscriptions")
  public void getByEmailWithExistingEmail() throws Exception {
    String email = "paul@paul.com";
    SubscriptionDto subscriptionDto1 =
        new SubscriptionDto(
            1L,
            email,
            "paul",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            1L,
            false);
    SubscriptionDto subscriptionDto2 =
        new SubscriptionDto(
            2L,
            email,
            "paul",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            2L,
            true);
    Mockito.when(subscriptionService.listByEmail(email))
        .thenReturn(Arrays.asList(subscriptionDto1, subscriptionDto2));

    mvc.perform(get("/subscription/listByEmail/" + email))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("[0].id", Matchers.is(1)))
        .andExpect(jsonPath("[0].email", Matchers.is("paul@paul.com")))
        .andExpect(jsonPath("[0].firstname", Matchers.is("paul")))
        .andExpect(jsonPath("[0].gender", Matchers.is(Gender.GENDER_MALE.toString())))
        .andExpect(jsonPath("[0].dateOfBirth", Matchers.is("2000-12-31T23:00:00.000+00:00")))
        .andExpect(jsonPath("[0].flagForConsent", Matchers.is(true)))
        .andExpect(jsonPath("[0].newsletterId", Matchers.is(1)))
        .andExpect(jsonPath("[0].cancelled", Matchers.is(false)))
        .andExpect(jsonPath("[1].id", Matchers.is(2)))
        .andExpect(jsonPath("[1].email", Matchers.is("paul@paul.com")))
        .andExpect(jsonPath("[1].firstname", Matchers.is("paul")))
        .andExpect(jsonPath("[1].gender", Matchers.is(Gender.GENDER_MALE.toString())))
        .andExpect(jsonPath("[1].dateOfBirth", Matchers.is("2000-12-31T23:00:00.000+00:00")))
        .andExpect(jsonPath("[1].flagForConsent", Matchers.is(true)))
        .andExpect(jsonPath("[1].newsletterId", Matchers.is(2)))
        .andExpect(jsonPath("[1].cancelled", Matchers.is(true)));
  }

  @Test
  @DisplayName("Test should pass when Http status is 404 and error message is included")
  public void getByEmailWithNonExistingEmail() throws Exception {
    String email = "jane@jane.com";
    Mockito.when(subscriptionService.listByEmail(email))
        .thenThrow(
            new NotFoundException("There aren't any subscriptions with email " + email + "."));

    mvc.perform(get("/subscription/listByEmail/" + email))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "errors[0]",
                Matchers.is("There aren't any subscriptions with email " + email + ".")));
  }

  @Test
  @DisplayName("Test should pass when Http status is 400 and error message is correct")
  public void getByEmailWithInvalidEmail() throws Exception {
    String email = "jane";

    mvc.perform(get("/subscription/listByEmail/" + email))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "errors[0]",
                Matchers.is("listByEmail.email: must be a well-formed email address")));
  }

  @Test
  @DisplayName("Test should pass when Http status is 200 and a subscription with id is returned")
  public void createValidSubscription() throws Exception {
    SubscriptionDto subscriptionDto =
        new SubscriptionDto(
            1L,
            "paul@paul.com",
            "paul",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            1L,
            false);
    Mockito.when(subscriptionService.create(ArgumentMatchers.any())).thenReturn(subscriptionDto);

    // TODO: Consider using GSON or Jackson to map an object to a JSON instead of writing the string
    mvc.perform(
            post("/subscription/create/")
                .content(
                    "{\"id\": 0, \"email\": \"paul@paul.com\", "
                        + "\"firstname\": \"paul\", \"gender\": \"GENDER_MALE\", "
                        + "\"dateOfBirth\": \"2000-12-31T23:00:00.000+00:00\", "
                        + "\"flagForConsent\": true, \"newsletterId\": 1, \"cancelled\": false}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("id", Matchers.greaterThan(0)))
        .andExpect(jsonPath("email", Matchers.is("paul@paul.com")))
        .andExpect(jsonPath("firstname", Matchers.is("paul")))
        .andExpect(jsonPath("gender", Matchers.is(Gender.GENDER_MALE.toString())))
        .andExpect(jsonPath("dateOfBirth", Matchers.is("2000-12-31T23:00:00.000+00:00")))
        .andExpect(jsonPath("flagForConsent", Matchers.is(true)))
        .andExpect(jsonPath("newsletterId", Matchers.is(1)))
        .andExpect(jsonPath("cancelled", Matchers.is(false)));
  }

  @Test
  @DisplayName("Test should pass when Http status is 200 and error message is correct")
  public void createSubscriptionWithMissingNonMandatoryFields() throws Exception {
    SubscriptionDto subscriptionDto =
        new SubscriptionDto(
            1L, "paul@paul.com", null, null, dateFormatter.parse("01-01-2001"), true, 1L, false);
    Mockito.when(subscriptionService.create(ArgumentMatchers.any())).thenReturn(subscriptionDto);

    // TODO: Consider using GSON or Jackson to map an object to a JSON instead of writing the string
    mvc.perform(
            post("/subscription/create/")
                .content(
                    "{\"id\": 0, \"email\": \"paul@paul.com\", "
                        + "\"dateOfBirth\": \"2000-12-31T23:00:00.000+00:00\", "
                        + "\"flagForConsent\": true, \"newsletterId\": 1, \"cancelled\": false}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("id", Matchers.greaterThan(0)))
        .andExpect(jsonPath("email", Matchers.is("paul@paul.com")))
        .andExpect(jsonPath("firstname").value(IsNull.nullValue()))
        .andExpect(jsonPath("gender").value(IsNull.nullValue()))
        .andExpect(jsonPath("dateOfBirth", Matchers.is("2000-12-31T23:00:00.000+00:00")))
        .andExpect(jsonPath("flagForConsent", Matchers.is(true)))
        .andExpect(jsonPath("newsletterId", Matchers.is(1)))
        .andExpect(jsonPath("cancelled", Matchers.is(false)));
  }

  @Test
  @DisplayName("Test should pass when Http status is 400 and error message is correct")
  public void createSubscriptionWithMissingMandatoryFieldEmail() throws Exception {
    mvc.perform(
            post("/subscription/create/")
                .content(
                    "{\"id\": 0, "
                        + "\"firstname\": \"paul\", \"gender\": \"GENDER_MALE\", "
                        + "\"dateOfBirth\": \"2000-12-31T23:00:00.000+00:00\", "
                        + "\"flagForConsent\": true, \"newsletterId\": 1, \"cancelled\": false}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errors[0]", Matchers.startsWith("Error in field email")));
  }

  @Test
  @DisplayName("Test should pass when Http status is 400 and error message is correct")
  public void createSubscriptionWithMissingMandatoryFieldDateOfBirth() throws Exception {
    mvc.perform(
            post("/subscription/create/")
                .content(
                    "{\"id\": 0, \"email\": \"paul@paul.com\", "
                        + "\"firstname\": \"paul\", \"gender\": \"GENDER_MALE\", "
                        + "\"flagForConsent\": true, \"newsletterId\": 1, \"cancelled\": false}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errors[0]", Matchers.startsWith("Error in field dateOfBirth: ")));
  }

  @Test
  @DisplayName("Test should pass when Http status is 400 and error message is correct")
  public void createSubscriptionWithMissingMandatoryFieldFlagForConsent() throws Exception {
    mvc.perform(
            post("/subscription/create/")
                .content(
                    "{\"id\": 0, \"email\": \"paul@paul.com\", "
                        + "\"firstname\": \"paul\", \"gender\": \"GENDER_MALE\", "
                        + "\"dateOfBirth\": \"2000-12-31T23:00:00.000+00:00\", "
                        + "\"newsletterId\": 1, \"cancelled\": false}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("errors[0]").value(Matchers.startsWith("Error in field flagForConsent: ")));
  }

  @Test
  @DisplayName("Test should pass when Http status is 400 and error message is correct")
  public void createSubscriptionWithMissingMandatoryFieldNewsletterId() throws Exception {
    mvc.perform(
            post("/subscription/create/")
                .content(
                    "{\"id\": 0, \"email\": \"paul@paul.com\", "
                        + "\"firstname\": \"paul\", \"gender\": \"GENDER_MALE\", "
                        + "\"dateOfBirth\": \"2000-12-31T23:00:00.000+00:00\", "
                        + "\"flagForConsent\": true, \"cancelled\": false}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("errors[0]").value(Matchers.startsWith("Error in field newsletterId: ")));
  }

  @Test
  @DisplayName("Test should pass when Http status is 400 and error message is correct")
  public void createSubscriptionWithInvalidEmail() throws Exception {
    mvc.perform(
            post("/subscription/create/")
                .content(
                    "{\"id\": 0, \"email\": \"paul.paul.com\", "
                        + "\"firstname\": \"paul\", \"gender\": \"GENDER_MALE\", "
                        + "\"dateOfBirth\": \"2000-12-31T23:00:00.000+00:00\", "
                        + "\"flagForConsent\": true, \"newsletterId\": 1, \"cancelled\": false}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("errors[0]").value(Matchers.startsWith("Error in field email: ")));
  }

  @Test
  @DisplayName("Test should pass when Http status is 400 and error message is correct")
  public void createSubscriptionWithFlagForConsentFalse() throws Exception {
    mvc.perform(
            post("/subscription/create/")
                .content(
                    "{\"id\": 0, \"email\": \"paul@paul.com\", "
                        + "\"firstname\": \"paul\", \"gender\": \"GENDER_MALE\", "
                        + "\"dateOfBirth\": \"2000-12-31T23:00:00.000+00:00\", "
                        + "\"flagForConsent\": false, \"newsletterId\": 1, \"cancelled\": false}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("errors[0]").value(Matchers.startsWith("Error in field flagForConsent")));
  }

  @Test
  @DisplayName("Test should pass when Http status is 400 and error message is correct")
  public void createDuplicateSubscriptionWithSameEmailAndNewsletterAsExisting() throws Exception {
    String email = "paul@paul.com";
    String newsletterId = "1";
    Mockito.when(subscriptionService.create(ArgumentMatchers.any()))
        .thenThrow(
            new BadRequestException(
                "Email "
                    + email
                    + "is already subscribed to newsletter with id "
                    + newsletterId
                    + "."));

    mvc.perform(
            post("/subscription/create/")
                .content(
                    "{\"id\": 0, \"email\": \"paul@paul.com\", "
                        + "\"firstname\": \"paul\", \"gender\": \"GENDER_MALE\", "
                        + "\"dateOfBirth\": \"2000-12-31T23:00:00.000+00:00\", "
                        + "\"flagForConsent\": true, \"newsletterId\": 1, \"cancelled\": false}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("errors[0]")
                .value(
                    Matchers.is(
                        "Email "
                            + email
                            + "is already subscribed to newsletter with id "
                            + newsletterId
                            + ".")));
  }

  @Test
  @DisplayName("Test should pass when Http status is ")
  public void cancelWithExisitingId() throws Exception {
    Long id = 1L;
    Mockito.doNothing().when(subscriptionService).cancel(id);

    mvc.perform(get("/subscription/cancel/" + id)).andExpect(status().isOk());
  }

  @Test
  @DisplayName("Test should pass when Http status is is 404 and error message is correct")
  public void cancelWithNonExisitingId() throws Exception {
    Long id = 4L;
    Mockito.doThrow(new NotFoundException("Subscription with id " + id + " doesn't exist."))
        .when(subscriptionService)
        .cancel(id);

    mvc.perform(get("/subscription/cancel/" + id))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("errors[0]", Matchers.is("Subscription with id " + id + " doesn't exist.")));
  }

  @Test
  @DisplayName("Test should pass when Http status is is 404 and error message is correct")
  public void cancelAlreadyCancelled() throws Exception {
    Long id = 2L;
    Mockito.doThrow(
            new BadRequestException("Subscription with id " + id + " is already cancelled."))
        .when(subscriptionService)
        .cancel(id);

    mvc.perform(get("/subscription/cancel/" + id))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "errors[0]", Matchers.is("Subscription with id " + id + " is already cancelled.")));
  }
}
