package org.tfl.backend;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Serial;
import java.util.logging.Logger;


/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginControllerServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(LoginControllerServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginControllerServlet() {
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
        if (session == null) {// no existing session

            //TODO Redirect to index.jsp

        }


        String userid = request.getParameter("userid");
        String password = request.getParameter("password");

        if (userid == null || password == null) {

            //TODO Redirect to index.jsp

        }

        if (LoginDAO.isAccountLocked(userid, request.getRemoteAddr())) {
            log.warning("Error: Account is locked " + userid + " " + request.getRemoteAddr());
            response.sendRedirect("/src/main/webapp/index.jsp");

        } else if (LoginDAO.validateUser(userid, password, request.getRemoteAddr())) {
            password = null;
            //Prevent Session fixation, invalidate and assign a new session

            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("userid2fa", userid);
            //Set the session id cookie with HttpOnly, secure and samesite flags
            String custsession = "JSESSIONID=" + session.getId() + ";Path=/;Secure;HttpOnly;SameSite=Strict";
            response.setHeader("Set-Cookie", custsession);

            //Dispatch request to otp.jsp

        } else {
            log.warning("Error: Username or password is invalid " + userid + " " + request.getRemoteAddr());
            String remoteip = request.getRemoteAddr();
            LoginDAO.incrementFailLogin(userid, remoteip);
            response.sendRedirect("index.jsp");
        }

    }

}
