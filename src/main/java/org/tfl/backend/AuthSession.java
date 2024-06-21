package org.tfl.backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Logger;


public class AuthSession {

    private static final Logger log = Logger.getLogger(AuthSession.class.getName());

    /**
     * Validate if a session has been authenticated successfully and is still valid
     * Redirect to login page if session is not authenticated or invalid
     *
     * @param req
     * @param resp
     * @return true if session is authenticated successfully, false otherwise
     * @throws IOException
     * @throws ServletException
     */
    public static boolean validate(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req == null || resp == null) {
            throw new ServletException("Request or Response object is null");
        }

        HttpSession session = req.getSession(false);

        if (session == null) {
            resp.sendRedirect("/");
            return false;
        }

        if (session.getAttribute("userid") == null) { // not authenticated
            resp.sendRedirect("/");
            return false;
        }

        return true;
    }

    /**
     * Check if 2fa userid attribute is set. If it is not, redirect to specified error url
     *
     * @param req
     * @param resp
     * @param redirecturl
     * @return true if 2fa userid attribute is properly set, false otherwise
     * @throws IOException
     * @throws ServletException
     */
    public static boolean check2FASession(HttpServletRequest req, HttpServletResponse resp, String redirecturl)
            throws IOException, ServletException {
        if (req == null || resp == null || redirecturl == null) {
            throw new ServletException("Request, Response or Redirect URL is null");
        }

        HttpSession session = req.getSession(false);

        if (session == null) {
            resp.sendRedirect(redirecturl);
            return false;
        }

        String userid2fa = (String) session.getAttribute("userid2fa");
        if (userid2fa == null) {
            resp.sendRedirect(redirecturl);
            return false;
        }

        return true;
    }
}
