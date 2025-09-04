package com.example.farmmitra.Repository;

import com.example.farmmitra.model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {

    /**
     * Finds a Buyer by their unique mobile number.
     * This is used for login and registration checks.
     */
    Optional<Buyer> findByMobileNumber(String mobileNumber);

    /**
     * Finds a Buyer by their username.
     * This is typically used by Spring Security to load the user details for authentication.
     * Returns an Optional to handle cases where the username does not exist.
     */
    Optional<Buyer> findByUsername(String username);
}