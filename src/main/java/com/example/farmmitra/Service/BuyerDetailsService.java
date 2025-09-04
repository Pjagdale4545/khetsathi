package com.example.farmmitra.Service;

import com.example.farmmitra.model.Buyer;
import com.example.farmmitra.Repository.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class BuyerDetailsService implements UserDetailsService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Buyer buyer = buyerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Buyer not found with username: " + username));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_BUYER");

        // ⭐ The call to the constructor here is correct
        return new BuyerUserDetails(
                buyer.getUsername(),
                buyer.getPassword(),
                Collections.singletonList(authority),
                buyer.getFullName()
        );
    }
}