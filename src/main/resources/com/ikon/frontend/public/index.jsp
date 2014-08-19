<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ikon.core.HttpSessionManager" %>
<%@ page errorPage="/general-error.jsp" %>
<%
	HttpSessionManager.getInstance().add(request);
	com.ikon.api.OKMAuth.getInstance().login();
	
	Cookie cookie = new Cookie("ctx", request.getContextPath());
	cookie.setMaxAge(365 * 24 * 60 * 60); // One year
	response.addCookie(cookie);
%>
<jsp:include flush="true" page="index.html" />
