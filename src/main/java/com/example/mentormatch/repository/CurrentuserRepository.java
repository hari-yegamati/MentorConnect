package com.example.mentormatch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mentormatch.entity.Currentuser;


@Repository
public interface CurrentuserRepository extends JpaRepository<Currentuser,Long>{

}
