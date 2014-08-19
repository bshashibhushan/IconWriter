<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script type="text/javascript">
    function validate(){
    	var excelFile = document.getElementById("file");
        if(excelFile.value == "" || !excelFile.value.contains(".xls")) {
            alert("Please upload valid excel file!");
            return false;
       }  
    }
</script>
  <title>Excel Import</title>
</head>
<body>

	<ul id="breadcrumb">
		<li  class="action"><a href="repository_import.jsp">Repository import</a></li>
		<li  class="path"><a href="repository_excel_import.jsp">Excel import</a></li>
	</ul>
	<br/>
	
	<form action="excelImport" method="post" enctype="multipart/form-data">
		<table class="form">
			<tbody>	
			<th align="center">Import Documents from Excel</th>
			<tr>
				<td colspan="2"><input type="file" name="file" id='file' size="50"/></td>
			</tr>
			<tr>
				<td colspan="4" align="right">
					<input type="submit" value="send"  onclick="return validate();"/>
				</td>
			</tr>
			</tbody>
		</table> 
	</form>
	<div id="line"/>

	<c:if test="${not empty ERROR }">
		<script type="text/javascript">
			alert("${ERROR}");
		</script>
	</c:if>

</body>
</html>
