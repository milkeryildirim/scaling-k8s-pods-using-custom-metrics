package com.example.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
@ConditionalOnClass({KafkaListener.class, MeterRegistry.class})
public class KafkaMetricsAutoConfiguration {

  @Bean
  public KafkaListenerMetricsAspect kafkaListenerMetricsAspect(MeterRegistry meterRegistry) {
    return new KafkaListenerMetricsAspect(meterRegistry);
  }
}
