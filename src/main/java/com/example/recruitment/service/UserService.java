package com.example.recruitment.service;

import com.example.recruitment.entity.Profile;
import com.example.recruitment.entity.User;
import com.example.recruitment.repository.ProfileRepository;
import com.example.recruitment.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user, String rawPassword) {
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        User saved = userRepository.save(user);
        if (saved.getUserType() == User.Role.APPLICANT) {
            Profile p = new Profile();
            p.setUser(saved);
            profileRepository.save(p);
            saved.setProfile(p);
            userRepository.save(saved);
        }
        return saved;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
