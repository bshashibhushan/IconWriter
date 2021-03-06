<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ikon.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Utilities</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="utilities.jsp">Utilities</a>
        </li>
      </ul>
      <br/>
      <center>
        <a href="CheckEmail"><button class="utilButton">Check email</button></a>
        <a href="rebuild_indexes.jsp"><button class="utilButton">Rebuild indexes</button></a>
        <a href="repository_checker.jsp"><button class="utilButton">Repository checker</button></a>
        <a href="ListIndexes"><button class="utilButton">List indexes</button></a>
        <a href="CheckTextExtraction"><button class="utilButton">Check text extraction</button></a>
        <a href="Css"><button class="utilButton">CSS</button></a>
        <a href="stamp_image_list.jsp"><button class="utilButton">Configure Stamp</button></a>
      </center>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
