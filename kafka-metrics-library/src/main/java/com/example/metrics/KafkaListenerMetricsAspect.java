package com.example.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class KafkaListenerMetricsAspect {

  private static final String METRIC_NAME = "kafka.consumer.lag";
  private static final String METRIC_DESCRIPTION =
      "The current estimated lag of a Kafka consumer group";

  private final MeterRegistry meterRegistry;
  private final Map<TopicPartition, LagInfo> lagGauges = new ConcurrentHashMap<>();

  /**
   * This advice runs "around" any method annotated with @MonitoredKafkaListener. It proceeds with
   * the original method execution and then calculates and records the consumer lag.
   */
  @Around("@annotation(monitoredKafkaListener)")
  public Object monitorLag(
      ProceedingJoinPoint joinPoint, MonitoredKafkaListener monitoredKafkaListener)
      throws Throwable {
    Object result = joinPoint.proceed();

    Object[] args = joinPoint.getArgs();

    Consumer<?, ?> consumer = findArgByType(args, Consumer.class);
    String topic = findArgByHeader(joinPoint, String.class, KafkaHeaders.RECEIVED_TOPIC);
    Integer partition = findArgByHeader(joinPoint, Integer.class, KafkaHeaders.RECEIVED_PARTITION);
    Long offset = findArgByHeader(joinPoint, Long.class, KafkaHeaders.OFFSET);
    if (Objects.isNull(consumer)
        || Objects.isNull(topic)
        || Objects.isNull(partition)
        || Objects.isNull(offset)) {
      log.warn(
          "Could not record lag metric. Listener method signature must include Consumer, @Header(KafkaHeaders.RECEIVED_TOPIC) String, @Header(KafkaHeaders.RECEIVED_PARTITION) int, and @Header(KafkaHeaders.OFFSET) long.");
      return result;
    }

    String groupId = consumer.groupMetadata().groupId();
    TopicPartition topicPartition = new TopicPartition(topic, partition);
    long endOffset = consumer.endOffsets(Collections.singleton(topicPartition)).get(topicPartition);
    long currentLag = Math.max(0, endOffset - offset);
    LagInfo lagInfo =
        lagGauges.computeIfAbsent(
            topicPartition,
            tp -> {
              LagInfo newInfo = new LagInfo();
              Tags tags =
                  Tags.of(
                      "groupId", groupId, "topic", topic, "partition", String.valueOf(partition));
              Gauge.builder(METRIC_NAME, newInfo, LagInfo::getLag)
                  .description(METRIC_DESCRIPTION)
                  .tags(tags)
                  .register(meterRegistry);
              return newInfo;
            });
    lagInfo.setLag(currentLag);
    log.trace("Updated lag metric for {}: {}", topicPartition, currentLag);

    return result;
  }

  /**
   * Finds an argument from the method call based on its type. Useful for arguments without special
   * annotations, like the Consumer object.
   *
   * @param args The runtime arguments of the method.
   * @param type The class type of the argument to find.
   * @return The argument object, or null if not found.
   */
  private <T> T findArgByType(Object[] args, Class<T> type) {
    for (Object arg : args) {
      if (type.isAssignableFrom(arg.getClass())) {
        return type.cast(arg);
      }
    }
    return null;
  }

  /**
   * Finds an argument from the method call by inspecting its parameter annotations. This is a
   * robust way to find a specific header-annotated parameter, even if multiple parameters have the
   * same Java type.
   *
   * @param joinPoint The ProceedingJoinPoint to access method signature.
   * @param type The class type of the argument to find.
   * @param headerName The value of the @Header annotation to look for.
   * @return The argument value, or null if not found.
   */
  private <T> T findArgByHeader(ProceedingJoinPoint joinPoint, Class<T> type, String headerName) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Parameter[] parameters = signature.getMethod().getParameters();
    Object[] args = joinPoint.getArgs();

    for (int i = 0; i < parameters.length; i++) {
      Parameter param = parameters[i];
      Header header = param.getAnnotation(Header.class);
      if (header != null && header.value().equals(headerName)) {
        if (i < args.length && type.isInstance(args[i])) {
          return type.cast(args[i]);
        }
      }
    }

    return null;
  }

  @Getter
  @Setter
  private static class LagInfo {
    private volatile long lag = 0;
  }
}
