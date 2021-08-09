package com.slb.atc.mail.service;

import com.google.gson.Gson;
import com.slb.atc.subscription.dto.SubscriptionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

  Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

  @Override
  public void sendSubscriptionCreatedNotification(SubscriptionDto subscriptionDto) {
    // TODO: Implement email building using FreeMarker and email sending using JavaMail Api
    // https://krishankantsinghal.medium.com/how-to-send-template-based-email-using-spring-boot-and-freemarker-b1fe8dd978f5
    // For the time being, as a mock, we just log the action
    logger.info(
        "sendSubscriptionCreatedNotification() for subscription "
            + new Gson().toJson(subscriptionDto));
  }

  @Override
  public void sendSubscriptionCancelledNotification(SubscriptionDto subscriptionDto) {
    // TODO: Implement email building using FreeMarker and email sending using JavaMail Api
    // https://krishankantsinghal.medium.com/how-to-send-template-based-email-using-spring-boot-and-freemarker-b1fe8dd978f5
    // For the time being, as a mock, we just log the action
    logger.info(
        "sendSubscriptionCancelledNotification() for subscription "
            + new Gson().toJson(subscriptionDto));
  }
}
