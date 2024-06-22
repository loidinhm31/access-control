package org.tfl.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class GeneralController {
    @GetMapping
    public String homePage() {
        return "index";
    }

    @GetMapping("/confirm")
    public String confirmPage() {
        return "confirm";
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

    @GetMapping("/logout")
    public String logoutPage() {
        return "logout";
    }

    @RequestMapping("/otp")
    public String handeOtpPage() {
        return "otp";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }

    @RequestMapping("/locked")
    public String lockPage() {
        return "locked.jsp";
    }
}
