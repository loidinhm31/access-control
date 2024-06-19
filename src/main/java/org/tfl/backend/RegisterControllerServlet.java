package org.tfl.backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.tfl.crypto.CryptoUtil;

import java.io.IOException;
import java.io.Serial;
import java.util.logging.Logger;


/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/register")
public class RegisterControllerServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(LoginControllerServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterControllerServlet() {
        super();

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);
        if (session == null) { // no existing session
            // Redirect to index.jsp
            response.sendRedirect("index.jsp");
        }
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String userid = request.getParameter("userid");
        String password = request.getParameter("password");
        String repassword = request.getParameter("repassword");

        if (firstName == null || lastName == null || userid == null || password == null || repassword == null) {
            // Redirect to index.jsp
            response.sendRedirect("index.jsp");
        }

        if (password.length() < AppConstants.MIN_LENGTH_PASS) {
            //TODO Redirect to error.html
            response.sendRedirect("error.html");
            return;  // Stop further processing
        }

        if (password.compareTo(repassword) != 0) {
            //TODO Redirect to error.html
            response.sendRedirect("error.html");
            return;  // Stop further processing
        }

        if (RegisterDAO.findUser(userid, request.getRemoteAddr())) {
            //TODO Redirect to error.html
            response.sendRedirect("error.html");
        } else {
            //TODO Generate salt by CryptoUtil
            byte[] salt = CryptoUtil.generateRandomBytes(CryptoUtil.SALT_SIZE);

            //TODO Encode Base64 salt
            String base64Salt = java.util.Base64.getEncoder().encodeToString(salt);

            //TODO	Generate hexadecimal OTP Secret by CryptoUtil
            String otpSecret = CryptoUtil.genHexaOTPSecret();

            //TODO Add new user to Database
            boolean isUserAdded = RegisterDAO.addUser(firstName, lastName, userid, password, base64Salt, otpSecret, request.getRemoteAddr());
            if (!isUserAdded) {
                response.sendRedirect("error.html");
                return;  // Stop further processing
            }

            password = null;
            repassword = null;
            //Prevent Session fixation, invalidate and assign a new session
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("userid2fa", userid);
            //Set the session id cookie with HttpOnly, secure and samesite flags
            String custsession = "JSESSIONID=" + session.getId() + ";Path=/;Secure;HttpOnly;SameSite=Strict";
            response.setHeader("Set-Cookie", custsession);

            //Dispatch request to confirm.jsp
            request.getRequestDispatcher("confirm.jsp").forward(request, response);

        }

    }
}
