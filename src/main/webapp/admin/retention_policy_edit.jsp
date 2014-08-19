<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.ikon.servlet.admin.BaseServlet" %>
<%@ page import="com.ikon.core.Config"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script src="../js/jquery-1.7.1.min.js" type="text/javascript"></script>
  <script src="../js/vanadium-min.js" type="text/javascript"></script>
  <script type="text/javascript" src="../js/jquery.DOMWindow.js"></script>
  <script type="text/javascript">
	$(document).ready(function() {
		$dm = $('.ds').openDOMWindow({
			height : 200,
			width : 300,
			eventType : 'click',
			overlayOpacity : '57',
			windowSource : 'iframe',
			windowPadding : 0
		});
	});

	function dialogClose() {
		$dm.closeDOMWindow();
	}
	</script>
  <title>Retention Policies</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <ul id="breadcrumb">
		<li class="path">
			<a href="RetentionPolicy"> Retention Policy </a>
		</li>
        <li class="path">
          <c:choose>
            <c:when test="${action == 'create'}">Create Retention Policy</c:when>
            <c:when test="${action == 'edit'}">Edit Retention Policy</c:when>
            <c:when test="${action == 'delete'}">Delete Retention Policy</c:when>
          </c:choose>
        </li>
      </ul>
      <br/>
      <form action="RetentionPolicy" method="post">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="policy_nodeUuid" value="${policy.nodeUuid}"/>
        <table class="form" width="425px">
          <tr>
            <td nowrap="nowrap">Source Path</td>
            <td><input size="30" class=":required :only_on_blur" name="policy_sourcePath" 
						id="policy_sourcePath" value="${policy.sourcePath}" readonly="readonly"/></td>
            <c:choose>
				<c:when test="${action == 'create'}">
					<td>
						<a class="ds" href="../extension/DataBrowser?action=repo&sel=fld&dst=policy_sourcePath&root=<%=Config.INSTANCE_CHROOT_PATH%>">
							<img src="img/action/browse_repo.png" />
						</a>
					</td>
				</c:when>
			</c:choose>
          </tr>
          <tr>
            <td nowrap="nowrap">Destination Folder</td>
            <td><input size="30" class=":required :only_on_blur" name="policy_destinationPath" 
					id="policy_destinationPath" value="${policy.destinationPath}" readonly="readonly"/></td>
            <td>
				<a class="ds" href="../extension/DataBrowser?action=repo&sel=fld&dst=policy_destinationPath&root=<%=Config.INSTANCE_CHROOT_PATH%>">
					<img src="img/action/browse_repo.png" />
				</a>
			</td>
          </tr>
          <tr>
            <td nowrap="nowrap">Retention Days</td>
            <td><input type="number" min="1" 
				class=":required :only_on_blur" name="policy_retentionDays" value="${policy.retentionDays}"
				oninvalid="setCustomValidity('Please enter valid retention days.')"
						onchange="try{setCustomValidity('')}catch(e){}"/></td>
          </tr>
          <tr>
            <td nowrap="nowrap">Email to be notified</td>
            <td><input name="policy_emailList" value="${policy.emailList}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${policy.active}">
                  <input name="policy_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="policy_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
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
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
