package com.slb.atc.subscription.dto;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class ErrorMessageDto {

  private Date timestamp;

  private List<String> errors;

  public ErrorMessageDto() {}
}
