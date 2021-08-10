package com.slb.atc.subscription.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.slb.atc.subscription.dto.SubscriptionDto;
import com.slb.atc.subscription.entity.Subscription;
import com.slb.atc.subscription.enums.Gender;
import com.slb.atc.subscription.error.BadRequestException;
import com.slb.atc.subscription.error.NotFoundException;
import com.slb.atc.subscription.repository.SubscriptionRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class SubscriptionServiceImplTest {

  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

  @TestConfiguration
  static class SubscriptionServiceImplTestContextConfiguration {
    @Bean
    public SubscriptionService subscriptionService() {
      return new SubscriptionServiceImpl();
    }
  }

  // TODO: Refactor taking code out of the BeforeEach or even doing away with it,
  // Rationale: All the code is executed with each test, but only the code required for each test
  // should be executed
  @BeforeEach
  public void init() throws ParseException {
    Map<Long, Subscription> subscriptions = new HashMap<>();
    subscriptions.put(
        1L,
        new Subscription(
            1L,
            "paul@paul.com",
            "paul",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            1L,
            false));
    subscriptions.put(
        2L,
        new Subscription(
            2L,
            "paul@paul.com",
            "paul",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            2L,
            true));
    subscriptions.put(
        3L,
        new Subscription(
            3L,
            "lucy@lucy.com",
            "luci",
            Gender.GENDER_FEMALE,
            dateFormatter.parse("01-01-2003"),
            true,
            1L,
            false));

    // Mock for test getByIdWithExistingId() and for cancelSubscriptionWithNonExistingId()
    Mockito.when(subscriptionRepository.findById(1L))
        .thenReturn(Optional.of(subscriptions.get(1L)));

    // Mock for test getByIdWithNonExistingId() and for cancelSubscriptionWithNonExistingId()
    Mockito.when(subscriptionRepository.findById(4L)).thenReturn(Optional.empty());

    // Mock for test listByEmailRecordsCorrectlyMapped()
    Mockito.when(subscriptionRepository.findByEmail("paul@paul.com"))
        .thenReturn(
            subscriptions.values().stream()
                .filter(s -> s.getEmail().equals("paul@paul.com"))
                .collect(Collectors.toList()));

    // Mock for test for cancelAlreadyCancelledSubscription()
    Mockito.when(subscriptionRepository.findById(2L))
        .thenReturn(Optional.of(subscriptions.get(2L)));
  }

  @Autowired private SubscriptionService subscriptionService;

  @MockBean private SubscriptionRepository subscriptionRepository;

  @MockBean private KafkaTemplate<String, Object> kafkaTemplate;

  @Test
  @DisplayName("Test should pass when id exists and a correctly mapped SubscriptionDto is returned")
  void getByIdWithExistingId() throws ParseException {
    Long id = 1L;
    SubscriptionDto subscriptionDto = subscriptionService.getById(id);
    assertThat(subscriptionDto.getId()).isEqualTo(1L);
    assertThat(subscriptionDto.getEmail()).isEqualTo("paul@paul.com");
    assertThat(subscriptionDto.getFirstname()).isEqualTo("paul");
    assertThat(subscriptionDto.getGender()).isEqualTo(Gender.GENDER_MALE);
    assertThat(subscriptionDto.getDateOfBirth()).isEqualTo(dateFormatter.parse("01-01-2001"));
    assertThat(subscriptionDto.getFlagForConsent()).isEqualTo(true);
    assertThat(subscriptionDto.getNewsletterId()).isEqualTo(1L);
    assertThat(subscriptionDto.getCancelled()).isEqualTo(false);
  }

  @Test
  @DisplayName("Test should pass when the id doesn't exist and NotFoundException is thrown")
  void getByIdWithNonExistingId() {
    Long id = 4L;
    assertThatThrownBy(
            () -> {
              subscriptionService.getById(id);
            })
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Subscription with id " + id + " doesn't exist.");
  }

  @Test
  @DisplayName("Test should pass when a correctly mapped list is returned")
  void listByEmailRecordsCorrectlyMapped() throws ParseException {
    String email = "paul@paul.com";
    List<SubscriptionDto> subscriptionDtos = subscriptionService.listByEmail(email);
    assertThat(subscriptionDtos.get(0).getId()).isEqualTo(1L);
    assertThat(subscriptionDtos.get(0).getEmail()).isEqualTo("paul@paul.com");
    assertThat(subscriptionDtos.get(0).getFirstname()).isEqualTo("paul");
    assertThat(subscriptionDtos.get(0).getGender()).isEqualTo(Gender.GENDER_MALE);
    assertThat(subscriptionDtos.get(0).getDateOfBirth())
        .isEqualTo(dateFormatter.parse("01-01-2001"));
    assertThat(subscriptionDtos.get(0).getFlagForConsent()).isEqualTo(true);
    assertThat(subscriptionDtos.get(0).getNewsletterId()).isEqualTo(1L);
    assertThat(subscriptionDtos.get(0).getCancelled()).isEqualTo(false);
    assertThat(subscriptionDtos.get(1).getId()).isEqualTo(2L);
    assertThat(subscriptionDtos.get(1).getEmail()).isEqualTo("paul@paul.com");
    assertThat(subscriptionDtos.get(1).getFirstname()).isEqualTo("paul");
    assertThat(subscriptionDtos.get(1).getGender()).isEqualTo(Gender.GENDER_MALE);
    assertThat(subscriptionDtos.get(1).getDateOfBirth())
        .isEqualTo(dateFormatter.parse("01-01-2001"));
    assertThat(subscriptionDtos.get(1).getFlagForConsent()).isEqualTo(true);
    assertThat(subscriptionDtos.get(1).getNewsletterId()).isEqualTo(2L);
    assertThat(subscriptionDtos.get(1).getCancelled()).isEqualTo(true);
  }

  @Test
  @DisplayName(
      "Test should pass when the subscription doesn't exist and a NotFoundException is thrown")
  void cancelSubscriptionWithNotExistingId() {
    Long id = 4L;
    assertThatThrownBy(
            () -> {
              subscriptionService.cancel(id);
            })
        .isInstanceOf(NotFoundException.class)
        .hasMessage("Subscription with id " + id + " doesn't exist.");
  }

  @Test
  @DisplayName(
      "Test should pass when the subscription is cancelled and a BadRequestException is thrown")
  void cancelAlreadyCancelledSubscription() {
    Long id = 2L;
    assertThatThrownBy(
            () -> {
              subscriptionService.cancel(id);
            })
        .isInstanceOf(BadRequestException.class)
        .hasMessage("Subscription with id " + id + " is already cancelled.");
  }
}
