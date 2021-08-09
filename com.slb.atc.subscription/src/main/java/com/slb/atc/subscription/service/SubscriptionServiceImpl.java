package com.slb.atc.subscription.service;

import com.slb.atc.subscription.dto.SubscriptionDto;
import com.slb.atc.subscription.entity.Subscription;
import com.slb.atc.subscription.error.BadRequestException;
import com.slb.atc.subscription.error.NotFoundException;
import com.slb.atc.subscription.repository.SubscriptionRepository;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
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
    return subscriptionDtos;
  }

	// FIXME: It appears not to be catching the EntityNotFoundException
	//  which in return causes a HTTP 500 response instead of the HTTP 404 expected
  @Override
  public SubscriptionDto getById(long id) {
    try {
      Subscription subscription = subscriptionRepository.getById(id);
      return getModelMapper().map(subscription, SubscriptionDto.class);
    } catch (EntityNotFoundException ex) {
      throw new NotFoundException("Subscription with id " + id + " doesn't exist.");
    }
  }

  // FIXME: Http Response not showing error message
  @Override
  @Transactional(rollbackFor = {KafkaException.class})
  public SubscriptionDto create(SubscriptionDto subscriptionDto) {
    try {
      Subscription subscription = getModelMapper().map(subscriptionDto, Subscription.class);
      subscription = subscriptionRepository.saveAndFlush(subscription);
      kafkaTemplate.send("sendSubscriptionCreatedNotification", subscriptionDto);
      return getModelMapper().map(subscription, SubscriptionDto.class);
    } catch (DataIntegrityViolationException ex) {
      throw new BadRequestException(
          "Email "
              + subscriptionDto.getEmail()
              + "is alredy subscribed to newsletter with id "
              + subscriptionDto.getNewsletterId());
    }
  }

  // FIXME: It appears not to be catching the EntityNotFoundException
	//  which in return causes a HTTP 500 response instead of the HTTP 404 expected
	@Override
  @Transactional(rollbackFor = {KafkaException.class})
  public void cancel(long id) {
    try {
      Subscription subscription = subscriptionRepository.getById(id);
      if (subscription.isCancelled()) {
        throw new BadRequestException("Subscription with id " + id + " is already cancelled.");
      } else {
        subscription.setCancelled(true);
        subscriptionRepository.save(subscription);
        kafkaTemplate.send(
            "sendSubscriptionCreatedNotification",
            getModelMapper().map(subscription, SubscriptionDto.class));
      }
    } catch (EntityNotFoundException ex) {
      throw new NotFoundException("Subscription with id " + id + " doesn't exist.");
    }
  }
}
