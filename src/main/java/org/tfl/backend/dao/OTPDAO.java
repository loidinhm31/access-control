package org.tfl.backend.dao;

import jakarta.servlet.ServletException;
import org.tfl.crypto.CryptoUtil;
import org.tfl.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class OTPDAO {

    private static final Logger log = Logger.getLogger(OTPDAO.class.getName());

    /**
     * Retrieves the otp secret hexadecimal string from the userid
     *
     * @param userid
     * @param remoteip
     * @return hexadecimal secret string
     * @throws ServletException
     */
    public static String getOTPSecret(String userid, String remoteip) throws ServletException {
        String otpSecret = null;
        if (userid == null || remoteip == null) {
            throw new ServletException("Invalid parameters");
        }

        String query = "SELECT otpsecret FROM users WHERE userid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    otpSecret = rs.getString("otpsecret");
                }
            }
        } catch (SQLException e) {
            log.severe("Error retrieving OTP secret: " + e.getMessage());
            throw new ServletException("Database error", e);
        }

        return otpSecret;
    }

    /**
     * Retrieves the otp secret hexadecimal string from the userid
     *
     * @param userid
     * @param remoteip
     * @return hexadecimal secret string
     * @throws ServletException
     */
    public static String getBase32OTPSecret(String userid, String remoteip) throws ServletException {
        String hexaString = getOTPSecret(userid, remoteip);
        byte[] hexaCode = CryptoUtil.hexStringToByteArray(hexaString);
        return CryptoUtil.base32Encode(hexaCode);
    }

    /**
     * Check if a user account is locked
     *
     * @param userid
     * @param remoteip
     * @return true if account is locked, false otherwise
     * @throws ServletException
     */
    public static boolean isAccountLocked(String userid, String remoteip) throws ServletException {
        boolean isLock = true;
        if (userid == null || remoteip == null) {
            throw new ServletException("Invalid parameters");
        }

        String query = "SELECT isLocked FROM users WHERE userid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    isLock = rs.getBoolean("isLocked");
                }
            }
        } catch (SQLException e) {
            log.severe("Error checking account lock status: " + e.getMessage());
            throw new ServletException("Database error", e);
        }

        return isLock;
    }

    /**
     * Reset the failed login counts of a user to zero
     * If an account is locked an exception will be thrown
     *
     * @param userid
     * @param remoteip
     * @throws ServletException
     */
    public static void resetFailLogin(String userid, String remoteip) throws ServletException {
        if (userid == null || remoteip == null) {
            throw new ServletException("Invalid parameters");
        }

        String query = "UPDATE users SET faillogin = 0 WHERE userid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userid);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new ServletException("Failed to reset failed login attempts");
            }
        } catch (SQLException e) {
            log.severe("Error resetting failed login attempts: " + e.getMessage());
            throw new ServletException("Database error", e);
        }
    }

    public static void updateFailLogin(String userid, String remoteip) throws ServletException {
        if (userid == null || remoteip == null) {
            throw new ServletException("Invalid parameters");
        }

        String queryFailLogin = "SELECT faillogin FROM users WHERE userid = ?";

        int failLogin = 0;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(queryFailLogin)) {
            ps.setString(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    failLogin = rs.getInt("faillogin");
                }
            }
        } catch (SQLException e) {
            log.severe("Error checking account lock status: " + e.getMessage());
            throw new ServletException("Database error", e);
        }

        String query = "UPDATE users SET faillogin = ? WHERE userid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, failLogin + 1);
            ps.setString(2, userid);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated == 0) {
                throw new ServletException("Failed to reset failed login attempts");
            }
        } catch (SQLException e) {
            log.severe("Error resetting failed login attempts: " + e.getMessage());
            throw new ServletException("Database error", e);
        }
    }
}
