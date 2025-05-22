<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <title>Error Page</title>
</head>
<body>
<div class="mainbody">

    <h3>An error has occurred</h3>

    <c:choose>
        <c:when test="${not empty error}">
            <div class="settingmsg">
                    ${error}
            </div>
        </c:when>
        <c:otherwise>
            <p>
                Oops... Please check back later.
                Contact the administrator if the problem persists.
            </p>
        </c:otherwise>
    </c:choose>

    <p>
        <a href="${pageContext.request.contextPath}/">Return to login page</a>
    </p>

    <footer class="bottom">
        <p>
            Copyright &copy; 2025 All rights reserved.<br>
        </p>
    </footer>
</div>

</body>
</html>