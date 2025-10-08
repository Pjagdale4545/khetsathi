package com.example.farmmitra.dto;

import java.time.LocalDateTime;

public class InquiryDto {

    private String name;
    private String email;
    private String subject;
    private String message;
    private LocalDateTime inquiryDateTime;

    public InquiryDto() {
        // Default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getInquiryDateTime() {
        return inquiryDateTime;
    }

    public void setInquiryDateTime(LocalDateTime inquiryDateTime) {
        this.inquiryDateTime = inquiryDateTime;
    }
}
