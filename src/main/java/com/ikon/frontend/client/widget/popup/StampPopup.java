package com.ikon.frontend.client.widget.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.service.OKMStampService;
import com.ikon.frontend.client.service.OKMStampServiceAsync;
import com.ikon.frontend.client.bean.GWTDocument;
import com.ikon.frontend.client.bean.GWTStamp;
import com.ikon.frontend.client.extension.comunicator.FileBrowserComunicator;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.extension.comunicator.TabDocumentComunicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StampPopup extends DialogBox{
  private final OKMStampServiceAsync stampService = (OKMStampServiceAsync)GWT.create(OKMStampService.class);
  private VerticalPanel vPanel;
  private HorizontalPanel hPanel;
  private Button closebutton;
  private Button stampButton;
  private ListBox listBox;
  private Map<String, GWTStamp> stampMap;

  public StampPopup(){
    super(false, true);

    this.stampMap = new HashMap<String,GWTStamp>();
    this.vPanel = new VerticalPanel();
    this.hPanel = new HorizontalPanel();

    this.closebutton = new Button(GeneralComunicator.i18n("button.close"), new ClickHandler()
    {
      public void onClick(ClickEvent event) {
        StampPopup.this.hide();
      }
    });
    this.stampButton = new Button(GeneralComunicator.i18n("stamp"), new ClickHandler()
    {
      public void onClick(ClickEvent event) {
    	
        StampPopup.this.stamp();
        StampPopup.this.hide();
      }
    });
    this.listBox = new ListBox();
    this.listBox.addChangeHandler(new ChangeHandler()
    {
      public void onChange(ChangeEvent arg0) {
        if (StampPopup.this.listBox.getSelectedIndex() > 0)
          StampPopup.this.stampButton.setEnabled(true);
        else
          StampPopup.this.stampButton.setEnabled(false);
      }
    });
    this.listBox.setStyleName("okm-Select");

    this.vPanel.setWidth("300px");
    this.vPanel.setHeight("50px");
    this.closebutton.setStyleName("okm-Button");
    this.stampButton.setStyleName("okm-Button");
    this.stampButton.setEnabled(false);

    this.hPanel.add(this.closebutton);
    this.hPanel.add(new HTML("&nbsp;&nbsp;"));
    this.hPanel.add(this.stampButton);

    this.hPanel.setCellHorizontalAlignment(this.closebutton, VerticalPanel.ALIGN_CENTER);
    this.hPanel.setCellHorizontalAlignment(this.stampButton, VerticalPanel.ALIGN_CENTER);

    this.vPanel.add(new HTML("<br>"));
    this.vPanel.add(this.listBox);
    this.vPanel.add(new HTML("<br>"));
    this.vPanel.add(this.hPanel);
    this.vPanel.add(new HTML("<br>"));

    this.vPanel.setCellHorizontalAlignment(this.listBox, VerticalPanel.ALIGN_CENTER);
    this.vPanel.setCellHorizontalAlignment(this.hPanel, VerticalPanel.ALIGN_CENTER);

    super.hide();
    setWidget(this.vPanel);
  }

  public void langRefresh()
  {
    setText(GeneralComunicator.i18nExtension("stamp.label"));
    this.closebutton.setText(GeneralComunicator.i18nExtension("button.close"));
    this.stampButton.setText(GeneralComunicator.i18nExtension("button.stamp"));
  }

  public void show()
  {
    setText(GeneralComunicator.i18nExtension("stamp.label"));
    getAll();
    this.stampButton.setEnabled(false);
    int left = (Window.getClientWidth() - 300) / 2;
    int top = (Window.getClientHeight() - 100) / 2;
    setPopupPosition(left, top);
    super.show();
  }

  private void getAll(){
    if (FileBrowserComunicator.isDocumentSelected()){}
      this.stampService.findAll(new AsyncCallback<List<GWTStamp>>(){
        public void onSuccess(List<GWTStamp> result) {
          StampPopup.this.stampMap = new HashMap<String,GWTStamp>();
          StampPopup.this.listBox.clear();
          StampPopup.this.listBox.addItem("", "");
          for (GWTStamp stamp : result) {
            String key = stamp.getId() + "-" + stamp.getType();
            StampPopup.this.stampMap.put(key, stamp);
            StampPopup.this.listBox.addItem(stamp.getName(), key);
          }
        }

        public void onFailure(Throwable caught)
        {
          GeneralComunicator.showError("findAll", caught);
        }
      });
  }

  private void stamp(){
    if ((this.listBox.getSelectedIndex() > 0) && (FileBrowserComunicator.isDocumentSelected())) {
      String key = this.listBox.getValue(this.listBox.getSelectedIndex());
      GWTStamp stamp = (GWTStamp)this.stampMap.get(key);
      final GWTDocument gwtDocument = TabDocumentComunicator.getDocument();
      this.stampService.Stamp(stamp.getId(), stamp.getType(), gwtDocument.getPath(), new AsyncCallback<Object>()
      {
        public void onSuccess(Object result) {
          GeneralComunicator.refreshUI();
          Main.get().previewPopup.show(gwtDocument.getUuid());
        }

        public void onFailure(Throwable caught)
        {
          GeneralComunicator.showError("Stamp", caught);
        }
      });
    }
  }
}