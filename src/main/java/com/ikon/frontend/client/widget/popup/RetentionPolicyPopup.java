package com.ikon.frontend.client.widget.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.bean.GWTRetentionPolicy;
import com.ikon.frontend.client.service.OKMRetentionPolicyService;
import com.ikon.frontend.client.service.OKMRetentionPolicyServiceAsync;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.bean.GWTUser;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.extension.comunicator.TabDocumentComunicator;
import com.ikon.frontend.client.util.OKMBundleResources;
import com.ikon.frontend.client.widget.form.FolderSelectPopup;
import com.ikon.frontend.client.widget.searchin.HasSearch;

public class RetentionPolicyPopup extends DialogBox{
  private final OKMRetentionPolicyServiceAsync retentionPolicyService = (OKMRetentionPolicyServiceAsync) GWT.create(OKMRetentionPolicyService.class);

  private VerticalPanel vPanel;
  private HorizontalPanel hPanel;
  private HorizontalPanel hPanel1;
  private HorizontalPanel hPanel2;
  private HorizontalPanel errorPanel;
  private TextBox retentionDays;
  private TextBox retentionDestFolderInput;
  private Button closebutton;
  private Button addButton;
  private HTML retentionDaysText;
  private HTML retentionDestFolder;
  private HTML retentionDaysError;
  private HTML retentionDestFolderError;
  private FolderSelectPopup folderSelectPopup;
  private HasSearch search;  
  
  public RetentionPolicyPopup(){
	  //empty constructor
  }
  
