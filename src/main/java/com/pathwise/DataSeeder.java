package com.pathwise;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathwise.entity.CareerTrack;
import com.pathwise.repository.CareerTrackRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CareerTrackRepository careerTrackRepository;
    private final ObjectMapper objectMapper;

    public DataSeeder(CareerTrackRepository careerTrackRepository) {
        this.careerTrackRepository = careerTrackRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run(String... args) throws Exception {
        if (careerTrackRepository.count() == 0) {
            System.out.println("Seeding database with job roles from roles.json...");
            ClassPathResource resource = new ClassPathResource("roles.json");
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    JsonNode root = objectMapper.readTree(is);
                    JsonNode rolesNode = root.get("roles");
                    if (rolesNode != null && rolesNode.isArray()) {
                        for (JsonNode roleNode : rolesNode) {
                            CareerTrack track = new CareerTrack();
                            track.setRoleId(roleNode.path("id").asText());
                            track.setTitle(roleNode.path("role").asText());
                            track.setCategory(roleNode.path("category").asText());
                            track.setDescription(roleNode.path("description").asText());
                            
                            JsonNode depts = roleNode.get("eligible_departments");
                            if (depts != null && depts.isArray()) {
                                StringBuilder sb = new StringBuilder();
                                for (JsonNode d : depts) {
                                    if (sb.length() > 0) sb.append(", ");
                                    sb.append(d.asText());
                                }
                                track.setEligibleDepartments(sb.toString());
                            }

                            if (roleNode.has("skills")) {
                                track.setSkillsJson(roleNode.get("skills").toString());
                            }

                            if (roleNode.has("hiring_companies")) {
                                track.setHiringCompaniesJson(roleNode.get("hiring_companies").toString());
                            }

                            JsonNode sal = roleNode.path("salary_lpa_overall");
                            if (sal.has("min")) track.setMinSalary(sal.get("min").asDouble());
                            if (sal.has("max")) track.setMaxSalary(sal.get("max").asDouble());

                            careerTrackRepository.save(track);
                        }
                        System.out.println("Successfully seeded " + careerTrackRepository.count() + " job roles into PostgreSQL!");
                    }
                }
            }
        }
    }
}
