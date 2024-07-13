package com.service;

public class EmailService {

    public void sendEmail(String to, String subject, String body) {
        System.out.println("The event mail notification has been sent to " + to);
        System.out.println("The subject: " + subject + ", body: " + body);
    }

}
