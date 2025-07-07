package com.example.kafkaconsumer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageConsumer {

  private static final String TOPIC = "TestTopic";

  /**
   * Listens for messages on the "TestTopic".
   * For each message, it calculates the current consumer lag for its partition and logs this information
   * along with the message details. A small, artificial delay is added to simulate processing time.
   *
   * @param message    The content of the incoming message from Kafka.
   * @param consumer   The underlying Kafka Consumer instance, injected by Spring to fetch partition metadata.
   * @param topic      The topic from which the message was received, injected from Kafka message headers.
   * @param partition  The partition number from which the message was received, injected from Kafka message headers.
   * @param offset     The offset of the received message within its partition, injected from Kafka message headers.
   */
  @KafkaListener(topics = TOPIC, groupId = "${spring.kafka.consumer.group-id}")
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
