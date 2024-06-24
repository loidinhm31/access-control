package org.tfl.backend.dao;

import jakarta.servlet.ServletException;
import org.tfl.backend.LabelEnum;
import org.tfl.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Logger;


public class NewsDAO {
    private static final Logger log = Logger.getLogger(NewsDAO.class.getName());

    public static boolean insertNews(String userid, String content, Date date, int label) throws ServletException {
        String sql = "INSERT INTO news (userid, content, date, label) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userid);
            stmt.setString(2, content);
            stmt.setTimestamp(3, new Timestamp(date.getTime()));
            stmt.setInt(4, label);

            stmt.executeUpdate();

            boolean rowInserted = stmt.executeUpdate() > 0;
            return rowInserted;

        } catch (SQLException e) {
            log.severe("Error get user full name: " + e.getMessage());
            throw new ServletException("Database error", e);
        }
    }

    public static boolean isLabelValid(String userLevel, String messageLevel) {
        LabelEnum messageLabelEnum = LabelEnum.fromName(messageLevel);
        LabelEnum userLevelEnum = LabelEnum.fromName(userLevel);

        if (messageLabelEnum == null || userLevelEnum == null) {
            return false;
        }
        return messageLabelEnum.getLabelValue() <= userLevelEnum.getLabelValue();
    }
}
