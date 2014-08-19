package com.ikon.servlet.admin;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ikon.util.backup.RestoreUtilityService;

public class RestoreUtilServlet extends BaseServlet {
	private static final long serialVersionUID = -402363273111096950L;

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String method = request.getMethod();
		if (isAdmin(request)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		RestoreUtilityService restoreUtilityService = new RestoreUtilityService();
		ServletContext sc = getServletContext();
		String action = request.getParameter("action");

		if ("ftp".equals(action)) {
			String serverId = request.getParameter("serverId");
			String userName = request.getParameter("userName");
			String password = request.getParameter("password");
			try {
				restoreUtilityService.restoreftp(serverId, userName, password);

			} catch (Exception e) {
				request.setAttribute("errMessage", e.getMessage());
				sc.getRequestDispatcher("/admin/restore_utilities.jsp")
						.forward(request, response);
			}
			request.setAttribute("successMessage", "Restore is Done");
			sc.getRequestDispatcher("/admin/success_message.jsp").forward(
					request, response);

		} else if ("strg".equals(action)) {
			String storagePath = request.getParameter("storagePath");
			try {
				restoreUtilityService.restoreStorage(storagePath);
			} catch (Exception e) {
				request.setAttribute("errMessage", e.getMessage());
				sc.getRequestDispatcher("/admin/restore_utilities.jsp")
						.forward(request, response);
			}
			request.setAttribute("successMessage", "Restore is Done");
			sc.getRequestDispatcher("/admin/success_message.jsp").forward(
					request, response);

		}

	}

}
