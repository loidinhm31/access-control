package org.tfl.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.tfl.backend.LabelEnum;
import org.tfl.backend.dao.SecurityLabelDAO;


@Controller
@RequestMapping("/security-label")
public class SecurityLabelController {

    @GetMapping
    public String showAssignSecurityLabelPage() {
        return "security-label";
    }

    @PostMapping
    public ModelAndView assignSecurityLabel(HttpServletRequest request, @RequestParam("targetUserId") String targetUserId, @RequestParam("levelName") String levelName) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        if (userid == null) {
            return new ModelAndView(new RedirectView("/"));
        }

        if (!"admin".equals(userid)) {
            return new ModelAndView(new RedirectView("/logout"));
        }

        ModelAndView modelAndView = new ModelAndView("security-label");
        try {
            LabelEnum labelLevel = LabelEnum.fromName(levelName);
            if (labelLevel != null) {
                boolean success = SecurityLabelDAO.assignSecurityLabel(targetUserId, labelLevel.getLabelValue());
                if (success) {
                    modelAndView.addObject("message", "Successful!");
                } else {
                    modelAndView.addObject("error", "Not found user");
                }
                return modelAndView;
            }
            return new ModelAndView(new RedirectView("/error"));
        } catch (Exception e) {
            modelAndView.addObject("error", "Error: " + e.getMessage());
            return modelAndView;
        }
    }
}
