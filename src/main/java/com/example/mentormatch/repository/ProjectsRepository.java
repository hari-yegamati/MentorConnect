package com.example.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mentormatch.entity.Projects;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects,Long>{
  
} 