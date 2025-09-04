package com.example.farmmitra.Service;

import com.example.farmmitra.model.Farmer;
import com.example.farmmitra.Repository.FarmerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FarmerDetailsService implements UserDetailsService {

    @Autowired
    private FarmerRepository farmerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Farmer farmer = farmerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Farmer not found with username: " + username));
        
        // ⭐ Return your custom UserDetails implementation
        return new FarmerUserDetails(farmer);
    }
}