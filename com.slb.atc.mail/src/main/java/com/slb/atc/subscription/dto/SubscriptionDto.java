package com.slb.atc.subscription.dto;

import com.slb.atc.subscription.enums.Gender;
import java.util.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionDto {

  private Long id;

  @NotBlank @Email private String email;

  private String firstname;

  private Gender gender;

  @NotNull private Date dateOfBirth;

  @NotNull private Long newsletterId;

  @NotNull private boolean cancelled;

  public SubscriptionDto() {}
}
