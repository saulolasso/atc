package com.slb.atc.subscription.service;

import com.slb.atc.subscription.dto.SubscriptionDto;
import com.slb.atc.subscription.entity.Subscription;
import com.slb.atc.subscription.error.BadRequestException;
import com.slb.atc.subscription.error.NotFoundException;
import com.slb.atc.subscription.repository.SubscriptionRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

  @Autowired SubscriptionRepository subscriptionRepository;

  @Autowired KafkaTemplate<String, Object> kafkaTemplate;

  public ModelMapper getModelMapper() {
    return new ModelMapper();
  }

  @Override
  public List<SubscriptionDto> listByEmail(String email) {
    List<Subscription> subscriptions = subscriptionRepository.findByEmail(email);
    List<SubscriptionDto> subscriptionDtos =
        subscriptions.stream()
            .map(subscription -> getModelMapper().map(subscription, SubscriptionDto.class))
            .collect(Collectors.toList());
    if (subscriptionDtos.isEmpty()) {
      throw new NotFoundException("There aren't any subscriptions with email " + email + ".");
    }
    return subscriptionDtos;
  }

  @Override
  public SubscriptionDto getById(long id) {
    Subscription subscription =
        subscriptionRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Subscription with id " + id + " doesn't exist."));
    return getModelMapper().map(subscription, SubscriptionDto.class);
  }

  @Override
  @Transactional(rollbackFor = {KafkaException.class})
  public SubscriptionDto create(SubscriptionDto subscriptionDto) {
    try {
      Subscription subscription = getModelMapper().map(subscriptionDto, Subscription.class);
      Subscription subscriptionWithId = subscriptionRepository.saveAndFlush(subscription);
      SubscriptionDto subscriptionDtoWithId =
          getModelMapper().map(subscriptionWithId, SubscriptionDto.class);
      kafkaTemplate.send("sendSubscriptionCreatedNotification", subscriptionDtoWithId);
      return subscriptionDtoWithId;
    } catch (DataIntegrityViolationException ex) {
      throw new BadRequestException(
          "Email "
              + subscriptionDto.getEmail()
              + "is already subscribed to newsletter with id "
              + subscriptionDto.getNewsletterId()
              + ".");
    }
  }

  @Override
  @Transactional(rollbackFor = {KafkaException.class})
  public void cancel(long id) {
    Subscription subscription =
        subscriptionRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("Subscription with id " + id + " doesn't exist."));
    if (subscription.getCancelled()) {
      throw new BadRequestException("Subscription with id " + id + " is already cancelled.");
    } else {
      subscription.setCancelled(true);
      subscriptionRepository.save(subscription);
      kafkaTemplate.send(
          "sendSubscriptionCancelledNotification",
          getModelMapper().map(subscription, SubscriptionDto.class));
    }
  }
}
