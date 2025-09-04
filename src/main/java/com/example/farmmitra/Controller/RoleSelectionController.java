package com.example.farmmitra.Controller;

import com.example.farmmitra.Service.BuyerService; 
import com.example.farmmitra.Service.FarmerService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoleSelectionController {

    // Keep these if you need them for other logic, otherwise they can be removed
    private final BuyerService buyerService;
    private final FarmerService farmerService;

    @Autowired
    public RoleSelectionController(BuyerService buyerService, FarmerService farmerService) {
        this.buyerService = buyerService;
        this.farmerService = farmerService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/index")
    public String indexPage() {
        return "index"; // This will render index.html
    }


    @GetMapping("/select-role")
    public String selectRole(@RequestParam(value = "role", required = false) String role) {
        if (role == null) {
            return "select-role";
        }

        if ("farmer".equalsIgnoreCase(role)) {
            return "redirect:/farmer/login";
        } else if ("buyer".equalsIgnoreCase(role)) {
            return "redirect:/buyer/login"; // Redirect to buyer login page
        } else {
            return "redirect:/";
        }
    }
}