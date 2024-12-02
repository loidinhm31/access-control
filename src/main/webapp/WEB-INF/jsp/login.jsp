<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    response.setHeader("Cache-Control", "no-store");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <title>Login Page</title>
</head>
<body>

<div class="webformdiv">
    <div class="formheader">
        <h3>Application Sign In</h3>
    </div>

    <c:if test="${not empty error}">
        <div class="settingmsg">
            ${error}
        </div>
    </c:if>

    <form method="POST" autocomplete="off" accept-charset="utf-8" action="${pageContext.request.contextPath}/login">
        <ul class="form-ul">
            <li>
                <label>Username</label>
                <input type="text" required autofocus name="userid">
            </li>
            <li>
                <label>Password</label>
                <input type="password" required name="password">
            </li>
            <li>
                <input type="submit" value="Login">
            </li>
        </ul>
    </form>
</div>

<div class="register-link">
    <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
</div>

<%@include file="../../templates/footer.html" %>

</body>
</html>