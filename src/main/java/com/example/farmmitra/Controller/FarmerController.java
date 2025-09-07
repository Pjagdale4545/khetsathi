package com.example.farmmitra.Controller;

import com.example.farmmitra.Repository.CropRepository;
import com.example.farmmitra.Repository.FarmerRepository;
import com.example.farmmitra.Repository.InquiryRepository;
import com.example.farmmitra.Repository.UserRepository;
import com.example.farmmitra.Service.CropService;
import com.example.farmmitra.Service.FarmerService;
import com.example.farmmitra.Service.InquiryService;
import com.example.farmmitra.Service.OtpService;
import com.example.farmmitra.dto.UserRegistrationDto;
import com.example.farmmitra.model.Farmer;
import com.example.farmmitra.model.Role;
import com.example.farmmitra.model.Crop;
import com.example.farmmitra.model.Inquiry;
import com.example.farmmitra.model.InquiryStatus;
import com.example.farmmitra.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.farmmitra.dto.CropDto;


@Controller
@RequestMapping("/farmer")
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
    private CropService cropService;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public FarmerController(FarmerRepository farmerRepository, PasswordEncoder passwordEncoder, OtpService otpService) {
        this.farmerRepository = farmerRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    @GetMapping("/login")
    public String showFarmerLoginPage() {
        log.debug("We are giving login of farmer");
        return "farmer-login";
    }

    @GetMapping("/register")
    public String showFarmerRegisterPage(@ModelAttribute("registrationForm") UserRegistrationDto registrationForm, Model model) {
        model.addAttribute("registrationForm", registrationForm);
        return "farmer-register";
    }

    @PostMapping("/send-otp")
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

    @PostMapping("/register")
    public String registerFarmer(@ModelAttribute("registrationForm") UserRegistrationDto registrationDto,
                                 @RequestParam("otp") String otp,
                                 RedirectAttributes redirectAttributes) {
        String mobileNumber = registrationDto.getMobileNumber();
        String fullName = registrationDto.getFullName();
        String email = registrationDto.getEmail();

        if (!otpService.verifyOtp(mobileNumber, otp)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid OTP. Please try again.");
            UserRegistrationDto errorDto = new UserRegistrationDto(fullName, mobileNumber, null, email);
            redirectAttributes.addFlashAttribute("registrationForm", errorDto);
            return "redirect:/farmer/register";
        }

        String defaultPassword = mobileNumber.substring(Math.max(0, mobileNumber.length() - 4));
        String encodedPassword = passwordEncoder.encode(defaultPassword);

        Farmer farmer = new Farmer();
        farmer.setFullName(fullName);
        farmer.setMobileNumber(mobileNumber);
        farmer.setUsername(mobileNumber);
        farmer.setPassword(encodedPassword);
        farmer.setRole(Role.FARMER);
        farmerRepository.save(farmer);

        log.info("Registration successful for {}. Default password is last 4 digits of mobile number.", mobileNumber);

        redirectAttributes.addFlashAttribute("message", "Registration successful! You can now log in.");
        return "redirect:/farmer/login";
    }

    // ⭐ This method was causing a conflict and has been removed.
    // The FarmerDashboardController is now solely responsible for the /farmer/dashboard URL.

    @GetMapping("/add-crop")
    public String showAddCropPage(Model model) {
        // Change from 'crop' to 'cropDto' to match the form submission
        model.addAttribute("cropDto", new CropDto());
        return "add-crop";
    }

    @PostMapping("/add-crop")
    // FIX: Change method parameter from 'Crop crop' to 'CropDto cropDto'
    public String addCrop(@ModelAttribute CropDto cropDto, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Farmer currentFarmer = farmerService.findByUsername(username);

        if (currentFarmer == null) {
            return "redirect:/logout";
        }

        // FIX: The saveCrop method already takes the DTO and the farmer, so no need to set the farmer here.
        cropService.saveCrop(cropDto, currentFarmer);


        redirectAttributes.addFlashAttribute("message", "Your crop has been added successfully!");
        return "redirect:/farmer/dashboard";
    }

    @PostMapping("/delete-crop")
    public String deleteCrop(@RequestParam("cropId") Long cropId) {
        cropService.deleteCrop(cropId);
        return "redirect:/farmer/dashboard";
    }

    // New methods for handling inquiries and chat
    @PostMapping("/inquiries/accept/{id}")
    public String acceptInquiry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        inquiryService.updateInquiryStatus(id, InquiryStatus.ACCEPTED);
        redirectAttributes.addFlashAttribute("message", "Inquiry accepted successfully!");
        return "redirect:/farmer/dashboard";
    }

    @PostMapping("/inquiries/reject/{id}")
    public String rejectInquiry(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        inquiryService.updateInquiryStatus(id, InquiryStatus.REJECTED);
        redirectAttributes.addFlashAttribute("message", "Inquiry rejected successfully!");
        return "redirect:/farmer/dashboard";
    }

    @GetMapping("/inquiries/chat/{inquiryId}")
    public String showChat(@PathVariable("inquiryId") Long inquiryId, Model model, Principal principal) {
        Optional<Inquiry> inquiryOptional = inquiryService.findInquiryById(inquiryId);
        if (inquiryOptional.isEmpty()) {
            return "redirect:/farmer/dashboard";
        }
        Inquiry inquiry = inquiryOptional.get();

        model.addAttribute("inquiry", inquiry);
        model.addAttribute("messages", inquiryService.getMessagesForInquiry(inquiry));

        // Get the current user's ID safely
        Optional<User> currentUserOptional = userRepository.findByMobileNumber(principal.getName());
        currentUserOptional.ifPresent(user -> model.addAttribute("currentUserId", user.getId()));

        return "inquiry-chat";
    }

    @PostMapping("/inquiries/chat/{inquiryId}/send")
    public String sendMessage(@PathVariable("inquiryId") Long inquiryId,
                              @RequestParam("content") String content,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        Optional<Inquiry> inquiryOptional = inquiryService.findInquiryById(inquiryId);
        Optional<User> senderOptional = userRepository.findByMobileNumber(principal.getName());

        if (inquiryOptional.isEmpty() || senderOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not send message. Inquiry or user not found.");
            return "redirect:/";
        }

        inquiryService.saveMessage(inquiryOptional.get(), senderOptional.get(), content);

        return "redirect:/farmer/inquiries/chat/" + inquiryId;
    }
}
