<%@page import="com.ikon.util.FormatUtil"%>
<html>

<head>
<link rel="Shortcut icon"
	href="<%=request.getContextPath()%>/favicon.ico" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/style.css" type="text/css" />
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/css/desktop.css" type="text/css" />

<title>Infodocs Info</title>
</head>

<body>
	<div id="box" align="center">
		<div id="logo"></div>
			<h3 style="margin-right: 20%; color: red;">${requestScope.ERROR}</h3><br />
			<h3 style="color: red;">${requestScope.SUPPORT }</h3>
			<h4 style="color: red;">${requestScope.MAX_USERS }</h4>
			<h4 style="color: red;">${requestScope.NO_LICENSE }</h4>
	</div>
</body>

</html>