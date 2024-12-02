package org.tfl.backend.dao;

import jakarta.servlet.ServletException;
import org.tfl.crypto.CryptoUtil;
import org.tfl.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Logger;

public class LoginDAO {
    private static final Logger log = Logger.getLogger(LoginDAO.class.getName());
    private static final int MAX_FAIL_LOGIN_ATTEMPTS = 5;

    /**
     * Validates user credential
     *
     * @param userid
     * @param password
     * @param remoteip client ip address
     * @return true if user is valid, false otherwise
     */
    public static boolean validateUser(String userid, String password, String remoteip) throws ServletException {
        if (userid == null || remoteip == null || password == null) {
            throw new ServletException("Invalid parameters");
        }

        boolean isValid = false;
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "SELECT salt, password, isLocked, faillogin FROM users WHERE userid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getInt("isLocked") == 1) {
                            return false;
                        }
                        String salt = rs.getString("salt");
                        String storedPassword = rs.getString("password");
                        byte[] saltBytes = Base64.getDecoder().decode(salt);
                        byte[] hashedPassword = CryptoUtil.getPasswordKey(password.toCharArray(), saltBytes, CryptoUtil.PBE_ITERATION);
                        String hashedPasswordHex = hashedPassword != null ? CryptoUtil.byteArrayToHexString(hashedPassword) : null;

                        if (storedPassword.equals(hashedPasswordHex)) {
                            isValid = true;
                        } else {
                            incrementFailLogin(userid, remoteip);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.severe("Error validating user: " + e.getMessage());
        }
        return isValid;
    }

    public static boolean isAccountLocked(String userid, String remoteip) throws ServletException {
        if (userid == null || remoteip == null) {
            throw new ServletException("Invalid parameters");
        }

        boolean isLocked = false;
        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "SELECT isLocked FROM users WHERE userid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt("isLocked") == 1) {
                        isLocked = true;
                    }
                }
            }
        } catch (Exception e) {
            log.severe("Error checking if account is locked: " + e.getMessage());
            throw new ServletException(e);
        }
        return isLocked;
    }

    /**
     * Increments the failed login count for a user
     * Locked the user account if fail logins exceed threshold.
     *
     * @param userid
     * @param remoteip
     * @throws ServletException
     */
    public static void incrementFailLogin(String userid, String remoteip) throws ServletException {
        if (userid == null || remoteip == null) {
            throw new ServletException("Invalid parameters");
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String query = "UPDATE users SET faillogin = faillogin + 1 WHERE userid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userid);
                stmt.executeUpdate();
            }

            query = "SELECT faillogin FROM users WHERE userid = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userid);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt("faillogin") > MAX_FAIL_LOGIN_ATTEMPTS) {
                        query = "UPDATE users SET isLocked = 1 WHERE userid = ?";
                        try (PreparedStatement lockStmt = conn.prepareStatement(query)) {
                            lockStmt.setString(1, userid);
                            lockStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            log.severe("Error incrementing fail login count: " + e.getMessage());
            throw new ServletException(e);
        }
    }
}
