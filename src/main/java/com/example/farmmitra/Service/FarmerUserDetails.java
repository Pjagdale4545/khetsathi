package com.example.farmmitra.Service;

import com.example.farmmitra.model.Farmer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class FarmerUserDetails implements UserDetails {

    private final Farmer farmer;

    public FarmerUserDetails(Farmer farmer) {
        this.farmer = farmer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_FARMER"));
    }

    @Override
    public String getPassword() {
        return farmer.getPassword();
    }

    @Override
    public String getUsername() {
        return farmer.getUsername();
    }
    
    // ⭐ This method is what allows Thymeleaf to access the full name.
    public String getFullName() {
        return farmer.getFullName();
    }

    // Standard UserDetails methods
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}