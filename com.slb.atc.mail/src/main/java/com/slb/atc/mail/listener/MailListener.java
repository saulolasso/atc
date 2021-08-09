package com.slb.atc.mail.listener;

import com.slb.atc.mail.service.MailService;
import com.slb.atc.subscription.dto.SubscriptionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MailListener {

  @Autowired MailService subscriptionService;

  @KafkaListener(topics = "sendSubscriptionCreatedNotification", groupId = "group-id")
  public void consumeSubscriptionCreatedEvent(SubscriptionDto subscriptionDto) {
    subscriptionService.sendSubscriptionCreatedNotification(subscriptionDto);
  }

  @KafkaListener(topics = "sendSubscriptionCancelledNotification", groupId = "group-id")
  public void consumeSubscriptionCancelledEvent(SubscriptionDto subscriptionDto) {
    subscriptionService.sendSubscriptionCreatedNotification(subscriptionDto);
  }
}
