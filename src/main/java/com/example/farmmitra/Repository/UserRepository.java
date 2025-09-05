package com.example.farmmitra.Repository;

import com.example.farmmitra.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Spring Data JPA will automatically provide CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their mobile number.
     * This method is used for authentication and other lookups.
     * @param mobileNumber The mobile number of the user.
     * @return An Optional containing the User if found, or empty otherwise.
     */
    Optional<User> findByMobileNumber(String mobileNumber);
}
