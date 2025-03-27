/**
 * @file UserDAOImpl.java
 * @brief Provides SQLite-based implementation of the UserDAO interface.
 */

package com.aygazyanar.recipe; ///< Main package for the Recipe Cost Calculator application.

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @class UserDAOImpl
 * @brief Implementation of the UserDAO interface using SQLite via JDBC.
 *
 * Provides methods to create users and retrieve users by username from the database.
 */
public class UserDAOImpl implements UserDAO {

    /**
     * @brief JDBC connection used for executing SQL statements.
     */
    private Connection connection;

    /**
     * @brief Constructor that initializes the database connection and ensures the users table exists.
     * @throws Exception If a database connection or table creation error occurs.
     */
    public UserDAOImpl() throws Exception {
        this.connection = DBConnection.getInstance().getConnection();
        createUsersTable();
    }

    /**
     * @brief Creates the "users" table if it does not already exist.
     * @throws Exception If an error occurs during table creation.
     */
    private void createUsersTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "username TEXT UNIQUE NOT NULL, "
                   + "password TEXT NOT NULL"
                   + ")";
        connection.createStatement().execute(sql);
    }

    /**
     * @brief Inserts a new user into the database.
     *
     * @param user The User object to be added.
     * @throws Exception If a database error occurs during the insert operation.
     */
    @Override
    public void createUser(User user) throws Exception {
        String sql = "INSERT INTO users (username, password) VALUES (?,?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, user.getUsername());
        pstmt.setString(2, user.getPassword());
        pstmt.executeUpdate();
    }

    /**
     * @brief Retrieves a user from the database by their username.
     *
     * @param username The username to search for.
     * @return The User object if found, otherwise null.
     * @throws Exception If a database query error occurs.
     */
    @Override
    public User getUserByUsername(String username) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password")
            );
        }
        return null;
    }
}
