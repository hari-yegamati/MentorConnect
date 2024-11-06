package com.example.mentormatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailsender;

    public void sendEmail(String to,String sub,String body){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setFrom("brooooo");
        message.setTo(to);
        message.setSubject(sub);
        message.setText(body);
        mailsender.send(message);
        System.out.println("mail sent....");
    }
}
