package com.example.farmmitra.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "buyers")
public class Buyer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String mobileNumber;
    private String username; // Will store mobile number for login
    private String password; // Encoded password
    private String role; // <--- ADD THIS FIELD
    private String aadharNumber;
    private String panNumber;


    @Column(nullable = true)
    private String email;

    private String organizationName; // New field for buyer

    // Constructors
    public Buyer() {
    }

    public Buyer(String fullName, String mobileNumber, String username, String password, String email, String organizationName, String role) { // <--- ADD role TO CONSTRUCTOR
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.username = username;
        this.password = password;
        this.email = email;
        this.organizationName = organizationName;
        this.role = role; // <--- INITIALIZE ROLE
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRole() { // <--- ADD THIS GETTER
        return role;
    }

    public void setRole(String role) { // <--- ADD THIS SETTER
        this.role = role;
    }
    
 

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }
    
    

    @Override
    public String toString() {
        return "Buyer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", role='" + role + '\'' + // <--- ADD ROLE TO TOSTRING
                '}';
    }

}