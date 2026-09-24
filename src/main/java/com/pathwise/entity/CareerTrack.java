package com.pathwise.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "career_tracks")
public class CareerTrack {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roleId;
    
    @Column(length = 500)
    private String title;

    private String category;

    @Column(length = 500)
    private String eligibleDepartments;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String skillsJson;

    @Column(columnDefinition = "TEXT")
    private String hiringCompaniesJson;

    private Double minSalary;
    private Double maxSalary;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getEligibleDepartments() { return eligibleDepartments; }
    public void setEligibleDepartments(String eligibleDepartments) { this.eligibleDepartments = eligibleDepartments; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSkillsJson() { return skillsJson; }
    public void setSkillsJson(String skillsJson) { this.skillsJson = skillsJson; }

    public String getHiringCompaniesJson() { return hiringCompaniesJson; }
    public void setHiringCompaniesJson(String hiringCompaniesJson) { this.hiringCompaniesJson = hiringCompaniesJson; }

    public Double getMinSalary() { return minSalary; }
    public void setMinSalary(Double minSalary) { this.minSalary = minSalary; }

    public Double getMaxSalary() { return maxSalary; }
    public void setMaxSalary(Double maxSalary) { this.maxSalary = maxSalary; }
}