package com.pathwise.entity;
import jakarta.persistence.*;
@Entity
public class Lesson {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String duration;
    private String videoUrl;
    @ManyToOne
    private Course course;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getDuration() { return duration; } public void setDuration(String duration) { this.duration = duration; }
    public String getVideoUrl() { return videoUrl; } public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public Course getCourse() { return course; } public void setCourse(Course course) { this.course = course; }
}