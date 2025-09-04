package com.example.farmmitra.dto;

import java.math.BigDecimal;

public class CropDto {
    private String cropName;
    private BigDecimal quantity;
    private BigDecimal pricePerUnit;
    private String description; // ⭐ Make sure this field exists

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

    
}