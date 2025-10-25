package com.example.recruitment.repository;

import com.example.recruitment.entity.Application;
import com.example.recruitment.entity.Job;
import com.example.recruitment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByJobAndApplicant(Job job, User applicant);
    List<Application> findByJob(Job job);
}
