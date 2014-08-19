<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ikon.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
	<head>
		<title> Create Document Types </title>
		<link rel="stylesheet" type="text/css" href="css/style.css" />
		<link rel="stylesheet" type="text/css" href="css/colorpicker.css" />
		<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="js/fixedTableHeader.js"></script>
		<script src="../js/vanadium-min.js" type="text/javascript"></script>
		<script type="text/javascript">
		 $(document).ready(function() {
		    	$('form').bind('submit', function(event) {
		        	var error = $('input[name="usr_id"] + span.vanadium-invalid');
		    		
		    		if (error == null || error.text() == '') {
		        		return true;
		        	} else {
		        		return false;
		            }
			   	});
			});
				$(function() {
					$('#errorDName').hide();
					$('#errorIndex').hide();
					
					/* Add new input box */
					var addDiv = $('#addinput');
					var i = $('#addinput tr').size();
					$('#addNew').live('click', function() {
						$('<tr> <td> Index ' + (i) + ' : <input type="text" id="p_new" name="dtype_index_' + i +'" size="20" value="" />   \
							<select name="dtype_index_' + i +'"> <option value="text">String</option><option value="date">Date</option><option value="link">Link</option><option value="folder">Folder</option><option value="checkbox">Check Box</option> \
									<option value="textarea">Textarea</option></select>  \
							<a href="#" id="addNew" ><img src="img/action/new.png" alt="New Index" title="New Index"/> </a>  <a href="#" id="remNew"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a> </td></tr>').appendTo(addDiv);				
								
						i++;
						return false;
					});

					/* Delete input box */
					$('#remNew').live('click', function() {
						$(this).parents('tr').remove();
						i--;
						
						return false;
					});
					
					/* This is for onBlur of document type name */
					$('#d_name').blur(function(){
						var str = $('#d_name').val();
						if(/^[a-zA-Z0-9- ]*$/.test(str) == false || str == "") {
							$('#errorDName').show();
							$("#d_name").focus();
							return false;
						} else {
							$('#errorDName').hide("slow");
						}
					});
					
					/* for submit button, which verifies all fields */
					$('.validate').live('click', function(){
						$('#errorDName').hide();
						$('#errorIndex').hide();
						var str = $('#d_name').val();
						if(/^[a-zA-Z0-9- ]*$/.test(str) == false || str == "") {
							$('#errorDName').show();
							$("#d_name").focus();
							return false;
						} else {
							$('#errorDName').hide("slow");
						}					
						
						var k = $('#addinput tr').size();
						for (var j = 1; j < k; j++) {
							var str1 = $('table tr:nth-child(' + (j + 1) + ') td:nth-child(1) input').val();
							if(/^[a-zA-Z0-9- ]*$/.test(str1) == false || str1 == "") {
								$('#errorIndex').show();
								$('table tr:nth-child(' + (j + 1) + ') td:nth-child(1) input').focus();
								return false;
							} else {
								$('#errorIndex').hide();
							}
						}						
					});	
					
				});
				
		</script>
		<style>
			.form tr td b{
				font-size : 14px;	
			}
		</style>
	</head>
	<body>
	 <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
		<c:choose>
		<c:when test="${isAdmin}">
			<ul id="breadcrumb">
			 <li class="action">
          		<a href="PropertyGroups">Property groups</a>
        	</li>
			<li class="path">
				 <a href="#">Create Document Types</a>
			</li>
			</ul>
			<br>
			<br>
				<center>
				<form action="PropertyGroups?action=register" method="post">
					<table class="form" id="addinput" width="450px">	
						<tr>
							<td> <b>Name of the Document Type : </b> </td>
							<td> <input name="document_type_name" id="d_name" value="" class=":required :only_on_blur :ajax;PropertyGroups?action=validatepgName"/> </td>
						</tr>												
						<tr>
							<td> Index 1 : <input type="text" id="p_new" name="dtype_index_0" value="" class="p_new" /> 
								<select name="dtype_index_0">
									<option value="text">String</option>					
									<option value="date">Date</option>					
									<option value="link">Link</option>					
									<option value="folder">Folder</option>
									<option value="checkbox">Check Box</option>
									<option value="textarea">Textarea</option>
								</select>
								<a href="#" id="addNew" ><img src="img/action/new.png" alt="New Index" title="New Index"/> </a> 
							</td>					
						</tr>
					</table>
					<table class="form" width="450px">	
						<tr>
							<td colspan="2" align="right">
							  <input type="button" onclick="javascript:window.history.back()" value="Cancel"/>
							  <input type="submit" value="Send" class="validate"/>
							</td>
						</tr>
					</table>
					<div id="errorDName" style="color:red"><p>Document Name contains illegal characters or is empty.</p></div>
					<div id="errorIndex" style="color:red"><p>Index values contains illegal characters or is empty.</p></div>
				</form>	
				</center>
		 </c:when>
		 <c:otherwise>
			<div class="error"><h3>Only admin users allowed</h3></div>
		 </c:otherwise>
		</c:choose>

	</body>
</html>
