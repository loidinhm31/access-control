package org.tfl.backend.dao;

import jakarta.servlet.ServletException;
import org.tfl.backend.LabelEnum;
import org.tfl.backend.model.Notice;
import org.tfl.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class NewsDAO {
    private static final Logger log = Logger.getLogger(NewsDAO.class.getName());

    public static boolean insertNews(String userid, String content, Date date, int label) throws ServletException {
        if (userid == null || date == null || label == 0) {
            throw new ServletException("Invalid parameters");
        }

        String sql = "INSERT INTO news (userid, content, date, label) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userid);
            stmt.setString(2, content);
            stmt.setTimestamp(3, new Timestamp(date.getTime()));
            stmt.setInt(4, label);

            boolean rowInserted = stmt.executeUpdate() > 0;
            return rowInserted;

        } catch (SQLException e) {
            log.severe("Error get user full name: " + e.getMessage());
            throw new ServletException("Database error", e);
        }
    }

    public static boolean isLabelValidWrite(String userLevel, String messageLevel) {
        LabelEnum messageLabelEnum = LabelEnum.fromName(messageLevel);
        LabelEnum userLevelEnum = LabelEnum.fromName(userLevel);

        if (messageLabelEnum == null || userLevelEnum == null) {
            return false;
        }
        return messageLabelEnum.getLabelValue() >= userLevelEnum.getLabelValue();
    }

    public static List<Notice> selectAllNoticesForUser(String userid, int userLevel) throws ServletException {
        if (userid == null) {
            throw new ServletException("Invalid parameters");
        }

        List<Notice> notices = new ArrayList<>();

        String sql = "SELECT * FROM news WHERE label <= ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userLevel);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String content = rs.getString("content");
                String author = rs.getString("userid");
                String date = rs.getString("date");
                String label = rs.getString("label");
                notices.add(new Notice(id, content, author, date, label));
            }
        } catch (SQLException e) {
            log.severe("Error get news for user: " + e.getMessage());
            throw new ServletException("Database error", e);
        }
        return notices;
    }
}
