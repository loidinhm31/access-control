package org.tfl.backend.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.tfl.backend.dao.LoginDAO;


import java.io.IOException;
import java.util.logging.Logger;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static final Logger log = Logger.getLogger(LoginController.class.getName());

    @GetMapping
    public String loginPage() {
        return "index";
    }

    @PostMapping
    public ModelAndView handleLoginPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return new ModelAndView(new RedirectView("/login"));
        }

        String userid = request.getParameter("userid");
        String password = request.getParameter("password");

        if (userid == null || password == null) {
            return new ModelAndView(new RedirectView("/login"));
        }

        if (LoginDAO.isAccountLocked(userid, request.getRemoteAddr())) {
            log.warning("Error: Account is locked " + userid + " " + request.getRemoteAddr());
            return new ModelAndView(new RedirectView("/login"));
        } else if (LoginDAO.validateUser(userid, password, request.getRemoteAddr())) {
            password = null;

            // Prevent Session fixation, invalidate and assign a new session
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("userid2fa", userid);

            // Set the session id cookie with HttpOnly, secure, and same site flags
            String custsession = "JSESSIONID=" + session.getId() + ";Path=/;Secure;HttpOnly;SameSite=Strict";
            response.setHeader("Set-Cookie", custsession);

            return new ModelAndView("forward:/otp");
        } else {
            log.warning("Error: Username or password is invalid " + userid + " " + request.getRemoteAddr());
            String remoteip = request.getRemoteAddr();
            LoginDAO.incrementFailLogin(userid, remoteip);
            return new ModelAndView(new RedirectView("/login"));
        }
    }
}
