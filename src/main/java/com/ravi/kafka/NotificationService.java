package com.ravi.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")  // <-- This is the FROM phone number
    private String twilioPhoneNumber;

    @Value("${app.sms.indiaNumbers}")  // <-- This is the to phone number
    private String indiaNumbersCsv;


    private List<String> indiaNumbers;

    @PostConstruct
    public void init() {
        // Initialize Twilio client once
        Twilio.init(accountSid, authToken);
        // Prepare list of recipient numbers
        indiaNumbers = Arrays.asList(indiaNumbersCsv.split(","));
    }

    public void sendNotificationToAll(String message) {

        String notificationMessage=buildNotificationMessage(message);

        indiaNumbers.forEach(to -> {
                Message.creator(
                        new PhoneNumber(to.trim()),
                        new PhoneNumber(twilioPhoneNumber),
                        notificationMessage
                ).create();
                System.out.println("Sent SMS to " + to);
        });
    }

    public static String buildNotificationMessage(String rawJson) {
        String prettyJson;
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            Object jsonObj = mapper.readValue(rawJson, Object.class);
            prettyJson = writer.writeValueAsString(jsonObj);
        } catch (Exception e) {
            prettyJson = rawJson; // fallback to raw JSON if parsing fails
        }

        return String.format("""
                            📦 *Order Notification*
                            ------------------------
                            ✅ Your order has been placed successfully!
                    
                            🧾 *Order Details:*
                            %s
                            """, prettyJson);
    }
}