/**
 * @file DBConnection.java
 * @brief Singleton class responsible for establishing and providing access to the SQLite database connection.
 */

package com.aygazyanar.recipe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @class DBConnection
 * @brief Singleton class responsible for establishing and providing access to the SQLite database connection.
 *
 * This class ensures that only one instance of the database connection exists during runtime.
 * It initializes the SQLite JDBC driver and handles reconnection if the connection is closed.
 */
public class DBConnection {

    /**
     * @brief Singleton instance of DBConnection.
     */
    private static DBConnection instance;

    /**
     * @brief The active JDBC database connection.
     */
    private Connection connection;

    /**
     * @brief The JDBC URL used to connect to the SQLite database.
     */
    private final String url = "jdbc:sqlite:recipe_calculator.db";

    /**
     * @brief Private constructor that initializes the database connection.
     * 
     * Loads the SQLite JDBC driver and connects to the database file.
     * 
     * @throws SQLException If the JDBC driver is not found or the connection cannot be established.
     */
    private DBConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException ex) {
            System.out.println("SQLite JDBC driver not found.");
            throw new SQLException(ex);
        }
    }

    /**
     * @brief Gets the active database connection.
     * @return The JDBC Connection object.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @brief Returns the singleton instance of DBConnection.
     *
     * If the instance does not exist or the connection is closed, a new one is created.
     * 
     * @return DBConnection singleton instance.
     * @throws SQLException If connection initialization fails.
     */
    public static DBConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }
}
