package com.ikon.frontend.client.widget.popup;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.util.ExtendedUtils;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.widget.popup.Status;

public class StapleGroupPopup extends DialogBox{
	private FlexTable table;
	private HorizontalPanel hPanel;
	private Button cancel;
	private Button save;
	private HTML stapleGroupName;
	private Status status;
	private TextBox textBox;
	private HTML text;
	
	public StapleGroupPopup() {
		super(false, true);
		setPopupPosition(600, 300);
		setText("Create New Relation");
		
		// Status
		status = new Status(this);
		status.setStyleName("okm-StatusPopup");
		
		table = new FlexTable();
		table.setCellPadding(4);
		table.setCellSpacing(4);
		table.setWidth("100%");
		hPanel = new HorizontalPanel();
		
		cancel = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isMassive()) {
					Main.get().mainPanel.topPanel.toolBar.executeRefresh();
				}
				hide();
			}
		});
		
		save = new Button("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ExtendedUtils.newStaple(textBox.getValue());
				hide();
			}   
		});
		
		textBox =new TextBox();
		textBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				if (textBox.getValue().length() > 0) {
					save.setEnabled(true);
				} else {
					save.setEnabled(false);
				}
			}
		});
		
		text=new HTML();
		textBox.setStyleName("okm-Select");
		HorizontalPanel grpNamePanel = new HorizontalPanel();
		stapleGroupName = new HTML("");
		grpNamePanel.add(stapleGroupName);
		grpNamePanel.setWidth("100%");
		grpNamePanel.setCellHorizontalAlignment(stapleGroupName, HasAlignment.ALIGN_CENTER);
		
		cancel.setStyleName("okm-NoButton");
		save.setStyleName("okm-AddButton");
		save.setEnabled(false);
		
		hPanel.add(cancel);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(save);
		
		hPanel.setCellHorizontalAlignment(cancel, VerticalPanel.ALIGN_CENTER);
		hPanel.setCellHorizontalAlignment(save, VerticalPanel.ALIGN_CENTER);
		
		table.setWidget(0, 0, textBox);
		table.setWidget(1, 0, grpNamePanel);
		table.setWidget(3, 0, hPanel);
		
		table.getCellFormatter().setHorizontalAlignment(0, 0, HasAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(1, 0, HasAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(3, 0, HasAlignment.ALIGN_CENTER);
		setStyleName("okm-Popup");
		super.hide();
		setWidget(table);
	}
	
	public void show(String msg) {
		//TODO: aqui pueden haber problemas de concurrencia al ser llamado simultaneamente este método
		// cabe la posibilidad de perder algun mensaje de error.
		if (!text.getHTML().equals("")) {
			text.setHTML(text.getHTML() + "<br><br>" + msg);
		} else {
			text.setHTML(msg);
		}
		setText("Create Group Name");
		int left = (Window.getClientWidth()-380)/2;
		int top = (Window.getClientHeight()-200)/2;
		setPopupPosition(left,top);
		super.show();
	}

}
