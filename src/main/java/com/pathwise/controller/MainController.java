package com.pathwise.controller;

import com.pathwise.entity.*;
import com.pathwise.repository.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MainController {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CareerTrackRepository careerTrackRepository;

    public MainController(UserRepository userRepository, 
                          CourseRepository courseRepository, 
                          LessonRepository lessonRepository, 
                          CareerTrackRepository careerTrackRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.careerTrackRepository = careerTrackRepository;
    }

    @GetMapping("/users/me")
    public User getMe(@RequestParam String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @GetMapping("/courses")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @GetMapping("/courses/active")
    public List<Course> getActiveCourses() {
        return courseRepository.findByActiveTrue();
    }

    @GetMapping("/courses/{id}")
    public Course getCourse(@PathVariable Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    @GetMapping("/courses/{id}/lessons")
    public List<Lesson> getCourseLessons(@PathVariable Long id) {
        return lessonRepository.findByCourseId(id);
    }

    @GetMapping("/courses/search")
    public List<Course> searchCourses(@RequestParam String query) {
        return courseRepository.findByTitleContainingIgnoreCase(query);
    }

    @GetMapping("/lessons/{id}")
    public Lesson getLesson(@PathVariable Long id) {
        return lessonRepository.findById(id).orElse(null);
    }

    @PostMapping("/lessons/{id}/progress")
    public String updateProgress(@PathVariable Long id) {
        return "Progress updated";
    }

    @GetMapping("/careers/tracks")
    public List<CareerTrack> getCareers() {
        return careerTrackRepository.findAll();
    }

    @GetMapping("/admin/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}