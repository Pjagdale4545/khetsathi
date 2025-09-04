package com.example.farmmitra.Controller;

import com.example.farmmitra.model.Farmer;
import com.example.farmmitra.model.Role;
import com.example.farmmitra.Repository.FarmerRepository;
import com.example.farmmitra.Service.FarmerService;
import com.example.farmmitra.Service.InquiryService;
import com.example.farmmitra.Service.OtpService;
import com.example.farmmitra.dto.UserRegistrationDto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.farmmitra.model.Inquiry;
import java.util.ArrayList;

@Controller
public class FarmerController {

    private final FarmerRepository farmerRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private static final Logger log = LoggerFactory.getLogger(FarmerController.class);
    
    @Autowired
    private FarmerService farmerService;

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    public FarmerController(FarmerRepository farmerRepository, PasswordEncoder passwordEncoder, OtpService otpService) {
        this.farmerRepository = farmerRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    @GetMapping("/farmer/login")
    public String showFarmerLoginPage() {
    	log.debug("We are giving login of farmer");
        return "farmer-login";
    }

    @GetMapping("/farmer/register")
    public String showFarmerRegisterPage(@ModelAttribute("registrationForm") UserRegistrationDto registrationForm, Model model) {
        model.addAttribute("registrationForm", registrationForm);
        return "farmer-register";
    }

    @PostMapping("/farmer/send-otp")
    public String sendOtp(@RequestParam("mobileNumber") String mobileNumber, RedirectAttributes redirectAttributes) {
        if (farmerRepository.findByMobileNumber(mobileNumber).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mobile number is already registered.");
            redirectAttributes.addFlashAttribute("registrationForm", new UserRegistrationDto(null, mobileNumber, null, null));
            return "redirect:/farmer/register";
        }
        
        String otp = otpService.generateOtp();
        otpService.saveAndSendOtp(mobileNumber, otp);

        redirectAttributes.addFlashAttribute("message", "OTP sent to " + mobileNumber);
        redirectAttributes.addFlashAttribute("registrationForm", new UserRegistrationDto(null, mobileNumber, null, null));

        return "redirect:/farmer/register";
    }

    @PostMapping("/farmer/register")
    public String registerFarmer(@ModelAttribute("registrationForm") UserRegistrationDto registrationDto,
                                 @RequestParam("otp") String otp,
                                 RedirectAttributes redirectAttributes) {
        
        String mobileNumber = registrationDto.getMobileNumber();
        String fullName = registrationDto.getFullName();
        String email = registrationDto.getEmail(); 

        // Check if OTP is valid
        if (!otpService.verifyOtp(mobileNumber, otp)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid OTP. Please try again.");
            // When redirecting back, do not repopulate password field for security/consistency
            UserRegistrationDto errorDto = new UserRegistrationDto(fullName, mobileNumber, null, email); 
            redirectAttributes.addFlashAttribute("registrationForm", errorDto);
            return "redirect:/farmer/register";
        }
        
        // ⭐ CRITICAL CHANGE: Derive the password directly from mobileNumber here
        // This ensures the password used for saving is always consistent
        String defaultPassword = mobileNumber.substring(Math.max(0, mobileNumber.length() - 4)); // Get last 4 digits
        String encodedPassword = passwordEncoder.encode(defaultPassword);

        // Create and save the new Farmer
        Farmer farmer = new Farmer();
        farmer.setFullName(fullName);
        farmer.setMobileNumber(mobileNumber);
        farmer.setUsername(mobileNumber); // Use mobile number as username for consistency
        farmer.setPassword(encodedPassword); // Save the generated and encoded password
        farmer.setRole(Role.FARMER);
        farmerRepository.save(farmer);

        System.out.println("Registration successful for " + mobileNumber + ". Default password is last 4 digits of mobile number.");

        redirectAttributes.addFlashAttribute("message", "Registration successful! You can now log in.");
        return "redirect:/farmer/login";
    }
    @GetMapping("/dashboard")
    public String showFarmerDashboard(Model model) {
    log.info("Accessing farmer dashboard");
    
    Optional<Farmer> currentFarmerOptional = farmerService.getCurrentFarmer();
    
    if (currentFarmerOptional.isEmpty()) {
        log.warn("No farmer found in current session");
        return "redirect:/farmer/login";
    }

    Farmer currentFarmer = currentFarmerOptional.get();

    try {
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
    } catch (Exception e) {
        log.error("Error loading inquiries for farmer: {}", currentFarmer.getFullName(), e);
        model.addAttribute("inquiries", new ArrayList<>());
        model.addAttribute("errorMessage", "Unable to load inquiries: " + e.getMessage());
        return "farmer-dashboard";
    }
}
}