package com.se.iotwatering.service;

public interface EmailService {
    void sendEmail(String to, String subject, String htmlContent, String toName);
}
