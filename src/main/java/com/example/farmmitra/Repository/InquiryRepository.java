package com.example.farmmitra.Repository;

import com.example.farmmitra.model.Inquiry;
import com.example.farmmitra.model.InquiryStatus;
import com.example.farmmitra.model.Buyer;
import com.example.farmmitra.model.Farmer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // ⭐ Import @Query
import org.springframework.data.repository.query.Param; // ⭐ Import @Param
import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByBuyer(Buyer buyer);

    List<Inquiry> findByBuyerAndStatus(Buyer buyer, InquiryStatus status);

    // ⭐ Explicitly define the query to find inquiries for a farmer
    // Changed method name to avoid potential conflicts/ambiguity with derived queries
    @Query("SELECT i FROM Inquiry i JOIN i.crop c WHERE c.farmer = :farmer")
    List<Inquiry> findInquiriesByFarmer(@Param("farmer") Farmer farmer); // ⭐ Use @Param
}
