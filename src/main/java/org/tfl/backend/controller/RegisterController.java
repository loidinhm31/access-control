package org.tfl.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.tfl.backend.AppConstants;
import org.tfl.backend.dao.RegisterDAO;
import org.tfl.crypto.CryptoUtil;

import java.io.IOException;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @GetMapping
    public String showRegister() {
        return "register";
    }

    @PostMapping
    public ModelAndView register(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);
        if (session == null) {
            return new ModelAndView(new RedirectView("index.jsp"));
        }

        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String userid = request.getParameter("userid");
        String password = request.getParameter("password");
        String repassword = request.getParameter("repassword");

        if (firstName == null || lastName == null || userid == null || password == null || repassword == null) {
            return new ModelAndView(new RedirectView("index"));
        }

        if (password.length() < AppConstants.MIN_LENGTH_PASS) {
            return new ModelAndView(new RedirectView("error.html"));
        }

        if (!password.equals(repassword)) {
            return new ModelAndView(new RedirectView("error.html"));
        }

        if (RegisterDAO.findUser(userid, request.getRemoteAddr())) {
            return new ModelAndView(new RedirectView("error.html"));
        } else {
            byte[] salt = CryptoUtil.generateRandomBytes(CryptoUtil.SALT_SIZE);
            String base64Salt = java.util.Base64.getEncoder().encodeToString(salt);
            String otpSecret = CryptoUtil.genHexaOTPSecret();

            boolean isUserAdded = RegisterDAO.addUser(firstName, lastName, userid, password, base64Salt, otpSecret, request.getRemoteAddr());
            if (!isUserAdded) {
                return new ModelAndView(new RedirectView("error.html"));
            }

            password = null;
            repassword = null;
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("userid2fa", userid);

            String custsession = "JSESSIONID=" + session.getId() + ";Path=/;Secure;HttpOnly;SameSite=Strict";
            response.setHeader("Set-Cookie", custsession);

            return new ModelAndView("confirm");
        }
    }
}