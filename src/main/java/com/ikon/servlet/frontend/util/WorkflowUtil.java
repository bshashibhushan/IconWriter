package com.ikon.servlet.frontend.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ikon.core.Config;
import com.ikon.principal.PrincipalAdapterException;
import com.websina.license.LicenseManager;

public class WorkflowUtil {
	

	static String password = null;

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
    InputStream is = new URL(url).openStream();
    try {
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      JSONObject json = new JSONObject(jsonText);
      return json;
    } finally {
      is.close();
    }
  }

  /**
   * get the workflow List
   * @return
   * @throws IOException
   * @throws JSONException
 * @throws PrincipalAdapterException 
   */
  public static Map<String, String> getWorkflowList() throws IOException, JSONException, PrincipalAdapterException{
	  
	  	password = null; // OKMAuth.getInstance().getWorkflowPassword("tAdmin");

	    JSONObject json = readJsonFromUrl(Config.WORKFLOW_URL + "/jw/web/json/workflow/process/list?j_username=admin&j_password=admin");
        JSONArray linkArray = json.getJSONArray("data");
	    
	    Map<String,String> workflowMap = new HashMap<String, String>();

	    int size = linkArray.length();
	    String key = null;
	    String value = null;
	    ArrayList<JSONObject> arrays = new ArrayList<JSONObject>();
	    for (int i = 0; i < size; i++) {
	        JSONObject another_json_object = linkArray.getJSONObject(i);
	            arrays.add(another_json_object);
	                        
	            key = ((String)arrays.get(i).get("packageName"));
	            value = (String)arrays.get(i).get("id");
	            	            
	            workflowMap.put(key, value);
	    }
	    
	    password = null;
	    return workflowMap;
	    }
  
  /**
   * Start the process
   * @param processId
   * @throws IOException
   * @throws JSONException
   * @throws PrincipalAdapterException 
   */
  public static void startWorkflow(String processId) throws IOException, JSONException, PrincipalAdapterException{
	  
	  processId = processId.replace("#", ":");
	  //readJsonFromUrl(Config.WORKFLOW_URL + "/jw/web/json/workflow/process/start/" + processId + "?j_username=admin&j_password=admin");  
	  String url=Config.WORKFLOW_URL+"/jw/web/json/workflow/process/start/" + processId + "?j_username=admin&j_password=admin";
	  InputStream is = new URL(url).openStream();

	  password = null;
  }
  
  /**
   * get User Task list
   * @param userId
   * @return taskList
   * @throws IOException
   * @throws JSONException
   * @throws PrincipalAdapterException
   */
  //this
  public static int getUserTaskLists(String userId) throws IOException, JSONException, PrincipalAdapterException{
	  
	  password = null; //OKMAuth.getInstance().getWorkflowPassword(userId);	 
	  int taskList = 0;
	  
	  JSONObject json = readJsonFromUrl(Config.WORKFLOW_URL + "/jw/web/json/workflow/assignment/list/pending/count?j_username=" + userId + "&j_password=" + userId); 
	  taskList = (Integer) json.get("total");
	  System.out.println("tasks pending---------->"+taskList);
	  password = null;
	  return taskList;
  }}
