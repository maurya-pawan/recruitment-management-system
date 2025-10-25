package com.example.recruitment.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.recruitment.entity.Application;
import com.example.recruitment.entity.Job;
import com.example.recruitment.entity.User;
import com.example.recruitment.repository.ApplicationRepository;
import com.example.recruitment.repository.JobRepository;
import com.example.recruitment.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/admin")
public class AdminController {

	private final JobRepository jobRepository;
	private final ApplicationRepository applicationRepository;
	private final UserRepository userRepository;

	public AdminController(JobRepository jobRepository, ApplicationRepository applicationRepository,
			UserRepository userRepository) {
		this.jobRepository = jobRepository;
		this.applicationRepository = applicationRepository;
		this.userRepository = userRepository;
	}

	private boolean isAdmin(HttpServletRequest request) {
		User u = (User) request.getAttribute("authenticatedUser");
		return u != null && u.getUserType() == User.Role.ADMIN;
	}

	@PostMapping("/job")
	public ResponseEntity<?> createJob(HttpServletRequest request, @RequestBody Map<String, String> body) {
		if (!isAdmin(request))
			return ResponseEntity.status(403).body("Forbidden");
		String title = body.get("title");
		if (title == null)
			return ResponseEntity.badRequest().body(Map.of("message", "Title required"));
		String description = body.get("description");
		String companyName = body.get("companyName");

		User admin = (User) request.getAttribute("authenticatedUser");
		Job job = new Job();
		job.setTitle(title);
		job.setDescription(description);
		job.setCompanyName(companyName);
		job.setPostedOn(Instant.now());
		job.setPostedBy(admin);
		jobRepository.save(job);
		return ResponseEntity.status(201).body(job);
	}

	@GetMapping("/job/{job_id}")
	public ResponseEntity<?> getJob(HttpServletRequest request, @PathVariable("job_id") Long jobId) {
		if (!isAdmin(request))
			return ResponseEntity.status(403).body("Forbidden");
		Optional<Job> jobOpt = jobRepository.findById(jobId);
		if (jobOpt.isEmpty())
			return ResponseEntity.status(404).body("Job not found");
		Job job = jobOpt.get();
		List<Application> apps = applicationRepository.findByJob(job);
		List<Map<String, Object>> applicants = new ArrayList<>();
		for (Application a : apps) {
			User applicant = a.getApplicant();
			Map<String, Object> map = new HashMap<>();
			map.put("id", applicant.getId());
			map.put("name", applicant.getName());
			map.put("email", applicant.getEmail());
			map.put("profile", applicant.getProfile());
			applicants.add(map);
		}
		return ResponseEntity.ok(Map.of("job", job, "applicants", applicants));
	}

	@GetMapping("/applicants")
	public ResponseEntity<?> getAllApplicants(HttpServletRequest request) {
		User u = (User) request.getAttribute("authenticatedUser");
		System.out.println("Authenticated User in AdminController: " + u);
		if (!isAdmin(request))
			return ResponseEntity.status(403).body("You are not authorized to access this resource.");
		List<User> users = userRepository.findAll();
		return ResponseEntity.ok(users);
	}

	@GetMapping("/applicant/{applicant_id}")
	public ResponseEntity<?> getApplicant(HttpServletRequest request, @PathVariable("applicant_id") Long applicantId) {
		if (!isAdmin(request)) {
			return ResponseEntity.status(403).body("You are not authorized to access this resource.");
		}
		return userRepository.findById(applicantId).<ResponseEntity<?>>map(user -> ResponseEntity.ok(user))
				.orElse(ResponseEntity.status(404).body("Applicant not found"));
	}

}
