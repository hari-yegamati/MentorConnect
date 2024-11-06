// package com.example.mentormatch.entity;

// import jakarta.persistence.*;

// @Entity
// @Table(name = "professors")
// public class Professor {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(nullable = false)
//     private String username;

//     @Column(nullable = false)
//     private String department;

//     @Column(nullable=false)
//     private String lang;

//     // Getters and setters
//     public Long getId() {
//         return id;
//     }

//     public void setId(Long id) {
//         this.id = id;
//     }

//     public String getUsername() {
//         return username;
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

//     public String getDepartment() {
//         return department;
//     }

//     public void setDepartment(String department) {
//         this.department = department;
//     }

//     public void setLang(String langs){
//         this.lang=langs;
//     }
//     public String getLang(){
//         return lang;
//     }
// }
