<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="org.tfl.backend.LabelEnum" %>

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
    <table>
        <tr>
            <th>ID#</th>
            <th>Content</th>
            <th>Author</th>
            <th>Date</th>
            <th>Classification</th>
        </tr>
        <c:forEach var="notice" items="${notices}">
            <tr>
                <td>${notice.id}</td>
                <td>${notice.content}</td>
                <td>${notice.author}</td>
                <td>${notice.date}</td>
                <td>${LabelEnum.fromValue(notice.label).labelName}</td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
