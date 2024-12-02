package org.tfl.backend.dao;

import jakarta.servlet.ServletException;
import org.tfl.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger log = Logger.getLogger(UserDAO.class.getName());

    /**
     * Retrieves the username for a userid
     *
     * @param userid
     * @param remoteip
     * @return username if successful, null if there is an error
     */
    public static String getUserName(String userid, String remoteip) throws ServletException {
        String fullName = null;
        if (userid == null || remoteip == null) {
            throw new ServletException("Invalid parameters");
        }

        // Query the full name of user from database
        String query = "SELECT CONCAT(lastname, ' ', firstname) AS userName FROM users WHERE userid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    fullName = rs.getString("userName");
                }
            }
        } catch (SQLException e) {
            log.severe("Error get user full name: " + e.getMessage());
            throw new ServletException("Database error", e);
        }

        return fullName;
    }
}
