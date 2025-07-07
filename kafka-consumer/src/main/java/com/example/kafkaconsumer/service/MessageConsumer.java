package com.example.kafkaconsumer.service;

import com.example.metrics.MonitoredKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageConsumer {

  private static final String TOPIC = "TestTopic";

  /**
   * Listens for messages using the custom @MonitoredKafkaListener. The method signature remains the
   * same because our AOP Aspect requires these parameters to calculate and expose the lag metric
   * automatically.
   */
  @MonitoredKafkaListener(topics = TOPIC, groupId = "${spring.kafka.consumer.group-id}")
  public void listen(
      String message,
      Consumer<?, ?> consumer,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset) {

    log.info("Received message: '{}' | Partition: {} | Offset: {}", message, partition, offset);

    // Simulate message processing time to help generate lag for testing
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
