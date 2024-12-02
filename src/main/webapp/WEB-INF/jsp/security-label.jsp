<%@ page import="org.tfl.backend.AuthSession" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%
    response.setHeader("Cache-Control", "no-store");
    if (!AuthSession.validate(request, response)) {
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <title>Assign Security Level</title>
</head>
<body>
<div class="webformdiv">
    <div class="formheader">
        <h2>Assign security level</h2>
    </div>

    <form action="${pageContext.request.contextPath}/security-label" method="POST">
        <ul class="form-ul">
            <li>
                <label>Username</label>
                <input type="text" name="targetUserId" required>
            </li>
            <li>
                <label>Level</label>
                <select name="levelName">
                    <option value="TOP_SECRET">Top Secret</option>
                    <option value="SECRET">Secret</option>
                    <option value="CONFIDENTIAL">Confidential</option>
                    <option value="UNCLASSIFIED">Unclassified</option>
                </select>
            </li>

            <li class="message">
                <c:if test="${not empty error}">
                    <div class="settingmsg">
                            ${error}
                    </div>
                </c:if>

                <c:if test="${not empty message}">
                    <div class="success">
                            ${message}
                    </div>
                </c:if>
            </li>

            <li>
                <input type="submit" value="Apply">
            </li>
        </ul>
    </form>
</div>
</body>
</html>
