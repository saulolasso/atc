package com.slb.atc.subscription.service;

import com.slb.atc.subscription.dto.SubscriptionDto;
import java.util.List;

public interface SubscriptionService {

  public List<SubscriptionDto> listByEmail(String email);

  public SubscriptionDto getById(long id);

  public SubscriptionDto create(SubscriptionDto suscriptionDto);

  public void cancel(long id);
}
