<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">

    <title>Post message</title>
</head>
<body>
<div class="webformdiv">
    <div class="formheader">
        <h3>Post message</h3>
    </div>

    <form autocomplete="off" accept-charset="utf-8" method="POST"
          action="${pageContext.request.contextPath}/news/write-news">
        <ul class="form-ul">
            <li>
                <b>Message</b>
            </li>
            <li>
                <textarea name="message" rows="5" cols="30" required></textarea>
            </li>
            <li>
                <b>Your level: ${userLevel}</b>
            </li>
            <li>
                Message level
            </li>
            <li>
                <select name="messageLevel">
                    <option value="TOP_SECRET">Top Secret</option>
                    <option value="SECRET">Secret</option>
                    <option value="CONFIDENTIAL">Confidential</option>
                    <option value="UNCLASSIFIED">Unclassified</option>
                </select>
            </li>
            <li>
                <c:if test="${not empty notificationMsg}">
                    <p class="settingmsg">${notificationMsg}</p>
                </c:if>
            </li>
            <li>
                <input type="submit" value="Submit">
            </li>
        </ul>
    </form>
</div>
</body>
</html>
