/**
 * @file AuthService.java
 * @brief Provides authentication services such as user registration and login.
 */

package com.aygazyanar.recipe;

/**
 * @class AuthService
 * @brief Provides authentication services such as user registration and login.
 *
 * Uses UserDAO to interact with user data stored in the database.
 */
public class AuthService {

    /**
     * @brief Data access object for user operations.
     */
    private UserDAO userDAO;

    /**
     * @brief Constructs the AuthService and initializes the UserDAO implementation.
     * @throws Exception if the UserDAO initialization fails.
     */
    public AuthService() throws Exception {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * @brief Registers a new user with the given username and password.
     *
     * If the username is already taken, registration fails.
     *
     * @param username The username of the user to be registered.
     * @param password The password of the user to be registered.
     * @return true if registration is successful, false otherwise.
     */
    public boolean register(String username, String password) {
        try {
            if (userDAO.getUserByUsername(username) != null) {
                System.out.println("User already exists.");
                return false;
            }
            User user = new User(0, username, password);
            userDAO.createUser(user);
            System.out.println("User registered successfully.");
            return true;
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /**
     * @brief Attempts to log in the user with the provided credentials.
     *
     * @param username The username of the user attempting to log in.
     * @param password The password of the user.
     * @return A User object if login is successful; null otherwise.
     */
    public User login(String username, String password) {
        try {
            User user = userDAO.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                System.out.println("Login successful.");
                return user;
            } else {
                System.out.println("Invalid username or password.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            return null;
        }
    }
}
