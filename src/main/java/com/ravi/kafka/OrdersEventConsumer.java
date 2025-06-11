package com.ravi.kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
public class OrdersEventConsumer {


    @Autowired
    NotificationService notificationService;


    @KafkaListener(topics = "order-events-topic", groupId = "notification-group")
    public void listen(String orderEvent) {
        System.out.println("Received message: " + orderEvent);

        notificationService.sendNotificationToAll(orderEvent);

        log.info("SMS notifications sent successfully");

    }
}
