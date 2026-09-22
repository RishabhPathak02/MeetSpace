package com.rishabh.meeting.controller;

import com.rishabh.meeting.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private EmailService emailService ;

    public EmailController(EmailService emailService){
        this.emailService = emailService ;
    }

    @PostMapping("/api/email")
    public void pushEmail(){

    }
}
