/**
 * @file User.java
 * @brief Represents a user in the system with properties for ID, username, and password.
 */

package com.aygazyanar.recipe;

/**
 * @class User
 * @brief Represents a user in the system with an ID, username, and password.
 *
 * Used for authentication and authorization purposes in the application.
 */
public class User {

    /**
     * @brief Unique identifier of the user.
     */
    private int id;

    /**
     * @brief Username of the user.
     */
    private String username;

    /**
     * @brief Password of the user.
     */
    private String password;

    /**
     * @brief Default constructor.
     */
    public User() {
    }

    /**
     * @brief Constructs a User with given ID, username, and password.
     * @param id The unique ID of the user.
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * @brief Gets the user ID.
     * @return The user ID.
     */
    public int getId() {
        return id;
    }

    /**
     * @brief Sets the user ID.
     * @param id The new ID to assign.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @brief Gets the username.
     * @return The username string.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @brief Sets the username.
     * @param username The username to assign.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @brief Gets the password.
     * @return The password string.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @brief Sets the password.
     * @param password The password to assign.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
