package com.example.recruitment.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.recruitment.apicalls.ApiCalls;
import com.example.recruitment.entity.User;
import com.example.recruitment.service.ResumeService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

	private final ResumeService resumeService;
	
	@Value("${upload.dir}")
	private String uploadDir;
	
	private final ApiCalls apiCalls;

	@PostMapping("/uploadResume")
	public ResponseEntity<?> uploadResume(HttpServletRequest request, @RequestParam("resume") MultipartFile file) {
		User authUser = (User) request.getAttribute("authenticatedUser");
		log.info("Authenticated user: {}", authUser != null ? authUser.getName() : "null");

		if (authUser == null)
			return ResponseEntity.status(401).body("Unauthorized");
		if (authUser.getUserType() != User.Role.APPLICANT)
			return ResponseEntity.status(403).body("Only applicants can upload resumes");

		if (file == null || file.isEmpty())
			return ResponseEntity.badRequest().body("No file uploaded");
		String contentType = file.getContentType();
		if (contentType == null || !(contentType.equals("application/pdf") || contentType.equals("application/msword")
				|| contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
			return ResponseEntity.badRequest().body("Only PDF or DOC/DOCX files are allowed");
		}

		try {
			Files.createDirectories(Paths.get(uploadDir));
			String filename = System.currentTimeMillis() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
			Path target = Paths.get(uploadDir).resolve(filename);
			file.transferTo(target);
			
			 Map<String, Object> callFileToJson = apiCalls.callFileToJson(file.getBytes());
			 if(callFileToJson.containsKey("error")) {
				 String errorMsg = callFileToJson.get("error").toString();
				 return ResponseEntity.status(500).body(errorMsg);
			 }
			 String data = String.valueOf(callFileToJson.get("success"));
			 resumeService.processAndSaveResume(authUser, data);
			 
			 return ResponseEntity.ok("Resume uploaded and processed successfully");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Failed to process resume: " + e.getMessage());
		}
	}
}
