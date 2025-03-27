/**
 * @file UserDAO.java
 * @brief Interface for user data access operations.
 */

package com.aygazyanar.recipe;

/**
 * @interface UserDAO
 * @brief Data Access Object interface for user-related database operations.
 *
 * Provides methods to create a new user and retrieve a user by username.
 */
public interface UserDAO {

    /**
     * @brief Creates a new user in the database.
     * @param user The User object containing user details to be stored.
     * @throws Exception If a database or connection error occurs.
     */
    void createUser(User user) throws Exception;

    /**
     * @brief Retrieves a user by their username.
     * @param username The username to search for.
     * @return The User object if found, otherwise null.
     * @throws Exception If a database or connection error occurs.
     */
    User getUserByUsername(String username) throws Exception;
}
