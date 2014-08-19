<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ikon.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Property Group Edit</title>
		<link rel="stylesheet" type="text/css" href="css/style.css" />
		<link rel="stylesheet" type="text/css" href="css/colorpicker.css" />
		<script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="js/fixedTableHeader.js"></script>
		<script type="text/javascript">
				$(function() {
					$('#errorIndex').hide();

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

					$('#remNew').live('click', function() {
						if (confirm('All values mapped to this field will be erased. Continue?')) {
							$(this).parents('tr').remove();
							i--;
							
							return false;
						} else {
							return false;
						}
					});
					
					$('#deleteProperty').live('click', function() {
						if (confirm('Do you want to delete this property Group?')) {
					
						} else {
							return false;
						}
					});
					
					/* for submit button, which verifies all fields */
					$('.validate').live('click', function(){					
						
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
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
        <li class="path">
          <a href="PropertyGroups">Property groups</a>
        </li>
        <li class="path">Edit</li>
      </ul>
      <br/>
      <form action="PropertyGroups?action=edit"  method="post">
			<table class="form" id="addinput" width="450px" > 
					<tr>
						<td style="font-size:12px"> <b>Name of the Document Type : </b> </td>
						<td style="font-size:12px;"> <b> ${pgLabel} <input name="document_type_name" id="d_name" type="hidden" value="${pgLabel}"/> </b> 
								<a href="PropertyGroups?action=delete&label=${pgLabel}" id="deleteProperty" ><img src="img/action/delete.png" alt="Delete" title="Delete Property Group"/> </a> </td>
					</tr>
				<c:forEach var="property" items="${propertyMap}" varStatus="loopCount">
				<c:set var="string" value="${property.others}"></c:set>
				<c:set var="indexType" value="${fn:split(string, ' ')}"></c:set>	
				<c:set var="indexTypeOther" value="${property.field}"></c:set>			
					<tr>
						<td> Index ${loopCount.index} : <input type="text" id="p_new" name="dtype_index_${loopCount.index}"  value="${property.label}" class="p_new" /> 
							<select name="dtype_index_${loopCount.index}" >
								<option value="text" ${indexType[3] == 'text' ? 'selected' : ''}>String</option>					
								<option value="date" ${indexType[3] == 'date' ? 'selected' : ''}>Date</option>					
								<option value="link" ${indexType[3] == 'link' ? 'selected' : ''}>Link</option>					
								<option value="folder" ${indexType[3] == 'folder' ? 'selected' : ''}>Folder</option>
								<option value="checkbox" ${indexTypeOther == 'CheckBox' ? 'selected' : ''}>Check Box</option>
								<option value="textarea" ${indexTypeOther == 'TextArea' ? 'selected' : ''}>Textarea</option>
							</select>
							<a href="#" id="addNew" ><img src="img/action/new.png" alt="New Index" title="New Index"/> </a> 
							<a href="#" id="remNew"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
						</td>					
					</tr>
				</c:forEach>
			</table>
			<table class="form" width="450px">	
				<tr>
					<td colspan="2" align="right">
						<input type="button" onclick="javascript:window.history.back()" value="Cancel"/>
						<input type="submit" value="Send" class="validate"/>
					</td>
				</tr>
			</table>
			<center><div id="errorIndex" style="color:red"><p>Index values contains illegal characters or is empty.</p></div></center>
	  </form>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
