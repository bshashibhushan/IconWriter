<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ikon.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <title>Property Group</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="PropertyGroups">Property groups</a>
        </li>
        <li class="action">
          <a href="property_group_register.jsp">Register property groups</a>
        </li>
      </ul>
      <br/>
        <c:forEach var="pGroup" items="${pGroups}">
        <div style="display: inline-table; ">
          <table class="results" width="90%" style="float: left; margin:0 100px;">
            <tr><th colspan="4">${pGroup.key.label}
            	<a href="PropertyGroups?action=edit&label=${pGroup.key.label}"><img src="img/action/edit.png" alt="Edit Property Group" title="Edit Property Group"/> </a></th></tr>
            <tr><th colspan="2">Index Name</th><th colspan="2">Index Type</th></tr>
            <c:forEach var="pgForm" items="${pGroup.value}" varStatus="row">
            <c:set var="string" value="${pgForm.others}"></c:set>
				<c:set var="indexType" value="${fn:split(string, ' ')}"></c:set>
               <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
                <td colspan="2"><center>${pgForm.label}</center></td>
                <c:choose>
    				<c:when test="${not empty indexType[3]}">
       					<td colspan="2" style="text-transform: capitalize"><center>${indexType[3]}</center></td>
    				</c:when>
    				<c:otherwise>
    					<td colspan="2" style="text-transform: capitalize"><center>${pgForm.field}</center></td>
    				</c:otherwise>
				</c:choose>
              </tr>
            </c:forEach>
          </table>
          <br/>
          </div>
        </c:forEach>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
