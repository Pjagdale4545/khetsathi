package com.example.farmmitra.dto;

import java.math.BigDecimal;

public class CropDto {
    private String cropName;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private String description; // ⭐ Make sure this field exists

    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    private String city;
    private String villageName;
    private String pincode;

    // Getters and setters
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerQuintal) { this.pricePerUnit = pricePerQuintal; }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public String getFullLocation() {
        return state + ", " + city + ", " + villageName + " - " + pincode;
    }



}