package com.ikon.frontend.client.widget.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.ikon.frontend.client.Main;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.constants.service.RPCService;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.service.OKMDigitalSignatureService;
import com.ikon.frontend.client.service.OKMDigitalSignatureServiceAsync;

public class RegisterPFXPopup extends DialogBox{
	private final OKMDigitalSignatureServiceAsync signService = (OKMDigitalSignatureServiceAsync) GWT
			.create(OKMDigitalSignatureService.class);
	
	private VerticalPanel registerPFXPanel;
	private FlexTable PFXTable;
	private FormPanel form;
	private FileUpload upload;
	private Label registerPFXText;
	private Label pfxPasswordText;
	private HTML uploadPFXError;
	private HTML pfxPasswordError;
	private PasswordTextBox pfxPassword;
	private Button submit;	
	
	public RegisterPFXPopup(){		
		super(true);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setPopupPosition(480, 250);
		setText("Register PFX");
		
		registerPFXPanel = new VerticalPanel();
		PFXTable = new FlexTable();
		form = new FormPanel();
		upload = new FileUpload();
		uploadPFXError = new HTML("Please upload pfx only");
		pfxPasswordError = new HTML("Password cannot be empty");
		submit = new Button("Register PFX");
		registerPFXText = new Label("Please upload your PFX file");
		pfxPasswordText = new Label("Pfx Password");
		pfxPassword = new PasswordTextBox();
		
		uploadPFXError.setVisible(false);
		pfxPasswordError.setVisible(false);
		
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		
		upload.setName("uploadFormElement");
		pfxPassword.setName("pfxPassword");
		
		uploadPFXError.setStyleName("okm-Input-Error");
		pfxPasswordError.setStyleName("okm-Input-Error");
		
		PFXTable.setWidget(0, 0, registerPFXText);
		PFXTable.setWidget(1, 0, pfxPasswordText);
		PFXTable.setWidget(0, 1, upload);
		PFXTable.setWidget(1, 1, pfxPassword);
		PFXTable.setWidget(2, 1, submit);
		registerPFXPanel.add(PFXTable);
		registerPFXPanel.add(uploadPFXError);
		registerPFXPanel.add(pfxPasswordError);
				
		submit.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	    		form.setAction(RPCService.RegisterPFXService + "?pfxPass=" + pfxPassword.getText() + "&user=" + getUser());
	            form.submit(); 
	        }
		});
		
		 // Add an event handler to the form.
	    form.addSubmitHandler(new FormPanel.SubmitHandler() {
	      public void onSubmit(SubmitEvent event) {
	        if (!upload.getFilename().endsWith(".pfx")) {
	        	pfxPasswordError.setVisible(false);
	        	uploadPFXError.setVisible(true);
	            event.cancel();
	        } else if (pfxPassword.getText().length() == 0){
	        	uploadPFXError.setVisible(false);
	        	pfxPasswordError.setVisible(true);
	            event.cancel();
	        } else {
	        	uploadPFXError.setVisible(false);
	        	pfxPasswordError.setVisible(false);
	        }
	      }
	    });
	    
	    form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
	      public void onSubmitComplete(SubmitCompleteEvent event) {
	    	  hide();
	      }
	    });
	    
	    submit.setStyleName("okm-ChangeButton");
	    upload.setStyleName("okm-Input");
		
		form.setWidget(registerPFXPanel);
		setWidget(form);			
	}	  
	
	private String getUser() {
		return Main.get().userHome.getUser();
	}
	
	public void deletePFX(){
		signService.deletePFX(getUser(), new AsyncCallback<Object>(){
	        public void onSuccess(Object result) {
	        	 Main.get().confirmPopup.hide();
	        }

	        public void onFailure(Throwable caught){
	          String error = "Failed to delete PFX. Please try again later";
	          GeneralComunicator.showError(error, caught);
	        }
	    });
	}
	    
}
