package com.example.farmmitra.Controller;

import com.example.farmmitra.Repository.BuyerRepository;
import com.example.farmmitra.Repository.CropRepository;
import com.example.farmmitra.Service.BuyerService;
import com.example.farmmitra.Service.OtpService;
import com.example.farmmitra.Service.SmsService;
import com.example.farmmitra.dto.BuyerRegistrationDto;
import com.example.farmmitra.model.Buyer;
import com.example.farmmitra.model.Crop;
import com.example.farmmitra.model.Inquiry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private final CropRepository cropRepository;

    @Autowired
    public BuyerController(BuyerRepository buyerRepository, PasswordEncoder passwordEncoder, OtpService otpService, SmsService smsService, BuyerService buyerService,CropRepository cropRepository) {
        this.buyerRepository = buyerRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.smsService = smsService;
        this.buyerService = buyerService;
        this.cropRepository = cropRepository; // ✅ inject here

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
                                @RequestParam("otp") String otp,
                                RedirectAttributes redirectAttributes) {

        String mobileNumber = registrationDto.getMobileNumber();

        if (!StringUtils.hasText(mobileNumber)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mobile number is required.");
            redirectAttributes.addFlashAttribute("registrationForm", registrationDto);
            return "redirect:/buyer/register";
        }

        if (!otpService.verifyOtp(mobileNumber, otp)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid OTP. Please try again.");
            redirectAttributes.addFlashAttribute("registrationForm", registrationDto);
            return "redirect:/buyer/register";
        }

        String fullName = registrationDto.getFullName();
        String email = registrationDto.getEmail();
        String organizationName = registrationDto.getOrganizationName();

        String defaultPassword = mobileNumber.substring(Math.max(0, mobileNumber.length() - 4));
        String encodedPassword = passwordEncoder.encode(defaultPassword);

        Buyer buyer = new Buyer();
        buyer.setRole("BUYER");
        buyer.setFullName(fullName);
        buyer.setMobileNumber(mobileNumber);
        buyer.setUsername(mobileNumber);
        buyer.setPassword(encodedPassword);
        
        buyer.setEmail(StringUtils.hasText(email) ? email : null); 
        
        buyer.setOrganizationName(organizationName);
        buyerRepository.save(buyer);

        String smsMessage = "Dear " + fullName + ", welcome to Khetsathi! Your username is " + mobileNumber + " and your default password is " + defaultPassword + ".";
        smsService.sendSms(mobileNumber, smsMessage);

        redirectAttributes.addFlashAttribute("message", "Registration successful! You can now log in.");
        return "redirect:/buyer/login";
    }
    
    public List<Crop> findByName(String search) {
        return cropRepository.findByCropNameContainingIgnoreCase(search);
    }


    @GetMapping("/buyer/dashboard")
    public String showBuyerDashboard(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) Optional<String> search,
                                     Model model) {
        
        // Get the authenticated buyer
        Optional<Buyer> currentBuyerOptional = buyerService.getCurrentBuyer();
        
        if (currentBuyerOptional.isEmpty()) {
            return "redirect:/buyer/login";
        }
        
        Buyer currentBuyer = currentBuyerOptional.get();
         try {
        // Fetch crops with pagination and search
        Page<Crop> cropsPage = buyerService.getCrops(page, size, search.orElse(""));

        // Fetch active inquiries
        List<Inquiry> activeInquiries = buyerService.getActiveInquiriesForBuyer(currentBuyer);

        // Add all required attributes to the model
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
        
        // Add error message to the model if you have a dedicated error page
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
        return "buyer-inquiries";
    }
}