package com.example.recruitment.controller;

import com.example.recruitment.entity.Application;
import com.example.recruitment.entity.Job;
import com.example.recruitment.entity.User;
import com.example.recruitment.repository.ApplicationRepository;
import com.example.recruitment.repository.JobRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobs")
public class JobsController {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public JobsController(JobRepository jobRepository, ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping
    public ResponseEntity<?> listJobs() {
        List<Job> jobs = jobRepository.findAll();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/apply")
    public ResponseEntity<?> applyToJob(HttpServletRequest request, @RequestParam("job_id") Long jobId) {
        User authUser = (User) request.getAttribute("authenticatedUser");
        if (authUser == null) return ResponseEntity.status(401).body("Unauthorized");
        if (authUser.getUserType() != User.Role.APPLICANT) return ResponseEntity.status(403).body("Only applicants can apply");
        Optional<Job> jobOpt = jobRepository.findById(jobId);
        if (jobOpt.isEmpty()) return ResponseEntity.status(404).body("Job not found");
        Job job = jobOpt.get();

        if (applicationRepository.findByJobAndApplicant(job, authUser).isPresent()) return ResponseEntity.badRequest().body("Already applied");

        Application app = new Application();
        app.setJob(job);
        app.setApplicant(authUser);
        app.setAppliedAt(Instant.now());
        applicationRepository.save(app);

        job.setTotalApplications((job.getTotalApplications() == null ? 0 : job.getTotalApplications()) + 1);
        jobRepository.save(job);
        return ResponseEntity.ok("Applied successfully");
    }
}
