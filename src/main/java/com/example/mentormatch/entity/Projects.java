package com.example.mentormatch.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "projects")
public class Projects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String studentName;
    private String department;
    private String professorName;
    private String language;
    private String studentEmail;
    private String professorEmail;

    // Constructors
    public Projects() {
        // Default constructor
    }

    public Projects(String projectName, String studentName, String professorName,
                   String language, String studentEmail, String professorEmail) {
        this.projectName = projectName;
        this.studentName = studentName;
        this.professorName = professorName;
        this.language = language;
        this.studentEmail = studentEmail;
        this.professorEmail = professorEmail;
    }

    // Getters and Setters

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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getProfessorEmail() {
        return professorEmail;
    }

    public void setProfessorEmail(String professorEmail) {
        this.professorEmail = professorEmail;
    }
}
