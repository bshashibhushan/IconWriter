package com.ikon.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.ikon.frontend.client.service.OKMStaplingService;
import com.ikon.frontend.client.service.OKMStaplingServiceAsync;
import com.ikon.frontend.client.widget.Stapling;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.bean.GWTDocument;
import com.ikon.frontend.client.constants.service.RPCService;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.extension.comunicator.TabDocumentComunicator;

public class ExtendedUtils {

	 private final static OKMStaplingServiceAsync staplingService = (OKMStaplingServiceAsync)GWT.create(OKMStaplingService.class);

	/**
	 *Annotate document
	 */
	public static void annotateDocument(String uuid) {
			final Element downloadIframe = RootPanel.get("__download").getElement();
			
			//generate swf file
			String url = RPCService.ConverterServlet + "?inline=true&toSwf=true&uuid=" + uuid;
			DOM.setElementAttribute(downloadIframe, "src", url); 
			
			//take to annotate page
			String newUrl = Main.get().workspaceUserProperties.getWorkspace().getApplicationURL() + "?uuid=" + uuid;
			String newEditUrl = newUrl.replace("e/", "e/annotate/");
			Window.open(newEditUrl, null, null);
		}

	/**
	 * Staple Documents
	 */
	public static void stapleDocuments(final String groupId, String uuid1, final boolean search) {
		String groupID = "";
		String uuid = "";
		
		if(search)
		{
			groupID = groupId;
			uuid=uuid1;
		}
		else
		{
			groupID = Stapling.get().getGroupId();
			uuid = TabDocumentComunicator.getDocument().getUuid();
		}
		
		staplingService.add(groupID, uuid, "openkm:document", new AsyncCallback<String>() {
           	   public void onSuccess(String result)
               {
           		   if(!search)
           		   {
	           		   Main.get().mainPanel.desktop.browser.tabMultiple.enableTabDocument();
					   GWTDocument doc = TabDocumentComunicator.getDocument();
					   Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setProperties(doc);
					   Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(doc,Main.get().activeFolderTree.getFolder());
           		   }
           		   else
           		   {
           			   Window.alert("Added document to group!!");
           		   }
               }
                   public void onFailure(Throwable caught)
                   {
                     GeneralComunicator.showError("Choose a Staple Group", caught);
                   }
                 });	
			}
	
	/**
	 * 	Create New Staple Group
	 */
	public static void newStaple(String groupName) {
		String uuid = "";
		String type = "";
			uuid = TabDocumentComunicator.getDocument().getUuid();
			type = "openkm:document";
		staplingService.create(GeneralComunicator.getUser(), uuid, type, uuid, type, groupName, new AsyncCallback<String>() {
        	   public void onSuccess(String result)
               {
        		  
        		   Main.get().mainPanel.desktop.browser.tabMultiple.enableTabDocument();
        		   GWTDocument doc = TabDocumentComunicator.getDocument();
        		   Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setProperties(doc);
        		   Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(doc,Main.get().activeFolderTree.getFolder());               }

               public void onFailure(Throwable caught)
               {
                 GeneralComunicator.showError("create", caught);
               }
             });	
	}
}
