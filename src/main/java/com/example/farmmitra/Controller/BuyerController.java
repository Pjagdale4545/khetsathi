package com.example.farmmitra.Controller;

import com.example.farmmitra.Repository.BuyerRepository;
import com.example.farmmitra.Repository.CropRepository;
import com.example.farmmitra.Service.*;
import com.example.farmmitra.dto.BuyerRegistrationDto;
import com.example.farmmitra.dto.ForgotPasswordRequest;
import com.example.farmmitra.model.Buyer;
import com.example.farmmitra.model.Crop;
import com.example.farmmitra.model.Farmer;
import com.example.farmmitra.model.Inquiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class BuyerController {

    private final BuyerRepository buyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final SmsService smsService;
    private final BuyerService buyerService;
    private final CropService cropService;


    @Autowired
    public BuyerController(BuyerRepository buyerRepository, PasswordEncoder passwordEncoder, OtpService otpService, SmsService smsService, BuyerService buyerService, CropRepository cropRepository, CropService cropService) {
        this.buyerRepository = buyerRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.smsService = smsService;
        this.buyerService = buyerService;
        this.cropService = cropService;
    }

    @GetMapping("/buyer/login")
    public String showBuyerLoginPage() {
        return "buyer-login";
    }

    @GetMapping("/buyer/register")
    public String showBuyerRegisterPage(Model model, @ModelAttribute("registrationForm") BuyerRegistrationDto registrationForm) {
        return "buyer-register";
    }

    @PostMapping("/buyer/send-otp")
    public String sendOtp(@ModelAttribute("registrationForm") BuyerRegistrationDto registrationDto, RedirectAttributes redirectAttributes) {
        String mobileNumber = registrationDto.getMobileNumber();
        if (!StringUtils.hasText(mobileNumber)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mobile number is required.");
            redirectAttributes.addFlashAttribute("registrationForm", registrationDto);
            return "redirect:/buyer/register";
        }

        if (buyerRepository.findByMobileNumber(mobileNumber).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mobile number is already registered.");
            redirectAttributes.addFlashAttribute("registrationForm", registrationDto);
            return "redirect:/buyer/register";
        }

        String otp = otpService.generateOtp();
        otpService.saveAndSendOtp(mobileNumber, otp);

        redirectAttributes.addFlashAttribute("message", "OTP sent to " + mobileNumber);
        redirectAttributes.addFlashAttribute("registrationForm", registrationDto);

        return "redirect:/buyer/register";
    }

    @PostMapping("/buyer/register")
    public String registerBuyer(@ModelAttribute("registrationForm") BuyerRegistrationDto registrationDto,

                                RedirectAttributes redirectAttributes) {

        String mobileNumber = registrationDto.getMobileNumber();

        if (!StringUtils.hasText(mobileNumber)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mobile number is required.");
            redirectAttributes.addFlashAttribute("registrationForm", registrationDto);
            return "redirect:/buyer/register";
        }


        String fullName = registrationDto.getFullName();
        String email = registrationDto.getEmail();
        String organizationName = registrationDto.getOrganizationName();

        String password = registrationDto.getPassword();
        String confirmPassword = registrationDto.getConfirmPassword();

// Check if passwords match
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            redirectAttributes.addFlashAttribute("registrationForm", registrationDto);
            return "redirect:/buyer/register";
        }

// Encode the password
        String encodedPassword = passwordEncoder.encode(password);


        Buyer buyer = new Buyer();
        buyer.setRole("BUYER");
        buyer.setFullName(fullName);
        buyer.setMobileNumber(mobileNumber);
        buyer.setUsername(mobileNumber);
        buyer.setPassword(encodedPassword);

        buyer.setEmail(StringUtils.hasText(email) ? email : null);

        buyer.setOrganizationName(organizationName);
        buyerRepository.save(buyer);

        String smsMessage = "Dear " + fullName + ", welcome to Khetsathi! Your username is " + mobileNumber + " and your default password is "  + ".";
        smsService.sendSms(mobileNumber, smsMessage);

        redirectAttributes.addFlashAttribute("message", "Registration successful! You can now log in.");
        return "redirect:/buyer/login";
    }

    @GetMapping("/buyer/dashboard")
    public String showBuyerDashboard(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) Optional<String> search,
                                     Model model) {

        Optional<Buyer> currentBuyerOptional = buyerService.getCurrentBuyer();

        if (currentBuyerOptional.isEmpty()) {
            return "redirect:/buyer/login";
        }

        Buyer currentBuyer = currentBuyerOptional.get();
        try {
            Page<Crop> cropsPage = buyerService.getCrops(page, size, search.orElse(""));
            List<Inquiry> activeInquiries = buyerService.getActiveInquiriesForBuyer(currentBuyer);

            model.addAttribute("crops", cropsPage);
            model.addAttribute("currentPage", cropsPage.getNumber());
            model.addAttribute("pageSize", cropsPage.getSize());
            model.addAttribute("totalPages", cropsPage.getTotalPages());
            model.addAttribute("search", search.orElse(""));
            model.addAttribute("buyerName", currentBuyer.getFullName());
            model.addAttribute("activeInquiries", activeInquiries);

            return "buyer-dashboard";
        }catch (Exception e) {
            System.err.println("An error occurred during crop search: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while searching for crops.");
            return "redirect:/error";
        }
    }


    @GetMapping("/buyer/inquiries")
    public String showBuyerInquiries(Model model) {
        Optional<Buyer> currentBuyerOptional = buyerService.getCurrentBuyer();
        if (currentBuyerOptional.isEmpty()) {
            return "redirect:/buyer/login";
        }
        Buyer currentBuyer = currentBuyerOptional.get();
        model.addAttribute("buyerName", currentBuyer.getFullName());
        List<Inquiry> activeInquiries = buyerService.getActiveInquiriesForBuyer(currentBuyer);
        model.addAttribute("activeInquiries", activeInquiries);
        return "buyer_dashboard_inquiries";
    }

    @GetMapping("/buyer/inquire/{cropId}")
    public String showInquiryForm(@PathVariable("cropId") Long cropId, Model model) {
        Optional<Crop> cropOptional = cropService.getCropById(cropId);

        if (cropOptional.isPresent()) {
            model.addAttribute("crop", cropOptional.get());
            model.addAttribute("inquiryForm", new Inquiry()); // Use the Inquiry model directly

            Optional<Buyer> buyerOptional = buyerService.getCurrentBuyer();

            boolean hasIdentityDetails = false;
            if (buyerOptional.isPresent()) {
                Buyer buyer = buyerOptional.get();
                if (StringUtils.hasText(buyer.getAadharNumber()) && StringUtils.hasText(buyer.getPanNumber())) {
                    hasIdentityDetails = true;
                }
            }
            model.addAttribute("hasIdentityDetails", hasIdentityDetails);
            Crop crop = cropOptional.get();
            model.addAttribute("crop", crop);
            model.addAttribute("inquiryForm", new Inquiry());

// Farmer details
            Farmer farmer = crop.getFarmer(); // get the Farmer object directly
            model.addAttribute("farmer", farmer);



            if (farmer != null) {
                System.out.println("---- Farmer Details ----");
                System.out.println("ID: " + farmer.getId());
                System.out.println("Full Name: " + farmer.getFullName());
                System.out.println("Username: " + farmer.getUsername());
                System.out.println("Mobile Number: " + farmer.getMobileNumber());
               //System.out.println("Email: " + farmer.getEmail());
                //System.out.println("PAN: " + farmer.getPanNumber());
                //System.out.println("Aadhar: " + farmer.getAadharNumber());
                System.out.println("Role: " + farmer.getRole());
               // System.out.println("Created At: " + farmer.getCreatedAt());
                System.out.println("------------------------");
            } else {
                System.out.println("Farmer is null for this crop!");
            }
            return "inquire-form";


        } else {
            model.addAttribute("errorMessage", "Crop not found.");
            return "redirect:/buyer/dashboard";
        }
    }

    @PostMapping("/buyer/send-inquiry/{cropId}")
    public String sendInquiry(@PathVariable("cropId") Long cropId,
                              @ModelAttribute Inquiry inquiry,
                              @RequestParam(name = "aadharNumber", required = false) String aadharNumber,
                              @RequestParam(name = "panNumber", required = false) String panNumber,
                              RedirectAttributes redirectAttributes) {
        try {
            buyerService.sendInquiry(cropId, inquiry, aadharNumber, panNumber);
            redirectAttributes.addFlashAttribute("successMessage", "Inquiry sent successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to send inquiry: " + e.getMessage());
        }

        return "redirect:/buyer/dashboard";
    }
    @GetMapping("/buyer/forgot_password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("role", "Buyer");
        model.addAttribute("actionUrl", "/buyer/forgot_password");
        return "forgot_password"; // same template reused
    }

    @PostMapping(value = "/buyer/forgot_password", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> resetPassword(@RequestBody ForgotPasswordRequest request) {
        boolean updated = buyerService.updatePassword(request.getMobileNumber(), request.getNewPassword());
        if (updated) {
            return ResponseEntity.ok().body(
                    java.util.Map.of("message", "Password updated successfully!")
            );
        } else {
            return ResponseEntity.status(404).body(
                    java.util.Map.of("message", "Mobile number not found.")
            );
        }
    }
}


