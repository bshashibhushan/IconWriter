package com.ikon.frontend.client.widget;

import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.bean.GWTDocument;
import com.ikon.frontend.client.constants.service.RPCService;
import com.ikon.frontend.client.extension.comunicator.FileBrowserComunicator;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.service.OKMDigitalSignatureService;
import com.ikon.frontend.client.service.OKMDigitalSignatureServiceAsync;
import com.ikon.frontend.client.util.OKMBundleResources;
import com.ikon.frontend.client.widget.popup.LoadingPopup;

public class PreviewPopup extends DialogBox{
	private final OKMDigitalSignatureServiceAsync signService = (OKMDigitalSignatureServiceAsync) GWT.create(OKMDigitalSignatureService.class);

	private VerticalPanel previewPanel;
	private HorizontalPanel previewOptionsPanel;
	private SWFWidget flexPaper;
	private Image signImage;
	private Image stampImage;
	private Image closeImage;
	private LoadingPopup loadingPopup;
	
	public PreviewPopup(){
		super(true);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setPopupPosition(100, 0);
		setText("Document Preview");
		
		previewPanel = new VerticalPanel();
		previewOptionsPanel = new HorizontalPanel();
		
		previewOptionsPanel.getElement().setId("previewPanel");
		
		closeImage = new Image(OKMBundleResources.INSTANCE.closePreview());
		closeImage.setStyleName("okm-KeyMap-ImageHover");
		
		previewOptionsPanel.getElement().setId("previewPanel");
		
		closeImage.getElement().setId("close");
		closeImage.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		signImage = new Image(OKMBundleResources.INSTANCE.sign());
		stampImage = new Image(OKMBundleResources.INSTANCE.stamp());
		
		signImage.setStyleName("okm-KeyMap-ImageHover");
		stampImage.setStyleName("okm-KeyMap-ImageHover");
		
		signImage.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if(Main.get().workspaceUserProperties.getWorkspace().isSignConfigured()){
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_SIGN_DOCUMENT);
					Main.get().confirmPopup.center();
				} else {
					Window.alert("Please configure your Signature");
				}
			}
		});
		
		stampImage.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_STAMP_DOCUMENT);
				Main.get().confirmPopup.center();				
			}
		});

		closeImage.setTitle("Close Preview");
		signImage.setTitle("Sign Documents");
		stampImage.setTitle("Stamp Documents");
		

		previewOptionsPanel.setWidth("90px");		
		setWidget(previewPanel);
	}
	
	public void show(String uuid){	
		previewPanel.clear();
		
		if(Main.get().workspaceUserProperties.getWorkspace().isPrintPreview()){
			this.flexPaper = new SWFWidget("../js/flexpaper/FlexPaperViewer.swf", (Window.getClientWidth() - 210), (Window.getClientHeight()) - 65);
		} else {
			this.flexPaper = new SWFWidget("../js/flexpaper/FlexPaperViewerRO.swf", (Window.getClientWidth() - 210), (Window.getClientHeight() - 65));
		}
		
		if(GeneralComunicator.getWorkspace().getAvailableOption().isSignOption())
			previewOptionsPanel.add(signImage);	
			previewOptionsPanel.add(new HTML(" "));
			
		if(GeneralComunicator.getWorkspace().getAvailableOption().isStampOption())
			previewOptionsPanel.add(stampImage);
			previewOptionsPanel.add(new HTML(" "));
			
		previewOptionsPanel.add(closeImage);	
					
		previewPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		previewPanel.add(previewOptionsPanel);
		previewPanel.add(flexPaper);
		
		setWidget(previewPanel);
		
		//generate swf file
		String url = RPCService.ConverterServlet + "?inline=true&toSwf=true&uuid=" + uuid;
		setFlashVars(url);
		show();
	}

	public void showMediaFile(String uuid, String mimeType){
		previewPanel.clear();

		String url = RPCService.DownloadServlet +"?uuid=" + uuid;	
		String mediaProvider = "";
		
		if (mimeType.equals("audio/mpeg")) {
			mediaProvider = "sound";
		} else if (mimeType.equals("video/x-flv") || mimeType.equals("video/mp4")) {
			mediaProvider = "video";
		} else if (mimeType.equals("application/x-shockwave-flash")) {
			mediaProvider = "";
		}
		
		flexPaper = new SWFWidget("../js/mediaplayer/player.swf", (Window.getClientWidth() - 210), (Window.getClientHeight()) - 65);
		flexPaper.addFlashVar("file", URL.encodePathSegment(url));
		flexPaper.addFlashVar("provider", mediaProvider);
		flexPaper.addFlashVar("autostart", "true");
		flexPaper.addFlashVar("allowscriptaccess", "always");
		flexPaper.addFlashVar("allowFullScreen", "true");
		flexPaper.addFlashVar("id", "jsmediaplayer");
		flexPaper.addFlashVar("name", "jsmediaplayer");
		
		previewPanel.add(flexPaper);		
		setWidget(previewPanel);
		
		show();		
	}
	
	
	public void setFlashVars(String url){		
		flexPaper.getElement().setId("documentViewer");
		flexPaper.addFlashVar("SwfFile", URL.encodePathSegment(url));
		flexPaper.addFlashVar("Scale", "1.2");
		flexPaper.addFlashVar("key", "@25b39f7859c86c82f97$bbcc2092dc86c7d54a8");
		flexPaper.addFlashVar("ZoomTransition", "easeout");
		flexPaper.addFlashVar("ZoomTime", "0.5");
		flexPaper.addFlashVar("ZoomInterval", "0.1");
		flexPaper.addFlashVar("FitPageOnLoad", "true");
		flexPaper.addFlashVar("FitWidthOnLoad", "false");
		flexPaper.addFlashVar("FullScreenAsMaxWindow", "false");
		flexPaper.addFlashVar("ProgressiveLoading", "false");
		flexPaper.addFlashVar("MinZoomSize", "0.2");
		flexPaper.addFlashVar("PrintEnabled", "true");
		flexPaper.addFlashVar("MaxZoomSize", "5");
		flexPaper.addFlashVar("SearchMatchAll", "false");
		flexPaper.addFlashVar("InitViewMode", "Portrait");
		flexPaper.addFlashVar("RenderingOrder", "flash,html");
		flexPaper.addFlashVar("StartAtPage", "");

		flexPaper.addFlashVar("ViewModeToolsVisible", "true");
		flexPaper.addFlashVar("ZoomToolsVisible", "true");
		flexPaper.addFlashVar("NavToolsVisible", "true");
		flexPaper.addFlashVar("CursorToolsVisible", "true");
		flexPaper.addFlashVar("SearchToolsVisible", "true");
		flexPaper.addFlashVar("localeChain", "en_US");
	}

	public void signDocument(){
		loadingPopup = new LoadingPopup();
		loadingPopup.show("Signing Document");
		final GWTDocument doc = FileBrowserComunicator.getDocument();
		 signService.signDocument(doc.getPath(), new AsyncCallback<Object>(){
		        public void onSuccess(Object result) {
		        	loadingPopup.hide();
		        	Main.get().mainPanel.topPanel.toolBar.executeRefresh();
		        	Main.get().previewPopup.show(doc.getUuid());
		        }

		        public void onFailure(Throwable caught){	
		        	loadingPopup.hide();
		            GeneralComunicator.showError("Failed to Sign Document", caught);
		        }
		    });
	}
	
	public static native void append(String mid)/*-{ 	
 		$wnd.alert(JSON.stringify($wnd.$FlexPaper('documentViewer').getMarkList()));

	}-*/;
}
