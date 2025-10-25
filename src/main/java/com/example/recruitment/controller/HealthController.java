package com.example.recruitment.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {
	
	@GetMapping
	public Map<String, Object> healthCheck() {
		return Map.of("response", "Application is up and running");
	}

}
