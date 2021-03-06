/**
 *  openkm, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2013  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ikon.frontend.client.widget.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ikon.frontend.client.widget.Stapling;
import com.ikon.frontend.client.widget.properties.TabDocumentStapling;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.bean.GWTDocument;
import com.ikon.frontend.client.bean.GWTFolder;
import com.ikon.frontend.client.bean.GWTPermission;
import com.ikon.frontend.client.bean.GWTPropertyGroup;
import com.ikon.frontend.client.constants.service.RPCService;
import com.ikon.frontend.client.extension.event.HasDocumentEvent;
import com.ikon.frontend.client.extension.event.handler.DocumentHandlerExtension;
import com.ikon.frontend.client.extension.event.handler.PropertyGroupHandlerExtension;
import com.ikon.frontend.client.extension.event.hashandler.HasDocumentHandlerExtension;
import com.ikon.frontend.client.extension.event.hashandler.HasPropertyGroupHandlerExtension;
import com.ikon.frontend.client.extension.widget.tabdocument.TabDocumentExtension;
import com.ikon.frontend.client.service.OKMPropertyGroupService;
import com.ikon.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.ikon.frontend.client.util.Util;

/**
 * The tab document
 * 
 * @author jllort
 *
 */
public class TabDocument extends Composite implements HasDocumentEvent, HasDocumentHandlerExtension, HasPropertyGroupHandlerExtension {
	
	private final OKMPropertyGroupServiceAsync propertyGroupService = (OKMPropertyGroupServiceAsync) GWT.create(OKMPropertyGroupService.class);
	
	private static final int TAB_HEIGHT = 20;
	public int PREVIEW_TAB = -1;
	private int SECURITY_TAB = -1;
	
	public TabLayoutPanel tabPanel;
	public Document document;
	public VersionScrollTable version;
	public SecurityScrollTable security;
	private VerticalPanel panel;
	public TabDocumentStapling stapling;
	public Stapling staple;
	private List<PropertyGroup> propertyGroup;
	private GWTDocument doc;
	public Notes notes;
	private Preview preview;
	private int selectedTab = 0; // Used to determine selected tab to mantain on change document, because not all documents
								 // have the same number of tabs ( document group properties are variable )
	private int latestSelectedTab = 0;
	private boolean visibleButton = true; // Sets visibleButtons enabled to default view 
	private List<TabDocumentExtension> widgetExtensionList;
	private List<DocumentHandlerExtension> docHandlerExtensionList;
	private int height = 0;
	private int width = 0;
	private boolean documentVisible = false;
	private boolean notesVisible = false;
	private boolean versionVisible = false;
	private boolean securityVisible = false;
	private boolean previewVisible = false;
	private boolean propertyGroupsVisible = false;
	private boolean stapleVisible = false;
	private List<PropertyGroupHandlerExtension> propertyGroupHandlerExtensionList;
	private int IEBugCorrections = 0;
	
