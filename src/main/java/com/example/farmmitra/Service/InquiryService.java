package com.example.farmmitra.Service;

import com.example.farmmitra.Controller.FarmerController;
import com.example.farmmitra.Repository.InquiryRepository;
import com.example.farmmitra.Repository.MessageRepository;
import com.example.farmmitra.Repository.UserRepository;
import com.example.farmmitra.model.Buyer;
import com.example.farmmitra.model.Crop;
import com.example.farmmitra.model.Farmer;
import com.example.farmmitra.model.Inquiry;
import com.example.farmmitra.model.InquiryStatus;
import com.example.farmmitra.model.Message;
import com.example.farmmitra.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
public class InquiryService {

    private static final Logger log = LoggerFactory.getLogger(FarmerController.class);
    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private MessageRepository messageRepository; // New Autowired Repository

    @Autowired
    private UserRepository userRepository;


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
        }
        return inquiries;
    }

    /**
     * Finds an inquiry by its ID.
     * @param inquiryId The ID of the inquiry to find.
     * @return An Optional containing the inquiry, or empty if not found.
     */
    public Optional<Inquiry> findInquiryById(Long inquiryId) {
        return inquiryRepository.findById(inquiryId);
    }

    /**
     * Updates the status of an inquiry.
     * @param inquiryId The ID of the inquiry to update.
     * @param newStatus The new status to set (e.g., ACCEPTED, REJECTED).
     */
    public void updateInquiryStatus(Long inquiryId, InquiryStatus newStatus) {
        inquiryRepository.findById(inquiryId).ifPresent(inquiry -> {
            inquiry.setStatus(newStatus);
            inquiryRepository.save(inquiry);
        });
    }

    /**
     * Saves a new chat message to the database.
     * @param inquiry The inquiry the message belongs to.
     * @param sender The user who sent the message.
     * @param content The message content.
     */
    public void saveMessage(Inquiry inquiry, User sender, String content) {
        Message message = new Message();
        message.setInquiry(inquiry);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
    }

    /**
     * Retrieves all messages for a given inquiry, sorted by timestamp.
     * @param inquiry The inquiry for which to retrieve messages.
     * @return A list of messages.
     */
    public List<Message> getMessagesForInquiry(Inquiry inquiry) {
        return messageRepository.findByInquiryOrderByTimestampAsc(inquiry);
    }
}
