package com.pathwise;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathwise.entity.CareerTrack;
import com.pathwise.entity.Course;
import com.pathwise.entity.Lesson;
import com.pathwise.repository.CareerTrackRepository;
import com.pathwise.repository.CourseRepository;
import com.pathwise.repository.LessonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CareerTrackRepository careerTrackRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ObjectMapper objectMapper;

    public DataSeeder(CareerTrackRepository careerTrackRepository,
                      CourseRepository courseRepository,
                      LessonRepository lessonRepository) {
        this.careerTrackRepository = careerTrackRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Career Tracks from roles.json
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
                        System.out.println("Successfully seeded " + careerTrackRepository.count() + " job roles!");
                    }
                }
            }
        }

        // 2. Seed Courses and Lessons from syllabus_video_guide.json
        if (courseRepository.count() == 0) {
            System.out.println("Seeding database with syllabus video guides from syllabus_video_guide.json...");
            ClassPathResource resource = new ClassPathResource("syllabus_video_guide.json");
            if (resource.exists()) {
                try (InputStream is = resource.getInputStream()) {
                    JsonNode root = objectMapper.readTree(is);
                    JsonNode coursesNode = root.get("courses");
                    if (coursesNode != null && coursesNode.isArray()) {
                        int seededCount = 0;
                        for (JsonNode courseNode : coursesNode) {
                            JsonNode videoNode = courseNode.get("video");
                            if (videoNode != null && !videoNode.isNull() && videoNode.has("embed_url")) {
                                Course course = new Course();
                                String code = courseNode.path("code").asText();
                                String title = courseNode.path("title").asText();
                                String category = courseNode.path("category").asText();
                                
                                course.setCode(code);
                                course.setTitle(title);
                                course.setCategory(category);
                                course.setDescription("Curated syllabus video guide course for " + title + " (" + code + ")");
                                course.setActive(true);
                                
                                JsonNode deptsNode = courseNode.get("departments");
                                if (deptsNode != null && deptsNode.isArray() && deptsNode.size() > 0) {
                                    course.setDepartment(deptsNode.get(0).asText());
                                }

                                course = courseRepository.save(course);

                                // Create corresponding Lesson with YouTube Video embed details
                                Lesson lesson = new Lesson();
                                String videoTitle = videoNode.path("title").asText(title);
                                String channel = videoNode.path("channel").asText("YouTube");
                                int durationMin = videoNode.path("duration_min").asInt(30);
                                String watchUrl = videoNode.path("watch_url").asText();
                                String embedUrl = videoNode.path("embed_url").asText();
                                String videoId = videoNode.path("video_id").asText();

                                lesson.setTitle(videoTitle);
                                lesson.setDescription("Official tutorial video by " + channel + " covering " + title);
                                lesson.setDuration(durationMin + " Mins");
                                lesson.setVideoUrl(watchUrl);
                                lesson.setEmbedUrl(embedUrl);
                                lesson.setVideoId(videoId);
                                lesson.setChannel(channel);
                                lesson.setDurationMin(durationMin);
                                lesson.setCourse(course);

                                lessonRepository.save(lesson);
                                seededCount++;
                            }
                        }
                        System.out.println("Successfully seeded " + seededCount + " syllabus video courses and lessons!");
                    }
                }
            }
        }
    }
}
