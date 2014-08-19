<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.ikon.util.FormatUtil"%>
<html>
<script type="text/javascript">
	window.history.forward(0);
</script>
<body>

  <%-- <c:choose>
   <c:when test="${not empty DAYS_LEFT }"> --%>
	<jsp:include page="login_desktop.jsp">
		<jsp:param name="error" value="${param.error}" />
	</jsp:include>
   <%-- </c:when>
   <c:otherwise>
		<jsp:forward page="./validate" />
    </c:otherwise>
  </c:choose> --%>

</body>

</html>