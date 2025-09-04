package com.example.farmmitra.Service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class BuyerUserDetails extends User {

    private final String fullName;

    // ⭐ This constructor must match the one being called in BuyerDetailsService
    public BuyerUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String fullName) {
        // Call the parent User class's constructor
        super(username, password, authorities);
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}