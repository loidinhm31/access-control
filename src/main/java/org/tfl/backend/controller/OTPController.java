package org.tfl.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.tfl.backend.dao.OTPDAO;
import org.tfl.crypto.CryptoUtil;
import org.tfl.crypto.TimeBaseOTP;


import java.io.IOException;
import java.util.logging.Logger;

@Controller
@RequestMapping("/otpctl")
public class OTPController {

    private static final Logger log = Logger.getLogger(OTPController.class.getName());

    @PostMapping
    public ModelAndView handleOtp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        // Make sure it has a valid 2fa session from login page
        // userid2fa session attribute must be set
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid2fa");
        if (userid == null) {
            return new ModelAndView(new RedirectView("/"));
        }

        // Remove the userid2fa attribute to prevent multiple submission attempts
        session.removeAttribute("userid2fa");
        String otpvalue = request.getParameter("totp");

        if (otpvalue == null) {
            session.invalidate();
            return new ModelAndView(new RedirectView("/error"));
        }

        String otpsecret;
        try {
            otpsecret = OTPDAO.getOTPSecret(userid, request.getRemoteAddr());
        } catch (Exception e) {
            log.severe("Failed to retrieve OTP secret: " + e.getMessage());
            session.invalidate();
            return new ModelAndView(new RedirectView("/error"));
        }

        String otpresult = TimeBaseOTP.generateOTP(CryptoUtil.hexStringToByteArray(otpsecret));
        if (otpresult == null) {
            session.invalidate();
            return new ModelAndView(new RedirectView("/error"));
        }

        if (otpresult.equals(otpvalue)) {
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("userid", userid);
            session.setAttribute("anticsrf_success", "AntiCSRF");

            String custsession = "JSESSIONID=" + session.getId() + ";Path=/;Secure;HttpOnly;SameSite=Strict";
            response.setHeader("Set-Cookie", custsession);

            try {
                // Reset fail login attempt
                OTPDAO.resetFailLogin(userid, request.getRemoteAddr());
            } catch (Exception e) {
                log.severe("Failed to reset fail login count: " + e.getMessage());
                return new ModelAndView(new RedirectView("/error"));
            }
            // Redirect success page
            return new ModelAndView(new RedirectView("success"));
        } else { // Incorrect OTP value
            String remoteip = request.getRemoteAddr();
            log.warning("Error: Invalid otp value from " + remoteip + " for user " + userid);

            try {
                // Update fail login count.
                OTPDAO.updateFailLogin(userid, remoteip);

                if (OTPDAO.isAccountLocked(userid, remoteip)) {
                    session.invalidate();
                    return new ModelAndView(new RedirectView("locked.jsp"));
                } else {
                    session.setAttribute("userid2fa", userid);
                    session.setAttribute("otperror", "OTP is invalid.");
                    return new ModelAndView(new RedirectView("confirm"));
                }
            } catch (Exception e) {
                log.severe("Failed to update fail login count or check account lock: " + e.getMessage());
                session.invalidate();
                return new ModelAndView(new RedirectView("/error"));
            }
        }
    }

    @PostMapping("/2fa")
    public ModelAndView handleOtp2FA(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        // Make sure it has a valid 2fa session from login page
        // userid2fa session attribute must be set
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid2fa");
        if (userid == null) {
            return new ModelAndView(new RedirectView("/"));
        }

        // Remove the userid2fa attribute to prevent multiple submission attempts
        session.removeAttribute("userid2fa");
        String otpValue = request.getParameter("totp");

        if (otpValue == null) {
            session.invalidate();
            return new ModelAndView(new RedirectView("/error"));
        }

        String otpsecret;
        try {
            otpsecret = OTPDAO.getOTPSecret(userid, request.getRemoteAddr());
        } catch (Exception e) {
            log.severe("Failed to retrieve OTP secret: " + e.getMessage());
            session.invalidate();
            return new ModelAndView(new RedirectView("/error"));
        }

        String otpResult = TimeBaseOTP.generateOTP(CryptoUtil.hexStringToByteArray(otpsecret));
        if (otpResult == null) {
            session.invalidate();
            return new ModelAndView(new RedirectView("/error"));
        }

        if (otpResult.equals(otpValue)) {
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("userid", userid);
            session.setAttribute("anticsrf_success", "AntiCSRF");

            String custsession = "JSESSIONID=" + session.getId() + ";Path=/;Secure;HttpOnly;SameSite=Strict";
            response.setHeader("Set-Cookie", custsession);

            try {
                // Reset fail login attempt
                OTPDAO.resetFailLogin(userid, request.getRemoteAddr());
            } catch (Exception e) {
                log.severe("Failed to reset fail login count: " + e.getMessage());
                return new ModelAndView(new RedirectView("/error"));
            }
            // Redirect success page
            return new ModelAndView(new RedirectView("/success"));
        } else { // Incorrect OTP value
            String remoteIpAddr = request.getRemoteAddr();
            log.warning("Error: Invalid otp value from " + remoteIpAddr + " for user " + userid);

            try {
                // Update fail login count.
                OTPDAO.updateFailLogin(userid, remoteIpAddr);

                if (OTPDAO.isAccountLocked(userid, remoteIpAddr)) {
                    session.invalidate();
                    return new ModelAndView(new RedirectView("locked.jsp"));
                } else {
                    session.setAttribute("userid2fa", userid);
                    session.setAttribute("otperror", "Invalid OTP");
                    return new ModelAndView(new RedirectView("/otp"));
                }
            } catch (Exception e) {
                log.severe("Failed to update fail login count or check account lock: " + e.getMessage());
                session.invalidate();
                return new ModelAndView(new RedirectView("/error"));
            }
        }
    }
}
