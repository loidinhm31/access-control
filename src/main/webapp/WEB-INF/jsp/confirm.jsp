<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@page import="org.tfl.backend.AuthSession" %>
<%@page import="org.tfl.backend.dao.OTPDAO" %>

<%
    response.setHeader("Cache-Control", "no-store");

    if (!AuthSession.check2FASession(request, response, "/")) {
        return;
    }

    String userid2fa = (String) session.getAttribute("userid2fa");
    String remoteip = request.getRemoteAddr();
    String otpSecretBase32 = OTPDAO.getBase32OTPSecret(userid2fa, remoteip).toLowerCase();

    // Split the Base32 string into groups of 4 characters
    StringBuilder formattedOtpSecret = new StringBuilder();
    for (int i = 0; i < otpSecretBase32.length(); i += 4) {
        if (i > 0) {
            formattedOtpSecret.append(" ");
        }
        formattedOtpSecret.append(otpSecretBase32, i, Math.min(i + 4, otpSecretBase32.length()));
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
        <h3>Device Confirmation</h3>
    </div>

    <div class="form-content">
        <!-- Get OTPSecret by OTPDAO and display as Base32 Encode -->
        <p>Active key for Google Authenticator:</p>
        <p><b><%= formattedOtpSecret.toString() %></b></p>
    </div>

    <form method="POST" autocomplete="off" accept-charset="utf-8" action="${pageContext.request.contextPath}/otpctl">
        <ul class="form-ul">

            <li>
                <label>Enter OTP</label>
            </li>

            <li>
                <div id="msg" class="settingmsg">

                    <%
                        // Check for OTP error message
                        String otpError = (String) session.getAttribute("otperror");
                        if (otpError != null) {
                            session.removeAttribute("otperror");
                            out.println("Invalid OTP");
                        }
                    %>

                </div>
                <input type="text" required name="totp" size="25">
            </li>
            <li>
                <input type="submit" value="Confirm">
            </li>

        </ul>
    </form>

</div>

<%@include file="../../templates/footer.html" %>

</body>
</html>
