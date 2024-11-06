// package com.example.mentormatch.service;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.example.mentormatch.entity.Professor;
// import com.example.mentormatch.repository.ProfessorRepository;

// @Service
// public class SearchService {

//     @Autowired
//     ProfessorRepository repos;

//     public List<Professor> getAllProfessors(){
//         List<Professor> newlist = repos.findAll();
//         System.out.println(newlist);
//         return newlist;
//     }
//     public boolean SaveOrUpdate(Professor x){
//         Professor updatednew = repos.save(x);
//         if(repos.findById(updatednew.getId())!=null){
//                 return true;
//         }
//         return false;
//     }
    
// }
