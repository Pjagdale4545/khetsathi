package com.example.farmmitra.Service;

import com.example.farmmitra.Repository.FarmerRepository;
import com.example.farmmitra.model.Farmer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Optional;

@Service
public class FarmerService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(FarmerService.class);
    private final FarmerRepository farmerRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public FarmerService(FarmerRepository farmerRepository, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.farmerRepository = farmerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Loads a user by their username (mobile number) for Spring Security.
     * @param mobileNumber The mobile number of the farmer.
     * @return UserDetails object representing the authenticated farmer.
     * @throws UsernameNotFoundException if the farmer is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
        return farmerRepository.findByMobileNumber(mobileNumber)
                .map(farmer -> new User(
                        farmer.getMobileNumber(),
                        farmer.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + farmer.getRole().name()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Farmer with mobile number " + mobileNumber + " not found"));
    }

    /**
     * Finds a farmer by their username (mobile number).
     * @param username The mobile number of the farmer.
     * @return The Farmer object, or null if not found.
     */
    public Farmer findByUsername(String username) {
        Optional<Farmer> farmerOptional = farmerRepository.findByMobileNumber(username);
        return farmerOptional.orElse(null);
    }

    /**
     * Retrieves the currently authenticated farmer from the security context.
     * @return An Optional containing the current Farmer, or an empty Optional if no user is authenticated.
     */
    public Optional<Farmer> getCurrentFarmer() {
        log.info("Getting current farmer now...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        String username = authentication.getName();
        log.info("Logged in farmer username is {}", username);
        return farmerRepository.findByMobileNumber(username);
    }
}
