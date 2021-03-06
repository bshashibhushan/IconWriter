<%@ page import="java.io.FileNotFoundException"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.File" %>
<%@ page import="com.ikon.core.Config" %>
<%@ page import="com.ikon.servlet.admin.BaseServlet" %>
<%@ page import="com.ikon.bean.ContentInfo" %>
<%@ page import="com.ikon.api.OKMFolder" %>
<%@ page import="com.ikon.util.WebUtils"%>
<%@ page import="com.ikon.util.FormatUtil" %>
<%@ page import="com.ikon.util.impexp.RepositoryExporter" %>
<%@ page import="com.ikon.util.impexp.HTMLInfoDecorator" %>
<%@ page import="com.ikon.util.impexp.ImpExpStats"%>
<%@ page import="com.ikon.bean.Repository"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script type="text/javascript" src="../js/jquery-1.7.1.min.js"></script>
  <script type="text/javascript" src="js/jquery.DOMWindow.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
		$dm = $('.ds').openDOMWindow({
			height:200, width:300,
			eventType:'click',
			overlayOpacity:'57',
			windowSource:'iframe', windowPadding:0
		});
	});
    
    function dialogClose() {
		$dm.closeDOMWindow();
    }
  </script>
  <title>Repository Export</title>
</head>
<body>
<%
	if (BaseServlet.isAdmin(request)) {
		request.setCharacterEncoding("UTF-8");
		String repoPath = WebUtils.getString(request, "repoPath", "/" + Repository.ROOT);
		String fsPath = WebUtils.getString(request, "fsPath");
		String metadata = WebUtils.getString(request, "metadata");
		boolean history = WebUtils.getBoolean(request, "history");
		
		out.println("<ul id=\"breadcrumb\">");
		out.println("  <li class=\"path\"><a href=\"repository_export.jsp\">Repository export</a></li>");
		out.println("  <li class=\"action\"><a href=\"backup_utilities.jsp\">Repository Backup</a></li>");
		out.println("</ul>");
		out.println("<br/>");
		out.println("<form action=\"repository_export.jsp\">");
		out.println("<table class=\"form\" align=\"center\">");
		out.println("<tr>");
		out.println("<td>Repository path</td>");
		out.println("<td><input type=\"text\" size=\"50\" name=\"repoPath\" id=\"repoPath\" value=\"" + repoPath + "\" ></td>");
		out.println("<td><a class=\"ds\" href=\"../extension/DataBrowser?action=repo&sel=fld&dst=repoPath&path=" + repoPath +"\"><img src=\"img/action/browse_repo.png\"/></a></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>Filesystem path</td>");
		out.println("<td><input type=\"text\" size=\"50\" name=\"fsPath\" id=\"fsPath\" value=\"" + fsPath + "\" ></td>");
		out.println("<td><a class=\"ds\" href=\"../extension/DataBrowser?action=fs&sel=fld&dst=fsPath&root=" + Config.INSTANCE_CHROOT_PATH + "&path=" + fsPath + "\"><img src=\"img/action/browse_fs.png\"/></a></td>");
		out.println("</tr>");
		out.println("<tr><td>Metadata</td><td><select name='metadata'><option value='none'>None</option><option value='XML'>XML</option><option value='JSON'>JSON</option></td></tr>");
		out.println("<tr><td>History</td><td><input type=\"checkbox\" name=\"history\" " + (history?"checked":"") + "/></td></tr>");
		out.println("<tr><td colspan=\"3\" align=\"right\">");
		out.println("<input type=\"submit\" value=\"Send\">");
		out.println("</td></tr>");
		out.println("</table>");
		out.println("</form>");

		try {
			if (repoPath != null && !repoPath.equals("") && fsPath != null && !fsPath.equals("")) {
				out.println("<hr/>");
				
				if (fsPath.startsWith(Config.INSTANCE_CHROOT_PATH)) {
					File dir = new File(fsPath);
					ContentInfo cInfo = OKMFolder.getInstance().getContentInfo(null, repoPath);
					out.println("<b>Files & directories to export:</b> "+(cInfo.getDocuments() + cInfo.getFolders())+"<br/>");
					long begin = System.currentTimeMillis();
					ImpExpStats stats = RepositoryExporter.exportDocuments(null, repoPath, dir, metadata, history, out,
						new HTMLInfoDecorator((int) cInfo.getDocuments() + (int) cInfo.getFolders()));
					long end = System.currentTimeMillis();
					out.println("<hr/>");
					out.println("<div class=\"ok\">Folder '"+repoPath+"' exported to '"+new File(fsPath).getAbsolutePath()+"'</div>");
					out.println("<br/>");
					out.println("<b>Documents:</b> "+stats.getDocuments()+"<br/>");
					out.println("<b>Folders:</b> "+stats.getFolders()+"<br/>");
					out.println("<b>Mails:</b> "+stats.getMails()+"<br/>");
					out.println("<b>Size:</b> "+FormatUtil.formatSize(stats.getSize())+"<br/>");
					out.println("<b>Time:</b> "+FormatUtil.formatSeconds(end - begin)+"<br/>");
				} else {
					out.println("<div class=\"error\">Path out of root: "+Config.INSTANCE_CHROOT_PATH+"<div>");
				}
			}
		} catch (FileNotFoundException e) {
			out.println("<div class=\"error\">File Not Found: "+e.getMessage()+"<div>");
		} catch (IOException e) {
			out.println("<div class=\"error\">IO Error: "+e.getMessage()+"<div>");
		} catch (Exception e) {
			out.println("<div class=\"error\">Error: "+e.getMessage()+"<div>");
		}
	} else {
		out.println("<div class=\"error\"><h3>Only admin users allowed</h3></div>");
	}
%>
</body>
</html>