  public RetentionPolicyPopup(final String uuid, final String docPath){
	  
    super(false, true);

    this.vPanel = new VerticalPanel();
    this.hPanel = new HorizontalPanel();
    this.hPanel1 = new HorizontalPanel();
    this.hPanel2 = new HorizontalPanel();
    errorPanel = new HorizontalPanel();
	
	retentionDays = new TextBox();
	retentionDays.setStyleName("okm-Input");
	retentionDestFolderInput = new TextBox();
	retentionDestFolderInput.setStyleName("okm-Input");
	retentionDaysText = new HTML("<b>"+Main.i18n("document.retention.expiration"));
	retentionDestFolder = new HTML("<b>"+Main.i18n("document.retention.destination.folder"));
	
	folderSelectPopup = new FolderSelectPopup();
	folderSelectPopup.setStyleName("okm-Popup");
	folderSelectPopup.addStyleName("okm-DisableSelect");
	
	retentionDaysError = new HTML("Please enter valid number of days");
	retentionDestFolderError = new HTML(Main.i18n("document.retention.dest.folder.error"));
	retentionDaysError.setStyleName("okm-Input-Error");
	retentionDestFolderError.setStyleName("okm-Input-Error");

    this.closebutton = new Button(GeneralComunicator.i18n("button.close"), new ClickHandler(){
      public void onClick(ClickEvent event) {
    	retentionDays.setText("");
        RetentionPolicyPopup.this.hide();
      }
    });
    
    this.addButton = new Button(GeneralComunicator.i18n("button.add"), new ClickHandler(){
      public void onClick(ClickEvent event) {
    	  if(retentionDays.getText().equals("") || retentionDays.getText().equals("0")){
    		   retentionDaysError.setVisible(true);
    	  } else if(retentionDestFolderInput.getText().equals("")){
  		       retentionDaysError.setVisible(false);
    		   retentionDestFolderError.setVisible(true);
    	  } else {
    		  retentionDaysError.setVisible(false);
    		  retentionDestFolderError.setVisible(false);
    		  int days = Integer.parseInt(retentionDays.getValue());
    		  GWTRetentionPolicy policy = new GWTRetentionPolicy();
    		  policy.setNodeUuid(uuid);
    		  policy.setSourcePath(docPath);
    		  policy.setDestinationPath(retentionDestFolderInput.getValue());
    		  for(GWTUser subscriber : TabDocumentComunicator.getDocument().getSubscriptors()){
        		  policy.setEmailList(subscriber.getId()); 
    		  }
        	  policy.setNodeType("openkm:document");
    		  policy.setRetentionDays(days);
    		  policy.setActive(true);
    		  retentionPolicyService.applyRetentionPolicy(policy, callBackRetentionPolicy);
    		  RetentionPolicyPopup.this.hide();
    	  }
      }
    });
	
	//folder popup
	
	Image pathExplorer = new Image(OKMBundleResources.INSTANCE.folderExplorer());
	pathExplorer.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			folderSelectPopup.show(retentionDestFolderInput, search);
		}
	});
	
	pathExplorer.setStyleName("okm-KeyMap-ImageHover");
	retentionDestFolderInput.setEnabled(false);
	retentionDestFolderInput.setTitle(retentionDestFolderInput.getText());
	retentionDaysError.setVisible(false);
	retentionDestFolderError.setVisible(false);    
    
    this.vPanel.setWidth("300px");
    this.vPanel.setHeight("100px");
    this.closebutton.setStyleName("okm-Button");
    this.addButton.setStyleName("okm-Button");
    this.addButton.setEnabled(false);
    
    this.hPanel1.add(retentionDaysText);
    this.hPanel1.add(new HTML("&nbsp;&nbsp;"));
    this.hPanel1.add(retentionDays);
    this.hPanel1.add(new HTML("&nbsp;"));
	
	this.hPanel1.setCellHorizontalAlignment(retentionDaysText, VerticalPanel.ALIGN_CENTER);
    this.hPanel1.setCellHorizontalAlignment(retentionDays, VerticalPanel.ALIGN_CENTER);
	
	hPanel2.add(retentionDestFolder);
    hPanel2.add(new HTML("&nbsp;&nbsp;"));
    hPanel2.add(retentionDestFolderInput);
    hPanel2.add(new HTML("&nbsp;"));
	hPanel2.add(pathExplorer);
	hPanel2.setCellVerticalAlignment(pathExplorer, HasAlignment.ALIGN_MIDDLE);
	
    this.hPanel2.setCellHorizontalAlignment(retentionDestFolder, VerticalPanel.ALIGN_CENTER);
    this.hPanel2.setCellHorizontalAlignment(retentionDestFolderInput, VerticalPanel.ALIGN_CENTER);
    this.hPanel2.setCellHorizontalAlignment(pathExplorer, VerticalPanel.ALIGN_CENTER);

    this.errorPanel.add(retentionDaysError);
    this.errorPanel.add(retentionDestFolderError);
    
    this.hPanel.add(this.closebutton);
    this.hPanel.add(new HTML("&nbsp;&nbsp;"));
    this.hPanel.add(this.addButton);

    this.hPanel.setCellHorizontalAlignment(this.closebutton, VerticalPanel.ALIGN_CENTER);
    this.hPanel.setCellHorizontalAlignment(this.addButton, VerticalPanel.ALIGN_CENTER);

    this.vPanel.add(new HTML("<br>"));
    this.vPanel.add(this.hPanel1);
    this.vPanel.add(new HTML("<br>"));
    vPanel.add(hPanel2);
    this.vPanel.add(new HTML("<br>"));
    vPanel.add(errorPanel);
    this.vPanel.add(new HTML("<br>"));
    this.vPanel.add(this.hPanel);

    this.vPanel.setCellHorizontalAlignment(this.hPanel1, VerticalPanel.ALIGN_CENTER);
    this.vPanel.setCellHorizontalAlignment(this.hPanel2, VerticalPanel.ALIGN_CENTER);
    this.vPanel.setCellHorizontalAlignment(errorPanel, VerticalPanel.ALIGN_CENTER);
    this.vPanel.setCellHorizontalAlignment(this.hPanel, VerticalPanel.ALIGN_CENTER);

    super.hide();
    setWidget(this.vPanel);
  }

  public void langRefresh()
  {
    setText(Main.i18n("document.retention.policy"));
    this.closebutton.setText(GeneralComunicator.i18nExtension("button.close"));
    this.addButton.setText(GeneralComunicator.i18nExtension("button.add"));
    this.retentionDaysText.setText(Main.i18n("document.retention.expiration"));
	retentionDestFolder = new HTML("<b>"+Main.i18n("document.retention.destination.folder"));
	retentionDaysError = new HTML(Main.i18n("document.retention.error"));
	retentionDestFolderError = new HTML(Main.i18n("document.retention.dest.folder.error"));
  }

  public void show() {
    setText(Main.i18n("document.retention.policy"));
    this.addButton.setEnabled(true);
    int left = (Window.getClientWidth() - 300) / 2;
    int top = (Window.getClientHeight() - 100) / 2;
    setPopupPosition(left, top);
    super.show();
  }

  public void delete(){
	  String uuid = TabDocumentComunicator.getDocument().getUuid();
	  retentionPolicyService.delete(uuid, callBackRetentionPolicy);
  }
/**
 * Call back update user workspace data
 */
final AsyncCallback<Object> callBackRetentionPolicy = new AsyncCallback<Object>() {
	public void onSuccess(Object result) {
		RetentionPolicyPopup.this.hide();		
	    Main.get().activeFolderTree.refresh(false);
	}
	
	public void onFailure(Throwable caught) {
		Main.get().showError("callbackUpdateUserWorkspace", caught);
	}
};

}