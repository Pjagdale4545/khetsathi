package com.example.farmmitra.Service;

import com.example.farmmitra.dto.UserRegistrationDto;
import com.example.farmmitra.model.Farmer;
import com.example.farmmitra.Controller.FarmerController;
import com.example.farmmitra.Repository.FarmerRepository;
import com.example.farmmitra.model.Role; // Import the Role enum

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication; // ⬅️ Add this import



import java.util.Collections;
import java.util.Optional;

@Service
public class FarmerService implements UserDetailsService {
	  private static final Logger log = LoggerFactory.getLogger(FarmerController.class);
    private final FarmerRepository farmerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public FarmerService(FarmerRepository farmerRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.farmerRepository = farmerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerFarmer(UserRegistrationDto registrationDto) {
        // 1. Check if a farmer with the mobile number already exists
        // CHANGE: Using findByMobileNumber as per our updated model
        if (farmerRepository.findByMobileNumber(registrationDto.getMobileNumber()).isPresent()) {
            return "Mobile number is already registered.";
        }

        // 2. Hash the password
        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());

        // 3. Create a new Farmer entity
        // CHANGE: The constructor now matches the one in Farmer.java
        Farmer farmer = new Farmer(
            registrationDto.getFullName(),
            registrationDto.getMobileNumber(), // Use mobile number as the username
            encodedPassword,
            Role.FARMER // Use the Role enum
        );

        // 4. Save the farmer to the database
        farmerRepository.save(farmer);
        return "Registration successful!";
    }

    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        // Find the farmer by the mobile number
        return farmerRepository.findByMobileNumber(mobileNumber)
            .map(farmer -> new User(
                farmer.getMobileNumber(),
                farmer.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + farmer.getRole().name()))
            ))
            .orElseThrow(() -> new UsernameNotFoundException("Farmer with mobile number " + mobileNumber + " not found"));
    }
    
    public Optional<Farmer> getCurrentFarmer() {
        // Use the fully qualified name or ensure the import is correct
    	log.info("--Getting Current Farmer now");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); 
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String username = authentication.getName();
        log.info("--Logged in Farmer Username is "+username);
        return farmerRepository.findByMobileNumber(username);
    }
}

