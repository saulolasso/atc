package com.slb.atc.mail.service;

import com.slb.atc.subscription.dto.SubscriptionDto;

public interface MailService {

  public void sendSubscriptionCreatedNotification(SubscriptionDto suscriptionDto);

  public void sendSubscriptionCancelledNotification(SubscriptionDto suscriptionDto);
}
