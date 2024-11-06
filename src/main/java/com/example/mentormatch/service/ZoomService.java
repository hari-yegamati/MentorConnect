package com.example.mentormatch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
// import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZoomService {

    @Value("${zoom.client-id}")
    private String clientId;

    @Value("${zoom.client-secret}")
    private String clientSecret;

    @Value("${zoom.account-id}")
    private String accountId;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ZoomService() {
        this.restTemplate = new RestTemplate();  // Initialize RestTemplate here
        this.objectMapper = new ObjectMapper();  // Initialize ObjectMapper here
    }

    public String getAccessToken() {
        try {
            String url = "https://zoom.us/oauth/token?grant_type=account_credentials&account_id=" + accountId;
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                return jsonResponse.get("access_token").asText();
            } else {
                System.out.println("Error getting access token: " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String,String> scheduleMeeting(String accessToken) {
        try {

            ///hostkey:731238

             String password;
            String meetingid;
            // LocalDateTime currentTime = LocalDateTime.now();
            ZonedDateTime indiaTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        System.out.println(indiaTime);
        // Convert IST to UTC
        ZonedDateTime utcTime = indiaTime.withZoneSameInstant(ZoneId.of("UTC"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String formattedStartTime = utcTime.format(formatter);
            String url = "https://api.zoom.us/v2/users/me/meetings";
            System.out.println(formattedStartTime);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Meeting details
            Map<String, Object> meetingDetails = new HashMap<>();
            meetingDetails.put("topic", "Mentor Connect");
            meetingDetails.put("type", 2);
            meetingDetails.put("start_time", formattedStartTime);
            meetingDetails.put("duration", 5);
            meetingDetails.put("timezone", "Asia/Kolkata");

            Map<String, Object> settings = new HashMap<>();
            settings.put("host_video", true);
            settings.put("participant_video", true);
            settings.put("join_before_host", true);
            settings.put("mute_upon_entry", true);
            meetingDetails.put("settings", settings);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(meetingDetails, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            String responsebody=response.getBody();
            Map<String, String> result = new HashMap<>();

            if (response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                meetingid=extractMeetingIdFromResponse(responsebody);
                password=jsonResponse.get("password").asText();
                System.out.println(meetingid+" "+password);
                result.put("meetingid",meetingid);
                result.put("password", password);
                result.put("join_url",jsonResponse.get("join_url").asText());
                return result;
            } else {
                result.put("error",response.getBody());
                System.out.println("Error creating meeting: " + response.getBody());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractMeetingIdFromResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse JSON response to JsonNode
            JsonNode rootNode = objectMapper.readTree(response);
            // Extract the "id" field and return it as a String
            return rootNode.get("id").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return null;  // Return null or handle the error as needed
        }
    }

    public String addAlternativeHost(String meetingId, String newHostEmail, String accessToken) {
        String url = "https://api.zoom.us/v2" + "/meetings/" + meetingId;

        // Set the headers, including the Authorization token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // Create request body JSON with alternative host email
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("alternative_hosts", newHostEmail); // Add new alternative host email
        
        // Send the PATCH request to Zoom API to update the meeting
        // RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);
            return responseEntity.getBody(); // Return response from Zoom API
        } catch (Exception e) {
            e.printStackTrace();
            return "Error adding alternative host: " + e.getMessage();
        }
    }
}