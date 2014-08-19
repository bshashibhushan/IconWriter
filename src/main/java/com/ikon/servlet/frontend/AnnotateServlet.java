package com.ikon.servlet.frontend;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import com.ikon.core.DatabaseException;
import com.ikon.dao.AnnotationDAO;
import com.ikon.dao.bean.Annotation;
import com.ikon.api.OKMDocument;
import com.ikon.api.OKMAuth;
import com.ikon.api.OKMNote;
import com.ikon.api.OKMNotification;
import java.util.*;

@SuppressWarnings("serial")
public class AnnotateServlet extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException	{
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException	{
		String action = request.getParameter("action");
		if(action.equals("getAnnotation")){
			applyAnnotations(request, response);				
		} else if(action.equals("saveAnnotation")) {
			saveAnnotations(request, response);
		}
			
	}
	    
	private void saveAnnotations(HttpServletRequest request, HttpServletResponse response) {
		
		try{
			   
		    String text = request.getParameter("marks");
			String uuid = request.getParameter("uuid");
			
			Annotation annotation = new Annotation();
			annotation.setUuid(uuid);
			annotation.setText(text);
			if(AnnotationDAO.getAnnotationDetails(uuid)==null){
				AnnotationDAO.saveAnnotation(annotation);
			} else if(AnnotationDAO.getAnnotationDetails(uuid)!=null){
				AnnotationDAO.updateAnnotation(annotation);
			}
		   
		    //for email notification
				   String doc = OKMDocument.getInstance().getPath(null, uuid);
					
				   List<String> userNames = new ArrayList<String>();
		     //for document users notification
				   Map<String, Integer> grantedUsers = OKMAuth.getInstance().getGrantedUsers(null, doc);
								for (Map.Entry<String, Integer> entry : grantedUsers.entrySet()) {								  
								    	userNames.add(entry.getKey());
								    }
			
								
		     //for folder users notification
					Map<String, Integer> grantedUsersFolder = OKMAuth.getInstance().getGrantedUsers(null, doc.substring(0, doc.lastIndexOf("/")));
								for (Map.Entry<String, Integer> entry : grantedUsersFolder.entrySet()) {
										userNames.add(entry.getKey());
								    }
					
								
			//add note
				OKMNote.getInstance().add(null, doc, "Annotated - ");
								
				String message = "Annotated";
				OKMNotification.getInstance().notify(null, doc, userNames, message, false);  
				
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void applyAnnotations(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uuid = request.getParameter("uuid");
		String marks = "";
		try {
			 Annotation annotation =  AnnotationDAO.getAnnotationDetails(uuid);
			 if(annotation!=null){
				 marks = annotation.getText();
				 request.setAttribute("uuid", uuid);
				 request.setAttribute("action", "annotate");
			 }
			 request.setAttribute("properties", marks);
			 request.getRequestDispatcher("index.jsp").forward(request, response);
			 
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
	}

}
