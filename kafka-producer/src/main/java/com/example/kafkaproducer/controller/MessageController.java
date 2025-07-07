package com.example.kafkaproducer.controller;

import com.example.kafkaproducer.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

  private final KafkaProducerService kafkaProducerService;

  @PostMapping
  public ResponseEntity<String> sendMessage(@RequestBody String message) {
    kafkaProducerService.sendMessage(message);
    return ResponseEntity.ok("Message sent successfully");
  }
}
