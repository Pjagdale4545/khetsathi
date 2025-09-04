package com.example.farmmitra.Controller;

import com.example.farmmitra.model.Crop;
import com.example.farmmitra.model.Buyer;
import com.example.farmmitra.model.Inquiry;
import com.example.farmmitra.Service.CropService;
import com.example.farmmitra.Service.BuyerService;
import com.example.farmmitra.Service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/buyer")
public class InquiryController {

    @Autowired
    private BuyerService buyerService;

    @Autowired
    private CropService cropService;

    @Autowired
    private InquiryService inquiryService;

    @GetMapping("/inquire/{cropId}")
    public String showInquiryPage(@PathVariable Long cropId, Model model) {
        Optional<Buyer> currentBuyerOptional = buyerService.getCurrentBuyer();
        if (currentBuyerOptional.isEmpty()) {
            return "redirect:/buyer/login";
        }
        Buyer currentBuyer = currentBuyerOptional.get();
        
        Optional<Crop> cropOptional = cropService.getCropById(cropId);
        if (cropOptional.isEmpty()) {
            // Handle case where crop is not found
            return "redirect:/buyer/dashboard";
        }
        Crop crop = cropOptional.get();

        // Check if buyer has Aadhar and PAN details
        boolean hasIdentityDetails = currentBuyer.getAadharNumber() != null && currentBuyer.getPanNumber() != null;

        model.addAttribute("crop", crop);
        model.addAttribute("inquiry", new Inquiry());
        model.addAttribute("hasIdentityDetails", hasIdentityDetails);
        model.addAttribute("buyer", currentBuyer);

        return "inquiry-form";
    }

    @PostMapping("/send-inquiry")
    public String sendInquiry(@ModelAttribute("inquiry") Inquiry inquiry,
                              @RequestParam("cropId") Long cropId,
                              @RequestParam(name = "aadharNumber", required = false) String aadharNumber,
                              @RequestParam(name = "panNumber", required = false) String panNumber,
                              RedirectAttributes redirectAttributes) {

        Optional<Buyer> currentBuyerOptional = buyerService.getCurrentBuyer();
        Optional<Crop> cropOptional = cropService.getCropById(cropId);
        
        if (currentBuyerOptional.isEmpty() || cropOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting inquiry.");
            return "redirect:/buyer/dashboard";
        }

        Buyer currentBuyer = currentBuyerOptional.get();
        Crop crop = cropOptional.get();

        // Check and save Aadhar/PAN if provided
        if (aadharNumber != null && panNumber != null && (currentBuyer.getAadharNumber() == null || currentBuyer.getPanNumber() == null)) {
            buyerService.saveIdentityDetails(currentBuyer, aadharNumber, panNumber);
        }

        // Save the inquiry
        inquiryService.saveInquiry(inquiry, currentBuyer, crop);

        redirectAttributes.addFlashAttribute("successMessage", "Inquiry sent successfully!");
        return "redirect:/buyer/dashboard";
    }
}