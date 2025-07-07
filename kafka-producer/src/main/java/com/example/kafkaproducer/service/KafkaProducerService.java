package com.example.kafkaproducer.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

  // Define the topic name as a constant
  private static final String TOPIC = "TestTopic";

  private final KafkaTemplate<String, String> kafkaTemplate;

  public void sendMessage(String message) {
    // Generate a random key to ensure messages are distributed across partitions
    String key = UUID.randomUUID().toString();
    kafkaTemplate.send(TOPIC, key, message);
    log.info("Message sent to Kafka topic: {} with key: {}", TOPIC, key);
  }
}
