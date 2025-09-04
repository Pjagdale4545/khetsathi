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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.farmmitra.model.InquiryStatus;


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

    public Optional<Buyer> getCurrentBuyer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
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
    


}