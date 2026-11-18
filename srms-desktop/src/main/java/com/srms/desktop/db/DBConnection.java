package com.srms.desktop.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC connection factory for the Swing client.
 * Reads db.properties and lets DB_URL / DB_USER / DB_PASSWORD (env or -D) override it.
 */
public final class DBConnection {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) PROPS.load(in);
        } catch (IOException e) {
            System.err.println("Could not read db.properties: " + e.getMessage());
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");          // load the MySQL JDBC driver
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC driver not found on classpath");
        }
    }

    private DBConnection() { }

    static String setting(String key, String prop) {
        String v = System.getProperty(key);
        if (v == null || v.isBlank()) v = System.getenv(key);
        return (v == null || v.isBlank()) ? PROPS.getProperty(prop) : v;
    }

    public static String url() { return setting("DB_URL", "db.url"); }

    /** Opens a new JDBC connection (caller closes it with try-with-resources). */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url(), setting("DB_USER", "db.user"), setting("DB_PASSWORD", "db.password"));
    }
}
