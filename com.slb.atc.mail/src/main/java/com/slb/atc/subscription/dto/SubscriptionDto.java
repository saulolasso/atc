package com.slb.atc.subscription.dto;

import com.slb.atc.subscription.enums.Gender;
import java.util.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {

  private Long id;

  @NotBlank @Email private String email;

  private String firstname;

  private Gender gender;

  @NotNull private Date dateOfBirth;

  @NotNull private Boolean flagForConsent;

  @NotNull private Long newsletterId;

  @NotNull private Boolean cancelled;
}
