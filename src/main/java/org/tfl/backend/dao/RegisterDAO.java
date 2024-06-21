package org.tfl.backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Logger;
import org.tfl.crypto.CryptoUtil;
import org.tfl.util.DatabaseUtil;

public class RegisterDAO {

    private static final Logger log = Logger.getLogger(RegisterDAO.class.getName());

    /**
     * Check user existence
     *
     * @param userid
     * @param remoteip client ip address
     * @return true if user is existent, false otherwise
     */
    public static boolean findUser(String userid, String remoteip) {
        boolean isFound = false;
        if (userid == null || remoteip == null) {
            return false;
        }

        String query = "SELECT COUNT(*) FROM users WHERE userid = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    isFound = true;
                }
            }
        } catch (SQLException e) {
            log.severe("Error finding user: " + e.getMessage());
        }

        return isFound;
    }

    public static boolean addUser(String firstName, String lastName, String userid, String password, String salt, String otpSecret, String remoteip) {
        boolean isSuccess = false;
        if (firstName == null || lastName == null || userid == null || password == null || salt == null || otpSecret == null || remoteip == null) {
            return false;
        }

        // Hash password with salt
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        byte[] passwordHash = CryptoUtil.getPasswordKey(password.toCharArray(), saltBytes, CryptoUtil.PBE_ITERATION);
        String passwordHex = passwordHash != null ? CryptoUtil.byteArrayToHexString(passwordHash) : null;

        String query = "INSERT INTO users (userid, firstname, lastname, salt, password, isLocked, faillogin, otpsecret, label) " +
                "VALUES (?, ?, ?, ?, ?, 0, 0, ?, 1)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, userid);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, salt);
            ps.setString(5, passwordHex);
            ps.setString(6, otpSecret);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                isSuccess = true;
            }
        } catch (SQLException e) {
            log.severe("Error adding user: " + e.getMessage());
        }

        return isSuccess;
    }
}
