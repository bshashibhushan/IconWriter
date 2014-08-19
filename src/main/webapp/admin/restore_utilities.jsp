<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="com.ikon.servlet.admin.BaseServlet" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
 	<link rel="Shortcut icon" href="favicon.ico" />
  	<link rel="stylesheet" type="text/css" href="css/style.css" />
  	 <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
<title>Repository Restore</title>
</head>
<body>
	<c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
	<c:choose>
   		<c:when test="${isAdmin}">
		      <ul id="breadcrumb">
		        <li class="path">
		            <a href="Restore-utilities.jsp">Restore utility</a>
		        </li>
		      </ul>
	      	<br/>
	      		<c:if test="${errMessage!=null}">
	      			<table class="form" width="372px" style="color: red; font-weight: bold;">
	      				<tr>
	      					<td style="text-align:left;">${errMessage}</td>
	      				</tr>
	      			</table>
      			</c:if>
				<table class="form" width="372px">
					<tr>
						<td>Restore Option</td>
					</tr>
					<tr>
						<td><input type="radio" id="optFtp" name="backupOpt" value="F" checked="checked"> FTP</td>
						<td><input type="radio" id="optStrDev" name="backupOpt" value="S">Storage Device</td>
					</tr>
				</table>
				<div id="ftpCntr">
				<form action="RestoreUtil?action=ftp" method="post" >
					<table class="form" width="372px">
						<tr>
							<td width="80px;">ServerID:</td>
							<td><INPUT TYPE="TEXT" NAME="serverId" required autofocus oninvalid="setCustomValidity('Please enter your server Id.')"
						onchange="try{setCustomValidity('')}catch(e){}" ></td>
						</tr>
						<tr>
							<td>UserID:</td>
							<td><INPUT TYPE="TEXT" NAME="userName"  required></td>
						</tr>
						<tr>
							<td>Password:</td>
							<td><INPUT TYPE="password" NAME="password" required></td>
						</tr>
						<tr>
						<td width="80px;">&nbsp;</td>
						<td>
							<input type="submit" name="submit"/>
						</td>  
					</tr>
					</table>
				</form>
				</div>
				<div id="storageCntr" style="display: none;">
				<form action="RestoreUtil?action=strg" method="post" >
					<table class="form" width="372px">
						<tr>
							<td>Backup path</td>
							<td><INPUT type="text" NAME="storagePath" value="" required autofocus
								oninvalid="setCustomValidity('Please enter restore dest.')"
								onchange="try{setCustomValidity('')}catch(e){}"></td>
						</tr>
						<tr>
						<td width="80px;">&nbsp;</td>
						<td>
							<input type="submit" name="submit"/>
						</td>  
					</tr>
					</table>
				</form>
				</div>
	</c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
<script type="text/javascript">
    $(document).ready(function() {
		$("#optFtp").click(function(){
			$("#ftpCntr").show();
			$("#storageCntr").hide();
		});
		$("#optStrDev").click(function(){
			$("#storageCntr").show();
			$("#ftpCntr").hide();
		});
	});
</script>
</html>