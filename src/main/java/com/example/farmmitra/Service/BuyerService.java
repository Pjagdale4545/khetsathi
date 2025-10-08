package com.example.farmmitra.Service;

import org.springframework.data.domain.Pageable;

import com.example.farmmitra.model.Buyer;
import com.example.farmmitra.model.Crop;
import com.example.farmmitra.model.Inquiry;
import com.example.farmmitra.Repository.BuyerRepository;
import com.example.farmmitra.Repository.CropRepository;
import com.example.farmmitra.Repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.farmmitra.model.InquiryStatus;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Optional<Buyer> getCurrentBuyer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return buyerRepository.findByUsername(username);
    }

    public Optional<Buyer> findByUsername(String username) {
        return buyerRepository.findByUsername(username);
    }


    public List<Crop> getLatestCrops() {
        return cropRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public List<Inquiry> getActiveInquiriesForBuyer(Buyer buyer) {
        return inquiryRepository.findByBuyerAndStatus(buyer, InquiryStatus.ACTIVE);
    }

    public void saveIdentityDetails(Buyer buyer, String aadharNumber, String panNumber) {
        buyer.setAadharNumber(aadharNumber);
        buyer.setPanNumber(panNumber);
        buyerRepository.save(buyer);
    }


    public Page<Crop> getCrops(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        if (search != null && !search.isEmpty()) {
            return cropRepository.findByCropNameContainingIgnoreCase(search, pageable);
        } else {
            return cropRepository.findAll(pageable);
        }
    }

    public Optional<Crop> getCropById(Long cropId) {
        return cropRepository.findById(cropId);
    }

    public void sendInquiry(Long cropId, Inquiry inquiry, String aadharNumber, String panNumber) {
        // 1. Get the authenticated user's details from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new IllegalStateException("User not authenticated.");
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        // 2. Find the Buyer and the Crop safely.
        Buyer buyer = buyerRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found with username: " + username));

        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new IllegalArgumentException("Crop not found with id: " + cropId));

        // 3. Update the buyer's identity details if they were submitted.
        // The controller should pass these from the form as separate parameters.
        if (StringUtils.hasText(aadharNumber)) {
            buyer.setAadharNumber(aadharNumber);
        }
        if (StringUtils.hasText(panNumber)) {
            buyer.setPanNumber(panNumber);
        }
        buyerRepository.save(buyer);

        // 4. Set the relationships and inquiry date on the passed-in inquiry object
        inquiry.setCrop(crop);
        inquiry.setBuyer(buyer);
        inquiry.setStatus(InquiryStatus.PENDING);
        inquiry.setInquiryDate(LocalDateTime.now());

        // 5. Save the complete inquiry to the database.
        inquiryRepository.save(inquiry);
    }
    public boolean updatePassword(String mobileNumber, String newPassword) {
        Optional<Buyer> buyerOptional = buyerRepository.findByMobileNumber(mobileNumber);

        if (buyerOptional.isPresent()) {
            Buyer buyer = buyerOptional.get();
            buyer.setPassword(passwordEncoder.encode(newPassword));
            buyerRepository.save(buyer);
            return true;
        } else {
            return false; // no buyer with that mobile number
        }
    }
}
