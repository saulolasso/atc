package com.slb.atc.subscription.dto;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorMessageDto {

  private Date timestamp;

  private List<String> errors;
}
