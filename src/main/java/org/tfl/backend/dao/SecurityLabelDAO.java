package org.tfl.backend.dao;

import jakarta.servlet.ServletException;
import org.tfl.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class SecurityLabelDAO {
    private static final Logger log = Logger.getLogger(SecurityLabelDAO.class.getName());

    public static boolean assignSecurityLabel(String targetUserId, int level) throws ServletException {
        if (targetUserId == null) {
            throw new ServletException("Invalid parameters");
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE userid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, targetUserId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        return false;
                    }
                }
            }

            query = "UPDATE users SET label = ? WHERE userid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, level);
                stmt.setString(2, targetUserId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            log.severe("Error assign security label: " + e.getMessage());
            throw new ServletException("Database error", e);
        }
        return true;
    }
}