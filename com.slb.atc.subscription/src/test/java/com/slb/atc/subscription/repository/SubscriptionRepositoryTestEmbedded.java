package com.slb.atc.subscription.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.slb.atc.subscription.entity.Subscription;
import com.slb.atc.subscription.enums.Gender;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

// TODO: Refactor for performance.
// Rationale: Cleaning the context before each test is too costly, specially with many test cases
// Possible solution: Set up test conditions within each method and make them not dependent on
//   any other data
@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SubscriptionRepositoryTestEmbedded {

  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

  Logger logger = LoggerFactory.getLogger(SubscriptionRepositoryTestEmbedded.class);

  @Autowired SubscriptionRepository subscriptionRepository;

  @BeforeEach
  public void init() throws ParseException {
    subscriptionRepository.save(
        new Subscription(
            0L,
            "paul@paul.com",
            "paul",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            1L,
            false));
    subscriptionRepository.save(
        new Subscription(
            0L,
            "paul@paul.com",
            "paul",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            2L,
            true));
    subscriptionRepository.save(
        new Subscription(
            0L,
            "lucy@lucy.com",
            "luci",
            Gender.GENDER_FEMALE,
            dateFormatter.parse("01-01-2003"),
            true,
            1L,
            false));
  }

  @Test
  @DisplayName("Test should pass when a non empty optional is returned")
  public void findByIdWithExistingId() throws ParseException {
    Long id = 1L;
    Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(id);
    assertThat(subscriptionOptional.isEmpty()).isFalse();
    assertThat(subscriptionOptional.get())
        .usingRecursiveComparison()
        .isEqualTo(
            new Subscription(
                1L,
                "paul@paul.com",
                "paul",
                Gender.GENDER_MALE,
                dateFormatter.parse("01-01-2001"),
                true,
                1L,
                false));
  }

  @Test
  @DisplayName("Test should pass when an empty optional is returned")
  public void findByIdWithNonExistingId() {
    Long id = 1000L;
    Optional<Subscription> subscriptionOptional = subscriptionRepository.findById(id);
    assertThat(subscriptionOptional.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Test should pass when a non empty list is returned")
  public void listByEmailWithRecords() throws ParseException {
    String email = "paul@paul.com";
    List<Subscription> subscriptions = subscriptionRepository.findByEmail(email);
    assertThat(subscriptions.get(0))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            new Subscription(
                0L,
                "paul@paul.com",
                "paul",
                Gender.GENDER_MALE,
                dateFormatter.parse("01-01-2001"),
                true,
                1L,
                false));
    assertThat(subscriptions.get(1))
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            new Subscription(
                0L,
                "paul@paul.com",
                "paul",
                Gender.GENDER_MALE,
                dateFormatter.parse("01-01-2001"),
                true,
                2L,
                true));
  }

  @Test
  @DisplayName("Test should pass when an empty list is returned")
  public void listByEmailWithNoRecords() {
    String email = "jake@jake.com";
    List<Subscription> subscriptions = subscriptionRepository.findByEmail(email);
    assertThat(subscriptions.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Test should pass when the subscription is saved and an id > 0 is returned")
  public void saveValidSubscription() throws ParseException {
    Subscription subscription =
        new Subscription(
            0L,
            "jane@jane.com",
            "jane",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            1L,
            false);
    Subscription savedSubscription = subscriptionRepository.saveAndFlush(subscription);
    assertThat(savedSubscription)
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(subscription);
    assertThat(savedSubscription.getId()).isGreaterThan(0L);
  }

  @Test
  @DisplayName("Test should pass when a DataIntegrityViolationException is thrown")
  public void saveDuplicateSubscription() throws ParseException {
    Subscription subscription =
        new Subscription(
            0L,
            "paul@paul.com",
            "paul",
            Gender.GENDER_MALE,
            dateFormatter.parse("01-01-2001"),
            true,
            1L,
            false);

    assertThatThrownBy(
            () -> {
              subscriptionRepository.save(subscription);
            })
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}
