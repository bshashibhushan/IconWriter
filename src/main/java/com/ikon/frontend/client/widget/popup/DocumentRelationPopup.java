package com.ikon.frontend.client.widget.popup;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.bean.GWTStaple;
import com.ikon.frontend.client.bean.GWTStapleGroup;
import com.ikon.frontend.client.service.OKMStaplingService;
import com.ikon.frontend.client.service.OKMStaplingServiceAsync;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.bean.GWTDocument;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.util.CommonUI;
import com.ikon.frontend.client.util.Util;

public class DocumentRelationPopup extends DialogBox implements ClickHandler{
	private final OKMStaplingServiceAsync staplingService = (OKMStaplingServiceAsync)GWT.create(OKMStaplingService.class);
	private HTML text;
	private Button button;
	private ScrollPanel sPanel;
	private VerticalPanel vPanel;
	private FlexTable table;
	private HorizontalPanel hPanel;
	private int rowNum=0;

	/**
	 * Error popup
	 * @param uuid 
	 */
	public DocumentRelationPopup(final GWTDocument doc) {
		
		// Establishes auto-close when click outside
		super(false,true);
		setPopupPosition(400, 100);
		hPanel= new HorizontalPanel();
		vPanel = new VerticalPanel();
		vPanel.setWidth("500");
		vPanel.setHeight("350");
	    sPanel = new ScrollPanel();
		sPanel.setStyleName("okm-Popup-text");
		table = new FlexTable();
		table.setWidth("100%");
		this.staplingService.getAll(doc.getUuid(), new AsyncCallback<List<GWTStapleGroup>>()
		{
			
			public void onSuccess(List<GWTStapleGroup> result) {
		   	  for(final GWTStapleGroup sGroup:result)
		   	  {
		   		table.setWidget(rowNum, 0, new Button("Add ", new ClickHandler() { 
					@Override
					public void onClick(ClickEvent event) {
						Main.get().mainPanel.search.searchBrowser.searchResult.searchFullResult.setGroupId(String.valueOf(sGroup.getId()));
					}
				}));
		   		table.setWidget(rowNum++, 1, new Label(sGroup.getName()));
		   		
		   		  for(GWTStaple sta:sGroup.getStaples())
		   		  {
		   			HorizontalPanel hPanel = new HorizontalPanel();
		   			hPanel.setStyleName("okm-NoWrap");
		   			Anchor anchor = new Anchor();
		   			
		   			anchor.setHTML(sta.getDoc().getName());
		   			anchor.setStyleName("okm-Hyperlink");
		   			String path = "";
		   			// On attachemt case must remove last folder path, because it's internal usage not for visualization
		   				anchor.setTitle(sta.getDoc().getParent());
		   				path = sta.getDoc().getPath();
		   			final String docPath = path;
		   			anchor.addClickHandler(new ClickHandler() {
		   				@Override
		   				public void onClick(ClickEvent event) {
		   					CommonUI.openPath(docPath.substring(0,docPath.lastIndexOf("/")), docPath);
		   					hide();
		   				}
		   			});
		   			hPanel.add(anchor);
		   			table.setHTML(rowNum, 0, Util.mimeImageHTML(doc.getMimeType()));
		   			table.setWidget(rowNum, 1, hPanel);
		   			table.getCellFormatter().setWidth(rowNum, 0, "50");
					table.getCellFormatter().setHorizontalAlignment(rowNum++, 0, HasHorizontalAlignment.ALIGN_CENTER);
		   		  }  	
		 
		   	  }
		      }
			  public void onFailure(Throwable caught)
			  {
			       GeneralComunicator.showError("getAll", caught);
			  }
		 });
		button = new Button(Main.i18n("button.close"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		/*stapleButton = new Button("Enable", new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
			staplingService.getAll(doc.getUuid(), new AsyncCallback<List<GWTStapleGroup>>()
			{
			    public void onSuccess(List<GWTStapleGroup> result) {
			   	  for(GWTStapleGroup sGroup:result)
				  {
			   		  groupId=sGroup.getId();
			   		  Window.alert("Relation "+groupId);
			   		  SearchFullResult.setgroupId(String.valueOf(groupId));
			   		  
				  }
			    }
			    public void onFailure(Throwable caught)
				  {
				       GeneralComunicator.showError("getAll", caught);
				  }
			});
				hide();
			}
		});*/
		text= new HTML();	
		sPanel.add(table);
		sPanel.setPixelSize(690, 300);
		vPanel.setTitle("Related Documents");
		vPanel.add(sPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(sPanel);
		vPanel.add(new HTML("<br>"));
		hPanel.add(button);
		HTML space = new HTML();
		space.setWidth("50");
		hPanel.add(space);
		//hPanel.add(stapleButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(button, HasAlignment.ALIGN_CENTER);
		//vPanel.setCellHorizontalAlignment(stapleButton, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(text, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(sPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHeight(sPanel, "300");
		button.setStyleName("okm-YesButton");
		button.setStyleName("okm-NoButton");
		setText("Related Documents");
		setStyleName("okm-Popup");
		hide();
		setWidget(vPanel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		Log.debug("onClick("+event+")");
		hide();
		// Removes all previous text for next errors messages, varios errors can be added simultanealy
		// on show(String msg )
		text.setText("");
		Log.debug("onClick: void");
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText("Related Documents");
		button.setText(Main.i18n("button.close"));
	}
	
	/**
	 * Show the popup error
	 * 
	 * @param msg Error message
	 */
	public void show(String msg) {
		//TODO: aqui pueden haber problemas de concurrencia al ser llamado simultaneamente este método
		// cabe la posibilidad de perder algun mensaje de error.
		if (!text.getHTML().equals("")) {
			text.setHTML(text.getHTML() + "<br><br>" + msg);
		} else {
			text.setHTML(msg);
		}
		setText("Related Documents");
		int left = (Window.getClientWidth()-380)/2;
		int top = (Window.getClientHeight()-200)/2;
		setPopupPosition(left,top);
		super.show();
	}

}
