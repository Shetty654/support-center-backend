package com.example.support_center_backend.config;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChartDataPublisher {
  private final SimpMessagingTemplate tpl;
  public ChartDataPublisher(SimpMessagingTemplate tpl) { this.tpl = tpl; }

  public void publish(String groupName, List<Map<String,Object>> rows) {
    tpl.convertAndSend("/topic/chart/" + groupName, rows);
  }
}