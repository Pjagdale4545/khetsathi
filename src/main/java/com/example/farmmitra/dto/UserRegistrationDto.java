package com.example.farmmitra.dto;

public class UserRegistrationDto {

    private String fullName;
    private String mobileNumber; // <-- Add this field
    private String password;
    private String email; // Assuming this field exists from previous errors


    public UserRegistrationDto() {
    }

    public UserRegistrationDto(String fullName, String mobileNumber, String password, String email) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.email = email;
    }

    // --- Getters and Setters ---

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber; // <-- Add this getter
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber; // <-- Add this setter
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}