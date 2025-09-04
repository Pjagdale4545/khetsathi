package com.example.farmmitra.Service;

import com.example.farmmitra.Controller.FarmerController;
import com.example.farmmitra.Repository.InquiryRepository;
import com.example.farmmitra.model.Buyer;
import com.example.farmmitra.model.Crop;
import com.example.farmmitra.model.Farmer;
import com.example.farmmitra.model.Inquiry;
import com.example.farmmitra.model.InquiryStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.farmmitra.model.Message; // ⭐ New Import
import com.example.farmmitra.model.User; // ⭐ New Import

@Service
public class InquiryService {

	 private static final Logger log = LoggerFactory.getLogger(FarmerController.class);
    @Autowired
    private InquiryRepository inquiryRepository;

    public void saveInquiry(Inquiry inquiry, Buyer buyer, Crop crop) {
        inquiry.setBuyer(buyer);
        inquiry.setCrop(crop);
        // Ensure status and inquiryDate are set, even if @PrePersist exists,
        // for clarity and to handle cases where @PrePersist might not trigger in certain contexts.
        if (inquiry.getInquiryDate() == null) {
            inquiry.setInquiryDate(LocalDateTime.now());
        }
        if (inquiry.getStatus() == null) {
            inquiry.setStatus(InquiryStatus.PENDING);
        }
        inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getInquiriesForFarmer(Farmer currentFarmer) {
        System.out.println("Fetching inquiries for farmer ID: " + currentFarmer.getId() + " - Full Name: " + currentFarmer.getFullName());
        log.info("Khet Insite the get enquiry");
        // ⭐ CRITICAL: Call the new, explicitly defined query method from InquiryRepository
        // This is the method defined in the 'Updated InquiryRepository with @Query' Canvas.
        List<Inquiry> inquiries = inquiryRepository.findInquiriesByFarmer(currentFarmer);

        if (inquiries == null || inquiries.isEmpty()) {
        	log.info("Khet if list is empty giving setting the list");
            System.out.println("No inquiries found for farmer ID: " + currentFarmer.getId());
          //  return Collections.emptyList();
            
            System.out.println("No inquiries found in DB. Adding test data manually...");

            // ⭐ Add dummy inquiries manually
            inquiries = new ArrayList<>();

            Inquiry test1 = new Inquiry();
            test1.setId(999L); // dummy id
            test1.setRequiredQuantity(90);
            test1.setNegotiatedRate(45.00);
            test1.setCrop(null); // or create a dummy crop
            test1.setBuyer(null); // or create a dummy buyer
            test1.setMessage("Testdate1");

            Inquiry test2 = new Inquiry();
            test2.setId(1000L);
            test2.setStatus(InquiryStatus.ACTIVE);
          //  test2.setFarmer(currentFarmer);

            inquiries.add(test1);
            inquiries.add(test2);
        } else {
            System.out.println("Found " + inquiries.size() + " inquiries for farmer ID: " + currentFarmer.getId());
            // Added detailed logging for debugging purposes
            inquiries.forEach(inq -> {
                String cropName = (inq.getCrop() != null ? inq.getCrop().getCropName() : "N/A");
                String buyerName = (inq.getBuyer() != null ? inq.getBuyer().getFullName() : "N/A");
                System.out.println("  - Inquiry ID: " + inq.getId() + ", Crop: " + cropName + ", Buyer: " + buyerName + ", Status: " + inq.getStatus());
            });
            return inquiries;
        }
		return inquiries;
    }    
}
