package com.example.mentormatch.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.mentormatch.entity.Prof;

@Repository
public interface ProfRepository extends JpaRepository<Prof,Long>{
    // Prof findByUsername(String username);
}