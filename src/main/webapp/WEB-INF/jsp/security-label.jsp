<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css">
    <title>Assign Security Level</title>
</head>
<body>
<h2>Assign security level</h2>
<form action="${pageContext.request.contextPath}/security-label" method="POST">
    <table>
        <tr>
            <td>Username:</td>
            <td><input type="text" name="targetUserId" required></td>
        </tr>
        <tr>
            <td>Level:</td>
            <td>
                <label>
                    <select name="levelName">
                        <option value="TOP_SECRET">Top Secret</option>
                        <option value="SECRET">Secret</option>
                        <option value="CONFIDENTIAL">Confidential</option>
                        <option value="UNCLASSIFIED">Unclassified</option>
                    </select>
                </label>
            </td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit" value="Apply"></td>
        </tr>
    </table>

    <div class="settingmsg">
        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
                out.println(error);
            }
        %>
    </div>
    <div class="success">
        <%
            String message = (String) request.getAttribute("message");
            if (message != null) {
                out.println(message);
            }
        %>
    </div>
</form>

</body>
</html>
