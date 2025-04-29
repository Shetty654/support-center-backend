package com.example.support_center_backend.services.impl;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChartStreamPublisher {
  private final SimpMessagingTemplate template;
  public ChartStreamPublisher(SimpMessagingTemplate template) { this.template = template; }

  public void publish(String groupName, List<Map<String,Object>> rows) {
    template.convertAndSend("/topic/chart/" + groupName, rows);
  }
}