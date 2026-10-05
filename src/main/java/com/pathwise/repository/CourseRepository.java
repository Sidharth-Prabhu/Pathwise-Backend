package com.pathwise.repository;

import com.pathwise.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByActiveTrue();
    Optional<Course> findByCode(String code);
    List<Course> findByTitleContainingIgnoreCase(String query);
}
