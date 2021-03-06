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

package com.ikon.frontend.client.widget.thesaurus;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.util.OKMBundleResources;

/**
 * Status
 * 
 * @author jllort
 *
 */
public class Status extends PopupPanel {
	
	private HorizontalPanel hPanel;
	private HTML msg;
	private HTML space;
	private Image image;
	private boolean flag_getChilds 			= false;
	private boolean flag_root				= false;
	private boolean flag_keywords			= false;
	
	/**
	 * Status
	 */
	public Status() {
		super(false,true);
		hPanel = new HorizontalPanel();
		image = new Image(OKMBundleResources.INSTANCE.indicator());
		msg = new HTML("");
		space = new HTML("");
		
		hPanel.add(image);
		hPanel.add(msg);
		hPanel.add(space);
		
		hPanel.setCellVerticalAlignment(image, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellVerticalAlignment(msg, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		hPanel.setCellWidth(image, "30px");
		hPanel.setCellWidth(space, "7px");
		
		hPanel.setHeight("25px");
		
		msg.setStyleName("okm-NoWrap");
		
		super.hide();
		setWidget(hPanel);
	}
	
	/**
	 * Refreshing satus
	 */
	public void refresh() {
		if (flag_getChilds || flag_root || flag_keywords ) {
			int left = ((Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.getOffsetWidth()-125)/2) +
						 Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.getAbsoluteLeft();
			int top = ((Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.getOffsetHeight()-40)/2) + 
			            Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.getAbsoluteTop();
			setPopupPosition(left,top);
			Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.thesaurusPanel.scrollDirectoryPanel.addStyleName("okm-PanelRefreshing");
			Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.thesaurusPanel.scrollKeywordPanel.addStyleName("okm-PanelRefreshing");
			super.show();
		} else {
			super.hide();
			Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.thesaurusPanel.scrollDirectoryPanel.removeStyleName("okm-PanelRefreshing");
			Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.thesaurusPanel.scrollKeywordPanel.removeStyleName("okm-PanelRefreshing");
		}
	}
	
	/**
	 * Set childs flag
	 */
	public void setFlagChilds() {
		msg.setHTML(Main.i18n("tree.status.refresh.folder"));
		flag_getChilds = true;
		refresh();
	}
	
	/**
	 * Unset childs flag
	 */
	public void unsetFlagChilds() {
		flag_getChilds = false;
		refresh();
	}
	
	/**
	 * Set remove root flag
	 */
	public void setFlagRoot() {
		msg.setHTML(Main.i18n("tree.status.refresh.get.root"));
		flag_root= true;
		refresh();
	}
	
	/**
	 * Unset remove root flag
	 */
	public void unsetFlagRoot() {
		flag_root = false;
		refresh();
	}
	
	/**
	 * Set remove keywords flag
	 */
	public void setFlagKeywords() {
		msg.setHTML(Main.i18n("tree.status.refresh.get.keywords"));
		flag_keywords= true;
		refresh();
	}
	
	/**
	 * Unset remove keywords flag
	 */
	public void unsetFlagKeywords() {
		flag_keywords = false;
		refresh();
	}
}
