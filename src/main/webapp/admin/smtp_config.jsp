<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ikon.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script src="js/jquery-1.3.2.min.js" type="text/javascript"></script>
  <script src="js/vanadium-min.js" type="text/javascript"></script>
  <title>SMTP Configuration</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
     <ul id="breadcrumb">
        <li class="action">
          <a href="Auth">User list</a>
        </li>
        <li class="action">
          <a href="Auth?action=roleList">Role list</a>
        </li>
        <li class="action">
          <a href="${messageList}">Message queue</a>
        </li>
        <li class="action">
          <a href="LoggedUsers">Logged users</a>
        </li>
         <li class="path">
          <a href="smtp.jsp">SMTP Configuration</a>
        </li>
      </ul>
     <h1>Configure SMTP Account</h1>
      <form action="SMTPConfig?action=check" method="post">
        <table class="form" width="372px">
          <tr>
            <td>SMTP host</td>
            <td width="100%">
                   <input name="smtphost" value="${smtphost}" required autofocus
						oninvalid="setCustomValidity('Please enter your SMTP server name.')"
						onchange="try{setCustomValidity('')}catch(e){}" />
            </td>
          </tr>
          <tr>
            <td>SMTP port</td>
            <td>
                  <input type="number" name="smtpport" value="${smtpport}" required
						oninvalid="setCustomValidity('Please enter your SMTP server port.')"
						onchange="try{setCustomValidity('')}catch(e){}" />
            </td>
          </tr>
          <tr>
            <td>SMTP User</td>
            <td>
				  <input name="username" value="${username}" required/>
            </td>
          </tr>
          <tr>
            <td>SMTP Password</td>
            <td><input type="password" name="password" value="${password}" required/></td>
          </tr>
          <tr>
            <td>Authentication required</td>
            <td>
             	<select name="isSSL">
             		<option value="false">Not required</option>
             		<option value="true">Required</option>
             	</select>
            </td>
          </tr>
          <tr>
          <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel"/>
              <input type="submit" value="Send"/>
            </td>
          </tr>
        </table>
      </form>
      <c:if test="${not empty ERROR }">
			<script type="text/javascript">
				alert("${ERROR}");
			</script>
	  </c:if>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
