package com.ravi.kafka;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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

        indiaNumbers.forEach(to -> {
            Message.creator(
                    new PhoneNumber(to.trim()),             // TO number
                    new PhoneNumber(twilioPhoneNumber),     // FROM number
                    message
            ).create();

            System.out.println("Sent SMS to " + to);
         });

    }
}
