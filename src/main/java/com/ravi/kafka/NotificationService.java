package com.ravi.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {


    @Value("${textbelt_api_key}")
    private String textbelt_api_key;

    @Value("${receivers_group_numbers}")
    private String indiaNumbersCsv;


    public void sendNotificationToAll1(String message) throws IOException, InterruptedException {

        String notificationMessage=buildNotificationMessage(message);

        HttpClient client = HttpClient.newHttpClient();

        String[] phoneNumbers=indiaNumbersCsv.split(",");


        for (String phone : phoneNumbers) {
            JSONObject postData = new JSONObject();
            postData.put("phone", phone.trim());
            postData.put("message", notificationMessage);
            postData.put("key",textbelt_api_key); // Replace with your API key if using paid

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://textbelt.com/text"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(postData.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("SMS sent to  " + phone + ": " + response.body());
        }
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