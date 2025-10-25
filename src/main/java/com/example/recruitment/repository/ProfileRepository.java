package com.example.recruitment.repository;

import com.example.recruitment.entity.Profile;
import com.example.recruitment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
}
