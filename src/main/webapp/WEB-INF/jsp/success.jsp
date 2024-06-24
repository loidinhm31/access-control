<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@page import="org.tfl.backend.dao.UserDAO" %>
<%@page import="org.tfl.backend.AuthSession" %>
<%@page import="org.tfl.backend.HtmlEscape" %>

<%

    response.setHeader("Cache-Control", "no-store");
    if (!AuthSession.validate(request, response)) {
        return;
    }

    String userid = (String) session.getAttribute("userid");

    if (userid == null) {
        response.sendRedirect("/error");
        return;
    }

    // Prevent CSRF by requiring OTP validation each time page is displayed.
    String anticsrf = (String) session.getAttribute("anticsrf_success");
    if (anticsrf == null) {// Token not present redirect back to OTP page for validation again
        session.removeAttribute("userid");
        session.setAttribute("userid2fa", userid);
        userid = null;
        RequestDispatcher rd = request.getRequestDispatcher("/otp");
        rd.forward(request, response);
        return;
    } else {// Token present
        // Remove the token so that subsequent request will require OTP validation
        session.removeAttribute("anticsrf_success");
    }

    String username = UserDAO.getUserName(userid, request.getRemoteAddr());
    username = HtmlEscape.escapeHTML(username);

%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <title>Success Page</title>
</head>
<body>

<div class="mainbody">

    <p>
        Welcome
        <%
            if (username != null) {
                out.print(username);
            } else {
                out.print("Unknown");
            }

        %>
        <br>
    </p>

    <p>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/">View notices</a>
    </p>
    <p>
        <a href="${pageContext.request.contextPath}/news/write-news">Write notices</a>
    </p>

    <%@include file="../../templates/footer.html" %>


</div>


</body>
</html>
