package com.slb.atc.subscription.entity;

import static javax.persistence.TemporalType.TIMESTAMP;

import com.slb.atc.subscription.enums.Gender;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "newsletterId"}))
public class Subscription {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private String email;

  private String firstname;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  @NotNull
  @Temporal(TIMESTAMP)
  private Date dateOfBirth;

  @NotNull @AssertTrue private Boolean flagForConsent;

  @NotNull private Long newsletterId;

  @NotNull private Boolean cancelled;
}
