package com.example.recruitment.service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.example.recruitment.entity.Profile;
import com.example.recruitment.entity.User;
import com.example.recruitment.repository.ProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResumeService {

	private final ProfileRepository profileRepository;

	public Profile processAndSaveResume(User user, String data) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(data);
			Profile profile = profileRepository.findByUser(user).orElseGet(() -> {
				Profile p = new Profile();
				p.setUser(user);
				return p;
			});
			profile.setResumeFileAddress(data);

			if (root.has("skills") && root.get("skills").isArray()) {
				String skills = StreamSupport.stream(root.get("skills").spliterator(), false).map(JsonNode::asText)
						.collect(Collectors.joining(", "));
				profile.setSkills(skills);
			}

			if (root.has("education") && root.get("education").isArray()) {
				String education = StreamSupport.stream(root.get("education").spliterator(), false)
						.map(n -> n.has("name") ? n.get("name").asText() : n.asText())
						.collect(Collectors.joining(" | "));
				profile.setEducation(education);
			}

			if (root.has("experience") && root.get("experience").isArray()) {
				String experience = StreamSupport.stream(root.get("experience").spliterator(), false)
						.map(n -> n.has("name") ? n.get("name").asText() : n.asText())
						.collect(Collectors.joining(" | "));
				profile.setExperience(experience);
			}
			if (root.has("name"))
				profile.setName(root.get("name").asText());
			if (root.has("email"))
				profile.setEmail(root.get("email").asText());
			if (root.has("phone"))
				profile.setPhone(root.get("phone").asText());
			return profileRepository.save(profile);
		} catch (Exception e) {
			Profile profile = profileRepository.findByUser(user).orElseGet(() -> {
				Profile p = new Profile();
				p.setUser(user);
				return p;
			});
			profile.setExperience("Third Party Resume Parsing API Unavailable");
			return profileRepository.save(profile);
		}
	}
}
