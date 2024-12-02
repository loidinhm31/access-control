package org.tfl.backend.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.tfl.backend.LabelEnum;
import org.tfl.backend.dao.NewsDAO;
import org.tfl.backend.dao.SecurityLabelDAO;
import org.tfl.backend.model.Notice;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/news")
public class NewsController {
    @GetMapping("/write-news")
    public String showPostPage(HttpServletRequest request, Model model) throws ServletException {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        if (userid == null) {
            return "redirect:/login";
        }

        // Get level of current user
        int labelLevel = SecurityLabelDAO.getSecurityLabelLevel(userid);

        model.addAttribute("userLevel", LabelEnum.fromValue(labelLevel).getLabelName());
        return "write-news";
    }

    @PostMapping("/write-news")
    public String postMessage(@RequestParam("message") String message,
                              @RequestParam("messageLevel") String messageLevel,
                              HttpServletRequest request,
                              Model model) throws ServletException {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");

        if (userid == null) {
            return "redirect:/login";
        }

        // Get level of current user
        int userLabelLevel = SecurityLabelDAO.getSecurityLabelLevel(userid);
        String userLevel = LabelEnum.fromValue(userLabelLevel).name();

        boolean isValid = NewsDAO.isLabelValidWrite(userLevel, messageLevel);

        if (!isValid) {
            model.addAttribute("notificationMsg", "Error! Message's level is lower than your level");
            model.addAttribute("userLevel", userLevel);
            return "write-news";
        }

        try {
            LabelEnum messageLabel = LabelEnum.fromName(messageLevel);
            NewsDAO.insertNews(userid, message, new Date(), messageLabel != null ? messageLabel.getLabelValue() : 0);
            model.addAttribute("notificationMsg", "Successful!");
        } catch (Exception e) {
            model.addAttribute("notificationMsg", "Error posting the message");
        }

        model.addAttribute("userLevel", userLevel);
        return "write-news";
    }

    @GetMapping
    public String showNewsPage(HttpServletRequest request, Model model) throws ServletException {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        if (userid == null) {
            return "redirect:/login";
        }

        // Get level of current user
        int userLabelLevel = SecurityLabelDAO.getSecurityLabelLevel(userid);

        List<Notice> notices = NewsDAO.selectAllNoticesForUser(userid, userLabelLevel);
        model.addAttribute("notices", notices);
        return "read-news";
    }
}
