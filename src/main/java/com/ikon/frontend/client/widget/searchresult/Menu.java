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

package com.ikon.frontend.client.widget.searchresult;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.bean.GWTAvailableOption;
import com.ikon.frontend.client.bean.GWTDocument;
import com.ikon.frontend.client.bean.GWTFolder;
import com.ikon.frontend.client.bean.GWTMail;
import com.ikon.frontend.client.util.Util;

/**
 * Search result menu
 * 
 * @author jllort
 */
public class Menu extends Composite {
	
	private boolean downloadOption = false;
	private boolean goOption = false;
	private boolean previewOption = false;
	
	private MenuBar searchMenu;
	private MenuItem download;
	private MenuItem relation;
	private MenuItem go;
	private MenuItem preview;
	
	/**
	 * Browser menu
	 */
	public Menu() {
		// The item selected must be called on style.css : .okm-MenuBar
		// .gwt-MenuItem-selected
		
		// First initialize language values
		searchMenu = new MenuBar(true);
		download = new MenuItem(
				Util.menuHTML("img/icon/actions/download.gif", Main.i18n("search.result.menu.download")), true,
				downloadFile);
		download.addStyleName("okm-MenuItem");
		relation = new MenuItem(
				Util.menuHTML("img/icon/actions/newRelation.gif", Main.i18n("Related Documents")), true,
				showRelation);
		relation.addStyleName("okm-MenuItem");
		go = new MenuItem(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")),
				true, goDirectory);
		go.addStyleName("okm-MenuItem");
		preview = new MenuItem(Util.menuHTML("img/icon/actions/preview.gif", Main.i18n("tab.document.preview")),
				true, previewFile);
		preview.addStyleName("okm-MenuItem");
		
		searchMenu.addItem(download);
		searchMenu.addItem(go);
		searchMenu.addItem(preview);
		searchMenu.setStyleName("okm-MenuBar");
		
		initWidget(searchMenu);
	}
	
	// Command menu to download file
	Command downloadFile = new Command() {
		public void execute() {
			if (downloadOption) {
				Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.downloadDocument();
			}
			hide();
		}
	};
	
	// Command menu to show document relation
	Command showRelation = new Command() {
		public void execute() {
			Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.documentRelation(null);
			hide();
		}
	};
	
	// Command menu to go directory file
	Command goDirectory = new Command() {
		public void execute() {
			if (goOption) {
				Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.openAllFolderPath();
			}
			hide();
		}
	};
	
	Command previewFile = new Command() {
		public void execute() {
			if(previewOption){
				GWTDocument doc = Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.getDocument();
				if (doc.getMimeType().equals("video/x-flv") || doc.getMimeType().equals("video/mp4") || 
						doc.getMimeType().equals("application/x-shockwave-flash") ||  
						doc.getMimeType().equals("audio/mpeg")) {
					Main.get().previewPopup.showMediaFile(doc.getUuid(), doc.getMimeType());
				} else if(doc.isConvertibleToSwf()){
					Main.get().previewPopup.show(doc.getUuid());
				} else {
					Window.alert("Document MimeType not supported");
				}
			}
			hide();	
		}
	};
	
	/**
	 * Refresh language values
	 */
	public void langRefresh() {
		download.setHTML(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("search.result.menu.download")));
		go.setHTML(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")));
		preview.setHTML(Util.menuHTML("img/icon/actions/preview.gif", Main.i18n("tab.document.preview")));
	}
	
	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.hide();
	}
	
	/**
	 * Checks permissions
	 * 
	 * @param folder
	 */
	public void checkMenuOptionPermissions(GWTFolder folder) {
		downloadOption = false;
		goOption = true;
		previewOption = false;
	}
	
	/**
	 * Checks permissions
	 * 
	 * @param doc
	 */
	public void checkMenuOptionPermissions(GWTDocument doc) {
		downloadOption = true;
		goOption = true;
		previewOption = true;
	}
	
	/**
	 * Checks permissions
	 * 
	 * @param mail
	 */
	public void checkMenuOptionPermissions(GWTMail mail) {
		downloadOption = true;
		goOption = true;
		previewOption = false;
	}
	
	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
		if (downloadOption) {
			enable(download);
		} else {
			disable(download);
		}
		if (goOption) {
			enable(go);
		} else {
			disable(go);
		}
		if (previewOption) {
			enable(preview);
		} else {
			disable(preview);
		}
		if (Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.isFolderSelected()) {
			go.setHTML(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")));
		} else {
			go.setHTML(Util.menuHTML("img/icon/actions/goto_document.gif", Main.i18n("search.result.menu.go.document")));
		}
	}
	
	/**
	 * Enables menu item
	 * 
	 * @param menuItem The menu item
	 */
	public void enable(MenuItem menuItem) {
		menuItem.addStyleName("okm-MenuItem");
		menuItem.removeStyleName("okm-MenuItem-strike");
	}
	
	/**
	 * Disable the menu item
	 * 
	 * @param menuItem The menu item
	 */
	public void disable(MenuItem menuItem) {
		menuItem.removeStyleName("okm-MenuItem");
		menuItem.addStyleName("okm-MenuItem-strike");
	}
	
	/**
	 * setAvailableOption
	 * 
	 * @param option
	 */
	public void setAvailableOption(GWTAvailableOption option) {
		if (!option.isDownloadOption()) {
			searchMenu.removeItem(download);
		}
		if (!option.isGotoFolderOption()) {
			searchMenu.removeItem(go);
		}
		if(!Main.get().workspaceUserProperties.getWorkspace().isTabDocumentPreviewVisible()){
			searchMenu.removeItem(preview);
		}
	}
}
