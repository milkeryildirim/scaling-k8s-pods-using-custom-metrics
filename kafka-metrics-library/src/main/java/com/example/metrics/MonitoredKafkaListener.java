package com.example.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@KafkaListener
public @interface MonitoredKafkaListener {

  @AliasFor(annotation = KafkaListener.class, attribute = "id")
  String id() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "containerFactory")
  String containerFactory() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "topics")
  String[] topics() default {};

  @AliasFor(annotation = KafkaListener.class, attribute = "topicPattern")
  String topicPattern() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "topicPartitions")
  TopicPartition[] topicPartitions() default {};

  @AliasFor(annotation = KafkaListener.class, attribute = "containerGroup")
  String containerGroup() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "errorHandler")
  String errorHandler() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "groupId")
  String groupId() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "idIsGroup")
  boolean idIsGroup() default true;

  @AliasFor(annotation = KafkaListener.class, attribute = "clientIdPrefix")
  String clientIdPrefix() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "beanRef")
  String beanRef() default "__listener";

  @AliasFor(annotation = KafkaListener.class, attribute = "concurrency")
  String concurrency() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "autoStartup")
  String autoStartup() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "properties")
  String[] properties() default {};

  @AliasFor(annotation = KafkaListener.class, attribute = "splitIterables")
  boolean splitIterables() default true;

  @AliasFor(annotation = KafkaListener.class, attribute = "contentTypeConverter")
  String contentTypeConverter() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "batch")
  String batch() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "filter")
  String filter() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "info")
  String info() default "";

  @AliasFor(annotation = KafkaListener.class, attribute = "containerPostProcessor")
  String containerPostProcessor() default "";
}
