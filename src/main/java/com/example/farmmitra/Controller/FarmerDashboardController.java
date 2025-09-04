package com.example.farmmitra.Controller;

import com.example.farmmitra.Service.FarmerService;
import com.example.farmmitra.Service.InquiryService;
import com.example.farmmitra.dto.CropDto;
import com.example.farmmitra.model.Farmer;
import com.example.farmmitra.Repository.FarmerRepository;
import com.example.farmmitra.Service.CropService;
import com.example.farmmitra.model.Inquiry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class FarmerDashboardController {

    private static final Logger log = LoggerFactory.getLogger(FarmerController.class);

    private final FarmerRepository farmerRepository;
    private final CropService cropService;

    @Autowired
    public FarmerDashboardController(FarmerRepository farmerRepository, CropService cropService) {
        this.farmerRepository = farmerRepository;
        this.cropService = cropService;
    }
    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private FarmerService farmerService;

    @GetMapping("/farmer/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        // authentication.getName() returns the mobile number (your principal)
        String mobileNumber = authentication.getName();
        
        // Change: Use findByMobileNumber instead of findByUsername
        Optional<Farmer> farmerOptional = farmerRepository.findByMobileNumber(mobileNumber);

        if (farmerOptional.isPresent()) {
            Farmer farmer = farmerOptional.get();
            model.addAttribute("farmer", farmer);
            model.addAttribute("crops", cropService.findCropsByFarmer(farmer));
            model.addAttribute("cropDto", new CropDto());
        } else {
            throw new UsernameNotFoundException("Farmer not found");
        }
        Optional<Farmer> currentFarmerOptional = farmerService.getCurrentFarmer();
        Farmer currentFarmer = currentFarmerOptional.get();
        List<Inquiry> inquiries = inquiryService.getInquiriesForFarmer(currentFarmer);
        model.addAttribute("farmerName", currentFarmer.getFullName());
        model.addAttribute("inquiries", inquiries);

        // Debug attributes
        model.addAttribute("debug_farmerName", currentFarmer.getFullName());
        model.addAttribute("debug_farmerId", currentFarmer.getId());
        model.addAttribute("debug_inquiriesCount", inquiries != null ? inquiries.size() : 0);

        log.info("Loaded {} inquiries for farmer: {}",
                inquiries != null ? inquiries.size() : 0,
                currentFarmer.getFullName());

        return "farmer-dashboard";



    }

    @PostMapping("/farmer/dashboard/add-crop")
    public String addCrop(@ModelAttribute("cropDto") CropDto cropDto, Authentication authentication) {
        // authentication.getName() returns the mobile number (your principal)
        String mobileNumber = authentication.getName();

        // Change: Use findByMobileNumber instead of findByUsername
        Optional<Farmer> farmerOptional = farmerRepository.findByMobileNumber(mobileNumber);

        if (farmerOptional.isPresent()) {
            Farmer farmer = farmerOptional.get();
            cropService.saveCrop(cropDto, farmer);
        }

        return "redirect:/farmer/dashboard";
    }

    @PostMapping("/farmer/dashboard/delete-crop")
    public String deleteCrop(@RequestParam("cropId") Long cropId) {
        cropService.deleteCrop(cropId);
        return "redirect:/farmer/dashboard";
    }
}