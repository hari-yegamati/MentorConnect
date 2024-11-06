package com.example.mentormatch.entity;

public class ProjectForm {
    private Long id;
    private String projectName;
    private String department;
    private String language;

    // Constructors, getters, and setters

    public ProjectForm() {
    }

    public ProjectForm(Long id, String projectName, String department, String language) {
        this.id = id;
        this.projectName = projectName;
        this.department = department;
        this.language = language;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "ProjectForm{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", department='" + department + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}

