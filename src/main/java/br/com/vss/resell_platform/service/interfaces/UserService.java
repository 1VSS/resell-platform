package br.com.vss.resell_platform.service.interfaces;

import br.com.vss.resell_platform.model.User;

import java.util.Optional;

/**
 * Service interface for managing users
 */
public interface UserService {
    
    /**
     * Find a user by username
     * 
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Save a user to the database
     * 
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);
    
    /**
     * Check if a username is already taken
     * 
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    boolean existsByUsername(String username);
} 