<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@page import="org.tfl.backend.AuthSession" %>

<%
    response.setHeader("Cache-Control", "no-store");

    if (!AuthSession.check2FASession(request, response, "/")) {
        return;
    }

%>


<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <title>2 Factor Authentication Page</title>
</head>
<body>


<div class="webformdiv">

    <div class="formheader">
        <h3>2 Factor Authentication</h3>
    </div>

    <c:if test="${not empty otperror}">
        <div class="settingmsg">
                ${otperror}
        </div>
    </c:if>

    <form method="POST" autocomplete="off" accept-charset="utf-8" action="/otpctl/2fa">
        <ul class="form-ul">
            <li>
                <label>Enter OTP</label>
                <input type="text" required name="totp" size="25">
            </li>
            <li>
                <input type="submit" value="Submit">
            </li>

        </ul>
    </form>

</div>

<%@include file="../../templates/footer.html" %>


</body>
</html>