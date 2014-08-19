package com.ikon.servlet.frontend.util;

import javax.ws.rs.client.ClientBuilder;

import com.ikon.core.Config;
import com.ikon.dao.bean.Role;
import com.ikon.dao.bean.User;

public class Workflow {
	
	public static void addUser(User user) {		
		ClientBuilder.newClient().target(Config.WORKFLOW_URL + "/jw/web/DMS/user/create/" + user.getId() + "/" + user.getId() + "/" + user.getEmail() + "/"  + user.getName() + "/"+ isWorkflowAdmin(user)).request().get();		
	}
	
	public static void editUser(User user) {		
		ClientBuilder.newClient().target(Config.WORKFLOW_URL + "/jw/web/DMS/user/edit/" + user.getId() + "/" + user.getId() + "/" + user.getEmail() + "/"  + user.getName() + "/"+ isWorkflowAdmin(user)).request().get();		
	}
	
	public static void deleteUser(String userId) {		
		ClientBuilder.newClient().target(Config.WORKFLOW_URL + "/jw/web/DMS/user/delete/" + userId).request().get();		
	}
	
	private static boolean isWorkflowAdmin(User user){
		boolean isWorkflowAdmin = false;
		for(Role rol : user.getRoles()){
			if(("ROLE_WORKFLOW_ADMIN").equals(rol.getId()))
				isWorkflowAdmin = true;
		}
			
		return isWorkflowAdmin;
	}


}
