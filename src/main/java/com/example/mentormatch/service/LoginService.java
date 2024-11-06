package com.example.mentormatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mentormatch.entity.User;
import com.example.mentormatch.repository.UserRepository;

@Service
public class LoginService {

    @Autowired
    UserRepository repo;

    public boolean SaveOrUpdate(User x){
        User updatednew = repo.save(x);
        if(repo.findById(updatednew.getId())!=null){
                return true;
        }
        return false;
    }

    public User find(String username){
        User user=repo.findByUsername(username);
        return user;
    }
    
}
