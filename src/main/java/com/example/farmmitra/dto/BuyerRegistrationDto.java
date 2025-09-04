package com.example.farmmitra.dto;

public class BuyerRegistrationDto {

    private String fullName;
    private String mobileNumber;
    private String email;
    private String organizationName;
   // private String password;

    // Constructors
    public BuyerRegistrationDto() {
    }

    public BuyerRegistrationDto(String fullName, String mobileNumber, String email, String organizationName, String password) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.organizationName = organizationName;
        //this.password = password;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
}