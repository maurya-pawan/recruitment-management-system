package com.example.recruitment.apicalls;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiCalls {

    private final RestTemplate restTemplate;

    @Value("${apilayer.url}")
    private String apiUrl;

    @Value("${apilayer.key}")
    private String apiKey;

    public Map<String, Object> callFileToJson(byte[] fileBytes) {
    	Map<String, Object> params = new HashMap<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("apikey", apiKey);  

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileBytes, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            log.info("External API response status: {}", response.getStatusCode());
            if(response.getStatusCode() != HttpStatus.OK) {
				log.error("Failed to call external API, status code: {}", response.getStatusCode());
				 params.put("error", response.getBody()).toString();
				 return params;
			}
             params.put("success", response.getBody());
             return params;
        } catch (Exception e) {
            log.error("Error calling external API: {}", e.getMessage(), e);
            params.put("error", e.getMessage());
            return params;
        }
    }
}