	/**
	 * The Document tab
	 */
	public TabDocument(){
		doc = new GWTDocument();
		propertyGroupHandlerExtensionList = new ArrayList<PropertyGroupHandlerExtension>();
		tabPanel = new TabLayoutPanel(TAB_HEIGHT, Unit.PX);
		document = new Document();
		notes = new Notes(Notes.DOCUMENT_NOTE);
		version = new VersionScrollTable();
		security = new SecurityScrollTable();
		preview = new Preview();
		panel = new VerticalPanel();
		new Stapling();
		staple = Stapling.get();
		stapling = new TabDocumentStapling();
		propertyGroup = new ArrayList<PropertyGroup>();
		widgetExtensionList = new ArrayList<TabDocumentExtension>();
		docHandlerExtensionList = new ArrayList<DocumentHandlerExtension>();

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				final int tabIndex = event.getSelectedItem().intValue();
				Main.get().mainPanel.topPanel.toolBar.evaluateRemovePropertyGroup(isRemovePropertyGroupEnabled(tabIndex));
				selectedTab = tabIndex;
				if (tabIndex==SECURITY_TAB) {
					Timer timer = new Timer() {
						@Override
						public void run() {
							security.fillWidth(); // Always when shows fires fill width
						}
					};
					timer.schedule(50); // Fill width must be done after really it'll be visible
				}
				preview.cleanPreview(); // Always clean preview tab
				if (tabIndex == PREVIEW_TAB) {
					Timer previewTimer = new Timer() {
						@Override
						public void run() {
							previewDocument(false);
						}
					};
					previewTimer.schedule(500);
				}
				fireEvent(HasDocumentEvent.TAB_CHANGED);
			}
		});
		
		panel.add(tabPanel);
		tabPanel.setWidth("100%");
		document.setSize("100%", "100%");
		notes.setSize("100%", "100%");
		panel.setSize("100%", "100%");
		
		tabPanel.setStyleName("okm-DisableSelect");
		
		initWidget(panel);
	}
	
	/**
	 * Sets the size
	 * 
	 * @param width With of the widget
	 * @param height Height of the widget
	 */
	public void setPixelSize(int width, int height) {
		//do not set if width and height are not changed
		if( this.height == height && this.width == width )
			return;
		
		this.height = height;
		this.width = width;
		tabPanel.setPixelSize(width, height);
		document.setPixelSize(width,height-TAB_HEIGHT); // Substract tab height
		notes.setPixelSize(width,height-TAB_HEIGHT); // Substract tab height
		version.setPixelSize(width,height-TAB_HEIGHT); // Substract tab height
		version.fillWidth();
		security.setPixelSize(width,height-TAB_HEIGHT); // Substract tab height
		security.fillWidth();
		stapling.setPixelSize(width,height-TAB_HEIGHT);
		
		// Setting size to extension
		for (Iterator<TabDocumentExtension> it = widgetExtensionList.iterator(); it.hasNext();) {
			it.next().setPixelSize(width,height-TAB_HEIGHT); // Substract tab height
		}
		
		if (!propertyGroup.isEmpty()) {			 // Sets size to propety groups	
			for (Iterator<PropertyGroup> it = propertyGroup.iterator(); it.hasNext();){
				PropertyGroup group =  it.next();
				group.setPixelSize(width,height-TAB_HEIGHT);
			}
		}

		preview.setPixelSize(width,height-TAB_HEIGHT); // Substract tab height
		if (selectedTab == PREVIEW_TAB) {
			previewDocument(true);
		}
		
		fireEvent(HasDocumentEvent.PANEL_RESIZED);
	}
	
	/**
	 * Sets document values
	 * 
	 * @param doc The document object
	 */
	public void setProperties(GWTDocument doc) {	
		// We must declare status here due pending downloading ( fired by status )
		if (securityVisible) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setUserSecurity();
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setRoleSecurity();
		}
		if (versionVisible) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setVersionHistory();
		}
		if (propertyGroupsVisible) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setGroupProperties();
		}
		
		this.doc = doc;
		selectedTab = tabPanel.getSelectedIndex(); 	// Sets the actual selected Tab
		latestSelectedTab = selectedTab; 			// stores latest selected tab
		
		document.set(doc); // Used by TabDocumentCommunicator
		notes.set(doc);	   // Used by TabDocumentCommunicator
		
		staple.refresh(stapling.getTable(), doc.getUuid());// Used by TabDocumentCommunicator
		
		if (versionVisible) {
			version.set(doc);
			version.getVersionHistory();
		}
		if (securityVisible) {
			security.setPath(doc.getPath());
			security.GetGrants();
			
			GWTFolder parentFolder = Main.get().activeFolderTree.getFolder();
			if ((parentFolder.getPermissions() & GWTPermission.SECURITY) == GWTPermission.SECURITY &&
				(doc.getPermissions() & GWTPermission.SECURITY) == GWTPermission.SECURITY && 
				!doc.isCheckedOut() && !doc.isLocked()) {
				security.setChangePermision(true);
			} else {
				security.setChangePermision(false);
			}
		}
		if (previewVisible) {
			preview.setPreviewAvailable(doc.isConvertibleToSwf());
		}
		
		if (!propertyGroup.isEmpty()) {
			for (Iterator<PropertyGroup> it = propertyGroup.iterator(); it.hasNext();){
				tabPanel.remove(it.next());
			}
			propertyGroup.clear();
		}
		
		// Only gets groups if really are visible
		if (propertyGroupsVisible) {
			getGroups(doc.getPath()); // Gets all the property group assigned to a document
									  // Here evalutates selectedTab
		}
		
		// Refresh preview if tab is visible
		if (selectedTab == PREVIEW_TAB) {
			previewDocument(false);
		}
		
		// TODO:Solves minor bug with IE ( now shows contents )
		if (Util.getUserAgent().startsWith("ie") && IEBugCorrections==1) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					correctIEDefect();
				}
			};
			timer.schedule(500);
		}
		
		fireEvent(HasDocumentEvent.DOCUMENT_CHANGED);
	}
	
	/**
	 * correctIEDefect
	 */
	public void correctIEDefect() {
		IEBugCorrections++;
		if (tabPanel.getWidgetCount()>1) {
			tabPanel.selectTab(1);
			tabPanel.selectTab(0);
		} else if (tabPanel.getWidgetCount()>1) {
			tabPanel.selectTab(0);
		}
	}
	
	/**
	 * refreshPreviewDocument
	 * 
	 * Used by extended previewers
	 */
	public void refreshPreviewDocument() {
		if (selectedTab == PREVIEW_TAB) {
			previewDocument(false);
		}
	}
	
	/**
	 * Refresh security values
	 */
	public void securityRefresh() {
		fireEvent(HasDocumentEvent.SECURITY_CHANGED);
		Main.get().mainPanel.desktop.browser.fileBrowser.securityRefresh();
	}
	
	/**
	 * isWidgetExtensionVisible
	 * 
	 * @param widget
	 * @return
	 */
	public boolean isWidgetExtensionVisible(Widget widget) {
		if (tabPanel.getWidget(selectedTab).equals(widget)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		selectedTab = tabPanel.getSelectedIndex();
		
		while (tabPanel.getWidgetCount() > 0) {
			tabPanel.remove(0);
		}
		
		if (documentVisible) {
			tabPanel.add(document, Main.i18n("tab.document.properties"));
			document.langRefresh();
		}
		if(stapleVisible) {
			tabPanel.add(stapling, Main.i18n("Related Documents"));
			stapling.langRefresh();
		}
		if (notesVisible) {
			tabPanel.add(notes, Main.i18n("tab.document.notes"));
			notes.langRefresh();
		}
		if (versionVisible) {
			tabPanel.add(version, Main.i18n("tab.document.history"));
			version.langRefresh();
		}
		if (securityVisible) {
			tabPanel.add(security, Main.i18n("tab.document.security"));
			security.langRefresh();
		}
		if (previewVisible) {
			tabPanel.add(preview, Main.i18n("tab.document.preview"));
			preview.langRefresh();
		}
		
		for (Iterator<TabDocumentExtension> it = widgetExtensionList.iterator(); it.hasNext();) {
			TabDocumentExtension extension = it.next();
			tabPanel.add(extension, extension.getTabText());
		}
		
		// Refresh lang property group
		if (!propertyGroup.isEmpty()) {
			for (Iterator<PropertyGroup> it = propertyGroup.iterator(); it.hasNext();){
				PropertyGroup group = it.next();
				tabPanel.add(group, group.getGrpLabel());
				group.langRefresh();
			}
		}		

		tabPanel.selectTab(selectedTab);
		
		resizingIncubatorWidgets();
	}
	
	/**
	 * Sets visibility to buttons ( true / false )
	 * 
	 * @param visible The visible value
	 */
	public void setVisibleButtons(boolean visible){
		this.visibleButton = visible;  // Save to be used by property group
		document.setVisibleButtons(visible);
		notes.setVisibleButtons(visible);
		version.setVisibleButtons(visible);
		security.setVisibleButtons(visible);
		stapling.setVisible(visible);
		
		fireEvent(HasDocumentEvent.SET_VISIBLE_BUTTONS);
	}
	
	/**
	 * isVisibleButton
	 * 
	 * @return
	 */
	public boolean isVisibleButton() {
		return visibleButton;
	}
	
	/**
	 * Gets asynchronous to get all groups assigned to a document
	 */
	final AsyncCallback<List<GWTPropertyGroup>> callbackGetGroups = new AsyncCallback<List<GWTPropertyGroup>>() {
		public void onSuccess(List<GWTPropertyGroup> result){
			GWTFolder gwtFolder = Main.get().activeFolderTree.getFolder();
			
			for (GWTPropertyGroup gwtGroup : result) {
				String groupTranslation = gwtGroup.getLabel();
				PropertyGroup group = new PropertyGroup(gwtGroup, doc, gwtFolder, visibleButton, gwtGroup.isReadonly());
				tabPanel.add(group, groupTranslation);
				propertyGroup.add(group);
				
				// Adds property group handlers
				for (Iterator<PropertyGroupHandlerExtension> itx = propertyGroupHandlerExtensionList.iterator(); itx.hasNext();) {
					group.addPropertyGroupHandlerExtension(itx.next());
				}
			}
			
			// To prevent change on document that has minor tabs than previous the new selected tab it'll be the max - 1 on that cases
			if (tabPanel.getWidgetCount()-1<latestSelectedTab) {
				tabPanel.selectTab(tabPanel.getWidgetCount()-1);
			} else {
				tabPanel.selectTab(latestSelectedTab); 	// Always enable selected tab because on document change tab group are removed
												 		// and on remove loses selectedTab
			}
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetGroupProperties();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetGroupProperties();
			Main.get().showError("GetGroups", caught);
		}
	};
	
	/**
	 * Gets all property groups assigned to document
	 */
	private void getGroups(String docPath) {
		Main.get().mainPanel.desktop.browser.tabMultiple.status.setGroupProperties();
		propertyGroupService.getGroups(docPath, callbackGetGroups);
	}
	
	/**
	 * Removes the actual property group
	 */
	public void removePropertyGroup(){
		selectedTab = tabPanel.getSelectedIndex(); // Sets the actual selectedted Tab
		
		// Removes group 
		PropertyGroup group = (PropertyGroup) tabPanel.getWidget(selectedTab);
		group.removeGroup();
		propertyGroup.remove(group);
		
		// Remove tab
		tabPanel.remove(selectedTab);
		
		// If removed tab is last the new selected tab is selectedTab -1
		if (tabPanel.getWidgetCount()-1<selectedTab) {
			selectedTab--;
		}
		
		// Sets the new selected tab
		tabPanel.selectTab(selectedTab);
		
	}
	
	/**
	 * Return if actual tab selected is group property and can be removed
	 * 
	 * @return
	 */
	private boolean isRemovePropertyGroupEnabled(int tabIndex) {
		if ((tabPanel.getWidget(tabIndex) instanceof PropertyGroup)) {
			return ((PropertyGroup) (tabPanel.getWidget(tabIndex))).isRemovePropertyGroupEnabled();
		} else {
			return false;
		}
	}
	
	/**
	 * resizingIncubatorWidgets 
	 * 
	 * Needs resizing if not widgets disapears
	 */
	public void resizingIncubatorWidgets() {
		if (!propertyGroup.isEmpty()) {
			for (Iterator<PropertyGroup> it = propertyGroup.iterator(); it.hasNext();){
				PropertyGroup group = it.next();
				group.setPixelSize(getOffsetWidth(), getOffsetHeight()-TAB_HEIGHT); // Substract tab height
			}
		}	
		version.setPixelSize(getOffsetWidth(), getOffsetHeight()-TAB_HEIGHT); // Substract tab height
		security.setPixelSize(getOffsetWidth(), getOffsetHeight()-TAB_HEIGHT); // Substract tab height
		version.fillWidth();
		security.fillWidth();
		// TODO:Solves minor bug with IE
		if (Util.getUserAgent().startsWith("ie")) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					tabPanel.setWidth(""+width);
					tabPanel.setWidth(""+(width+1));
					Timer timer = new Timer() {
						@Override
						public void run() {
							tabPanel.setWidth(""+width);
						}
					};
					timer.schedule(50);
				}
			};
			timer.schedule(100);
		}
	}
	
	/**
	 * getSelectedTab
	 * 
	 * @return
	 */
	public int getSelectedTab() {
		return selectedTab;
	}
	
	/**
	 * getDocument
	 * 
	 * @return
	 */
	public GWTDocument getDocument() {
		return doc;
	}
	
	public void showDocument() {
		tabPanel.add(document, Main.i18n("tab.document.properties"));
		documentVisible = true;
	}
	
	public void showStaple() {
		tabPanel.add(stapling, Main.i18n("Related Documents"));
		stapleVisible = true;
	}
	
	/**
	 * showNotes
	 */
	public void showNotes() {
		tabPanel.add(notes, Main.i18n("tab.document.notes"));
		notesVisible = true;
	}
	
	/**
	 * showVersion
	 */
	public void showVersion() {
		tabPanel.add(version, Main.i18n("tab.document.history"));
		versionVisible = true;
	}
	
	/**
	 * showSecurity
	 */
	public void showSecurity() {
		tabPanel.add(security, Main.i18n("tab.document.security"));
		securityVisible = true;
		SECURITY_TAB = tabPanel.getWidgetCount()-1; // starts at 0
	}
	
	/**
	 * showPreview
	 */
	public void showPreview() {
		tabPanel.add(preview, Main.i18n("tab.document.preview"));
		previewVisible = true;
		PREVIEW_TAB = tabPanel.getWidgetCount()-1; // starts at 0
	}
	
	/**
	 * showPropertyGroups
	 */
	public void showPropertyGroups() {
		propertyGroupsVisible = true;
	}
	
	/**
	 * showExtensions
	 */
	public void showExtensions() {
		for (TabDocumentExtension extension : widgetExtensionList) {
			tabPanel.add(extension, extension.getTabText());
			extension.setPixelSize(width, height-TAB_HEIGHT); // Substract tab height
		}
	}
	
	/**
	 * setKeywordEnabled
	 * 
	 * @param enabled
	 */
	public void setKeywordEnabled(boolean enabled) {
		document.setKeywordEnabled(enabled);
	}
	
	/**
	 * previewDocument
	 */
	private void previewDocument(boolean refreshing) {
		if (doc.getMimeType().equals("video/x-flv") || doc.getMimeType().equals("video/mp4") || 
				doc.getMimeType().equals("application/x-shockwave-flash") ||  
				doc.getMimeType().equals("audio/mpeg")) {
			if (!refreshing) {
					preview.showMediaFile(RPCService.DownloadServlet +"?uuid=" + URL.encodeQueryString(getDocument().getUuid()), getDocument().getMimeType());
			} else {
				preview.resizeMediaFile(width, height-TAB_HEIGHT);
			}
		} else {
			if(!refreshing) {
				preview.showEmbedSWF(doc.getUuid());
			} else {
				preview.resizeEmbedSWF(width, height-TAB_HEIGHT);
			}
		}
	}
	
	/**
	 * init
	 */
	public void init() {
		if (tabPanel.getWidgetCount()>0) {
			tabPanel.selectTab(0);
			
			if (securityVisible && doc!=null) {
				security.setPath(doc.getPath());
				security.GetGrants();
				
				GWTFolder parentFolder = Main.get().activeFolderTree.getFolder();
				if ((parentFolder.getPermissions() & GWTPermission.SECURITY) == GWTPermission.SECURITY &&
					(doc.getPermissions() & GWTPermission.SECURITY) == GWTPermission.SECURITY && 
					!doc.isCheckedOut() && !doc.isLocked()) {
					security.setChangePermision(true);
				} else {
					security.setChangePermision(false);
				}
			}
		}
	}
	
	/**
	 * addDocumentExtension
	 * 
	 * @param extension
	 */
	public void addDocumentExtension(TabDocumentExtension extension) {
		widgetExtensionList.add(extension);
	}
	
	@Override
	public void addDocumentHandlerExtension(DocumentHandlerExtension handlerExtension) {
		docHandlerExtensionList.add(handlerExtension);
	}

	@Override
	public void fireEvent(DocumentEventConstant event) {
		for (DocumentHandlerExtension handler : docHandlerExtensionList) {
			handler.onChange(event);
		}
	}

	@Override
	public void addPropertyGroupHandlerExtension(PropertyGroupHandlerExtension handlerExtension) {
		propertyGroupHandlerExtensionList.add(handlerExtension);
	}
	
	/**
	 * hasPropertyGroups
	 * 
	 * @return
	 */
	public boolean hasPropertyGroups() {
		return (propertyGroup.size()>0);
	}
}
