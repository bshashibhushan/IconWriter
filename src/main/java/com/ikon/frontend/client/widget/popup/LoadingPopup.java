package com.ikon.frontend.client.widget.popup;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.util.OKMBundleResources;

public class LoadingPopup extends DialogBox{
	final Image image;
	private VerticalPanel panel;
	
    /**
     * Public Constructor which initializes the image 
     * and puts it in the center
     * @since 3.0
     */
	public LoadingPopup(){	
		 panel = new VerticalPanel();
		 image = new Image(OKMBundleResources.INSTANCE.loadingGif());
		 
		 panel.add(image);
         center();		
	}
	
	public void show(String text){
		panel.add(new HTML(text));
		show();
	}
	
}