package com.ikon.frontend.client.widget.properties;

import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ResizePolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollPolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.bean.GWTActivity;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.extension.comunicator.TabDocumentComunicator;
import com.ikon.frontend.client.extension.event.HasDocumentEvent.DocumentEventConstant;
import com.ikon.frontend.client.extension.event.handler.DocumentHandlerExtension;
import com.ikon.frontend.client.extension.widget.tabdocument.TabDocumentExtension;
import com.ikon.frontend.client.service.OKMDocumentService;
import com.ikon.frontend.client.service.OKMDocumentServiceAsync;
import com.ikon.frontend.client.util.Activity;

public class RecentActivity extends TabDocumentExtension implements DocumentHandlerExtension{
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);
	
	public static final int NUMBER_OF_COLUMNS = 4;
	private ScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	private ListBox activityActionList;
	
	public RecentActivity(){
		headerTable = new FixedWidthFlexTable();
		dataTable = new FixedWidthGrid();
		activityActionList = new ListBox();
		
		ScrollTableImages scrollTableImages = new ScrollTableImages(){
			public AbstractImagePrototype scrollTableAscending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_asc.gif");
					}
					
					public Image createImage() {
						return  new Image("img/sort_asc.gif");
					}
					
					public String getHTML(){
						return "<img border=\"0\" src=\"img/sort_asc.gif\"/>";
					}
				};
			}
			
			public AbstractImagePrototype scrollTableDescending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_desc.gif");
					}
					
					public Image createImage() {
						return  new Image("img/sort_desc.gif");
					}
					
					public String getHTML(){
						return "<img border=\"0\" src=\"img/sort_desc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableFillWidth() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/fill_width.gif");
					}
					
					public Image createImage() {
						return  new Image("img/fill_width.gif");
					}
					
					public String getHTML(){
						return "<img border=\"0\" src=\"img/fill_width.gif\"/>";
					}
				};
			}
		};
		
		table = new ScrollTable(dataTable, headerTable, scrollTableImages);
		table.setCellSpacing(0);
		table.setCellPadding(2);
		table.setColumnWidth(0, 170);
	    table.setColumnWidth(1, 70);
	    table.setColumnWidth(2, 160);
	    table.setColumnWidth(3, 300);
	    table.setColumnWidth(4, 300);
	    
	    headerTable.setHTML(0, 0, "Activity Info");
	    headerTable.setHTML(0, 1, "User");
	    headerTable.setHTML(0, 2, "Date");
	    headerTable.setHTML(0, 3, "Details");	    
	    headerTable.setWidget(0, 4, activityActionList);
	    
	    //populate list
	    activityActionList.addItem("ALL_ACTIONS");
	    for(Activity act: Activity.values())
	    	activityActionList.addItem(act.name());
	    
	    activityActionList.addChangeHandler(new ChangeHandler(){
		      public void onChange(ChangeEvent event) {
		    	  removeAllRows();
		    	  
		    	  String actName = activityActionList.getValue(activityActionList.getSelectedIndex());
		    	  getActivityList(TabDocumentComunicator.getDocument().getUuid(), actName.equals("ALL_ACTIONS")?"":actName);
		      }
		});	
	    
	    // Table data
	    dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
	    table.setResizePolicy(ResizePolicy.UNCONSTRAINED);
	    table.setScrollPolicy(ScrollPolicy.BOTH);
	    
	    headerTable.addStyleName("okm-DisableSelect");
	    dataTable.addStyleName("okm-DisableSelect");
	    
	    activityActionList.setStyleName("okm-Input");
		
		initWidget(table);
	}

	@Override
	public String getTabText() {
		return "Recent Activity";
	}

	@Override
	public void onChange(DocumentEventConstant event) {
		activityActionList.setItemSelected(0, true);
		removeAllRows();
		getActivityList(TabDocumentComunicator.getDocument().getUuid(), "");
	}
	
	public void getActivityList(String uuid, String actName){
		documentService.getAllActivity(uuid, actName, new AsyncCallback<List<GWTActivity>>(){				
		    public void onSuccess(List<GWTActivity> result){
		    	Collections.reverse(result);
		    	for(final GWTActivity act : result){	
		    		addValidActivities(act);
		    	}			   
		    }
			      
		    public void onFailure(Throwable caught){
			   GeneralComunicator.showError("getDocumentActivityLog", caught);
		    }
				
		});	
	}
	
	protected void addValidActivities(GWTActivity activity) {
		for (Activity actEnum : Activity.values()) {
	        if (actEnum.name().equals(activity.getAction())) {
	           addRow(activity, actEnum.getActivityName()); 
	        }
		}
	}

	private void addRow(GWTActivity activity, String activityName){
		final int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		dataTable.setHTML(rows, 0, activityName);
		dataTable.setHTML(rows, 1, activity.getUser());
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		dataTable.setHTML(rows, 2, dtf.format(activity.getDate()));
		dataTable.setHTML(rows, 3, activity.getParams());
	}
	
	private void removeAllRows(){
		// Purge all rows except first
		while (dataTable.getRowCount() > 0) {
			dataTable.removeRow(0);
		}
		dataTable.resize(0, NUMBER_OF_COLUMNS);
	}

}
