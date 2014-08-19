package com.ikon.frontend.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.ikon.frontend.client.bean.GWTStaple;
import com.ikon.frontend.client.bean.GWTStapleGroup;
import com.ikon.frontend.client.service.OKMStaplingService;
import com.ikon.frontend.client.service.OKMStaplingServiceAsync;
import com.ikon.frontend.client.util.OKMBundleResources;
import com.ikon.frontend.client.util.StapleTableManager;
import com.ikon.frontend.client.widget.ConfirmPopup;
import com.ikon.frontend.client.widget.properties.TabDocumentStapling;
import com.ikon.frontend.client.widget.properties.TabFolderStapling;
import com.ikon.frontend.client.widget.properties.TabMailStapling;
import com.ikon.frontend.client.widget.toolbar.ToolBar;
import com.ikon.frontend.client.bean.GWTDocument;
import com.ikon.frontend.client.bean.GWTFolder;
import com.ikon.frontend.client.bean.GWTMail;
import com.ikon.frontend.client.constants.service.RPCService; 
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.extension.comunicator.NavigatorComunicator;
import com.ikon.frontend.client.extension.comunicator.TabDocumentComunicator;
import com.ikon.frontend.client.extension.comunicator.TabFolderComunicator;
import com.ikon.frontend.client.extension.comunicator.TabMailComunicator;
import com.ikon.frontend.client.extension.comunicator.WorkspaceComunicator;
import com.ikon.frontend.client.extension.event.HasDocumentEvent;
import com.ikon.frontend.client.extension.event.HasFolderEvent;
import com.ikon.frontend.client.extension.event.HasLanguageEvent;
import com.ikon.frontend.client.extension.event.HasMailEvent;
import com.ikon.frontend.client.extension.event.HasNavigatorEvent;
import com.ikon.frontend.client.extension.event.HasWorkspaceEvent;
import com.ikon.frontend.client.extension.event.handler.DocumentHandlerExtension;
import com.ikon.frontend.client.extension.event.handler.FolderHandlerExtension;
import com.ikon.frontend.client.extension.event.handler.LanguageHandlerExtension;
import com.ikon.frontend.client.extension.event.handler.MailHandlerExtension;
import com.ikon.frontend.client.extension.event.handler.NavigatorHandlerExtension;
import com.ikon.frontend.client.extension.event.handler.WorkspaceHandlerExtension;
import com.ikon.frontend.client.extension.widget.toolbar.ToolBarButtonExtension;
import java.util.ArrayList;
import java.util.List;

public class Stapling
  implements DocumentHandlerExtension, FolderHandlerExtension, MailHandlerExtension, NavigatorHandlerExtension, LanguageHandlerExtension, WorkspaceHandlerExtension
{
  public static final int TAB_DOCUMENT = 0;
  public static final int TAB_FOLDER = 1;
  public static final int TAB_MAIL = 2;
  private static Stapling singleton;
  private final OKMStaplingServiceAsync staplingService = (OKMStaplingServiceAsync)GWT.create(OKMStaplingService.class);
  private ToolBarButton buttonStart;
  private ToolBarButton buttonStop;
  public TabDocumentStapling tabDocument;
  public ToolBar tBar;
  public TabFolderStapling tabFolder;
  public TabMailStapling tabMail;
  private boolean enabled = false;
  private boolean wasEnabled = false;
  private int actualWorkspace = 0;
  private String groupId = "";
  public String firstUUID = "";
  private String firstType = "";
  private String groupIdMarkedToDelete = "";
  private GWTStapleGroup actualStapleGroup = new GWTStapleGroup();
  private List<Button> addButtonList = new ArrayList<Button>();
  private List<Button> deleteButtonList = new ArrayList<Button>();
  private List<Button> downloadButtonList = new ArrayList<Button>();
  private ConfirmPopup confirmPopup;
  private int selectedPanel = 1;
  public Stapling() {
	  singleton = this;
      this.confirmPopup = new ConfirmPopup();
      this.confirmPopup.setWidth("300px");
      this.confirmPopup.setHeight("125px");
      this.confirmPopup.setStyleName("okm-Popup");
      this.confirmPopup.addStyleName("okm-DisableSelect");
      this.buttonStart = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.downloadPdf()), GeneralComunicator.i18nExtension("stapling document title"), new ClickHandler()
      {
        public void onClick(ClickEvent event)
        {
          if (true) {
            String docTypeTemp = "";
            String uuidTemp = Stapling.this.getUuid();
            String nameTemp = Stapling.this.getName();
            switch (Stapling.this.selectedPanel) {
            case 0:
              docTypeTemp = "openkm:document";
              break;
            case 1:
              docTypeTemp = "openkm:folder";
              break;
            case 2:
              docTypeTemp = "openkm:mail";
            }

            final String docType = docTypeTemp;
            final String uuid = uuidTemp;
            final String name = nameTemp;

            if (Stapling.this.groupId.equals(""))
            {
              if (Stapling.this.firstUUID.equals("")) {
                Stapling.this.firstUUID = uuid;
                Stapling.this.firstType = docType;
                GeneralComunicator.setStatus(GeneralComunicator.i18nExtension("stapling.status.started"));

                Stapling.this.buttonStart.enable(false);
                Stapling.this.buttonStart.evaluateShowIcons();
                Stapling.this.buttonStop.setVisible(true);
                Stapling.this.enableAddButtons(false);
              } else if (!Stapling.this.firstUUID.equals(uuid)) {
                Stapling.this.staplingService.create(GeneralComunicator.getUser(), Stapling.this.firstUUID, Stapling.this.firstType, uuid, docType, "", new AsyncCallback<String>() {
                  public void onSuccess(String result)
                  {
                    Stapling.this.groupId = result;
                    Stapling.this.buttonStart.enable(false);
                    Stapling.this.buttonStart.evaluateShowIcons();
                    if (docType.equals("openkm:folder"))
                      Stapling.this.refresh(Stapling.this.tabFolder.getTable(), uuid);
                    else if (docType.equals("openkm:document")) {
                      Stapling.this.refresh(Stapling.this.tabDocument.getTable(), uuid);
                    }
                    else if (docType.equals("openkm:mail")) {
                      Stapling.this.refresh(Stapling.this.tabMail.getTable(), uuid);
                    }
                  }

                  public void onFailure(Throwable caught)
                  {
                    GeneralComunicator.showError("create", caught);
                  }
                });
              }
            }
            else Stapling.this.staplingService.add(Stapling.this.groupId, uuid, docType, new AsyncCallback<Object>()
              {
                public void onSuccess(Object result) {
                  GeneralComunicator.setStatus(GeneralComunicator.i18nExtension("stapling done") + " - " + name);
                  Stapling.this.buttonStart.enable(false);
                  Stapling.this.buttonStart.evaluateShowIcons();
                  if (docType.equals("openkm:folder"))
                    Stapling.this.refresh(Stapling.this.tabFolder.getTable(), uuid);
                  else if (docType.equals("openkm:document"))
                    Stapling.this.refresh(Stapling.this.tabDocument.getTable(), uuid);
                  else if (docType.equals("openkm:mail"))
                    Stapling.this.refresh(Stapling.this.tabMail.getTable(), uuid);
                }

                public void onFailure(Throwable caught)
                {
                  GeneralComunicator.showError("add", caught);
                }
              });
          }
        }
      });
      Stapling.this.buttonStart.enable(true);

      this.buttonStop = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.checkin()), GeneralComunicator.i18nExtension("stapling.document.stop.title"), new ClickHandler()
      {
        public void onClick(ClickEvent event)
        {
          Stapling.this.stopStapling();
        }
      });
      this.buttonStop.setStyleName("okm-ToolBar-button");
      this.buttonStop.setVisible(false);

      this.tabDocument = new TabDocumentStapling();
      this.tabDocument.setSize("100%", "100%");

      this.tabFolder = new TabFolderStapling();
      this.tabFolder.setSize("100%", "100%");

      this.tabMail = new TabMailStapling();
      this.tabMail.setSize("100%", "100%");
      
      this.tBar= new ToolBar();
    }
  

  private String getUuid()
  {
    switch (this.selectedPanel) {
    case 0:
      return TabDocumentComunicator.getDocument().getUuid();
    case 1:
      return TabFolderComunicator.getFolder().getUuid();
    case 2:
      return TabMailComunicator.getMail().getUuid();
    }

    return null;
  }

  private String getName()
  {
    switch (this.selectedPanel) {
    case 0:
      return TabDocumentComunicator.getDocument().getName();
    case 1:
      return TabFolderComunicator.getFolder().getName();
    case 2:
      return TabMailComunicator.getMail().getSubject();
    }

    return null;
  }

  public static Stapling get()
  {
    return singleton;
  }

  public List<Object> getExtensions()
  {
    List<Object> extensions = new ArrayList<Object>();
    extensions.add(singleton);
    extensions.add(this.buttonStart);
    extensions.add(this.buttonStop);
    extensions.add(this.tabDocument);
    extensions.add(this.tabFolder);
    extensions.add(this.tabMail);
    return extensions;
  }

  public void onChange(HasDocumentEvent.DocumentEventConstant event)
  {
    if (event.equals(HasDocumentEvent.DOCUMENT_CHANGED)) {
      this.selectedPanel = 0;
      if ((NavigatorComunicator.isTaxonomyShown()) || (NavigatorComunicator.isCategoriesShown()) || (NavigatorComunicator.isThesaurusShown()))
      {
        refresh(this.tabDocument.getTable(), getUuid());
      } else {
        if (this.buttonStop.isVisible()) {
          enableAddButtons(true);
          stopStapling();
        }
        this.buttonStop.enable(false);
        this.buttonStart.evaluateShowIcons();
        this.tabDocument.getTable().removeAllRows();
      }
    } else if (event.equals(HasDocumentEvent.DOCUMENT_DELETED)) {
      this.staplingService.removeAllStapleByUuid(getUuid(), new AsyncCallback<Object>()
      {
        public void onSuccess(Object result)
        {
        }

        public void onFailure(Throwable caught) {
          GeneralComunicator.showError("removeAllStapleByUuid", caught);
        }
      });
    }
  }

  public void onChange(HasMailEvent.MailEventConstant event)
  {
    if (event.equals(HasMailEvent.MAIL_CHANGED)) {
      this.selectedPanel = 2;
      if ((NavigatorComunicator.isTaxonomyShown()) || (NavigatorComunicator.isCategoriesShown()) || (NavigatorComunicator.isThesaurusShown()))
      {
        refresh(this.tabMail.getTable(), getUuid());
      } else {
        if (this.buttonStop.isVisible()) {
          enableAddButtons(true);
          stopStapling();
        }
        this.buttonStop.enable(false);
        this.buttonStart.evaluateShowIcons();
        this.tabDocument.getTable().removeAllRows();
      }
    } else if (event.equals(HasMailEvent.MAIL_DELETED)) {
      this.staplingService.removeAllStapleByUuid(getUuid(), new AsyncCallback<Object>()
      {
        public void onSuccess(Object result)
        {
        }

        public void onFailure(Throwable caught) {
          GeneralComunicator.showError("removeAllStapleByUuid", caught);
        }
      });
    }
  }

  public void onChange(HasFolderEvent.FolderEventConstant event)
  {
    if (event.equals(HasFolderEvent.FOLDER_CHANGED)) {
      this.selectedPanel = 1;

      if ((NavigatorComunicator.isTaxonomyShown()) || (NavigatorComunicator.isCategoriesShown()) || (NavigatorComunicator.isThesaurusShown()))
      {
        refresh(this.tabFolder.getTable(), getUuid());
      } else {
        if (this.buttonStop.isVisible()) {
          this.buttonStop.setVisible(false);
          stopStapling();
        }
        this.buttonStop.enable(false);
        this.buttonStart.evaluateShowIcons();
        this.tabFolder.getTable().removeAllRows();
      }
    } else if (event.equals(HasFolderEvent.FOLDER_DELETED)) {
      this.staplingService.removeAllStapleByUuid(getUuid(), new AsyncCallback<Object>()
      {
        public void onSuccess(Object result)
        {
        }

        public void onFailure(Throwable caught) {
          GeneralComunicator.showError("removeAllStapleByUuid", caught);
        }
      });
    }
  }

  public void onChange(HasNavigatorEvent.NavigatorEventConstant event)
  {
    if (event.equals(HasNavigatorEvent.STACK_CHANGED))
      if ((!NavigatorComunicator.isTaxonomyShown()) && (!NavigatorComunicator.isCategoriesShown()) && (!NavigatorComunicator.isThesaurusShown()))
      {
        if (this.buttonStop.isVisible()) {
          this.buttonStop.setVisible(false);
          stopStapling();
        }
        this.buttonStop.enable(false);
        this.buttonStart.evaluateShowIcons();
      } 
  }

  public void onChange(HasWorkspaceEvent.WorkspaceEventConstant event)
  {
    if (event.equals(HasWorkspaceEvent.STACK_CHANGED))
    {
      if (WorkspaceComunicator.getSelectedTab() != 0) {
        if (this.actualWorkspace == 0) {
          this.wasEnabled = this.enabled;
        }
        this.enabled = false;
        stopStapling();
        this.buttonStart.evaluateShowIcons();
      } else {
        this.enabled = this.wasEnabled;
        this.buttonStart.evaluateShowIcons();
      }
      this.actualWorkspace = WorkspaceComunicator.getSelectedTab();
    }
  }

  public String getGroupId()
  {
    return this.groupId;
  }

  public void refreshFolder(String uuid)
  {
    refresh(this.tabFolder.getTable(), uuid);
  }

  public void refreshDocument(String uuid)
  {
    refresh(this.tabDocument.getTable(), uuid);
  }

  public void refreshMail(String uuid)
  {
    refresh(this.tabMail.getTable(), uuid);
  }

  public void refresh(final FlexTable table, final String uuid)
  {
    this.staplingService.getAll(uuid, new AsyncCallback<List<GWTStapleGroup>>()
    {
      public void onSuccess(List<GWTStapleGroup> result) {
        Stapling.this.actualStapleGroup = new GWTStapleGroup();
        table.removeAllRows();
        Stapling.this.addButtonList = new ArrayList<Button>();
        Stapling.this.deleteButtonList = new ArrayList<Button>();
        Stapling.this.downloadButtonList = new ArrayList<Button>();
        for (final GWTStapleGroup sg : result)
        {
          if (Stapling.get().getGroupId().equals(String.valueOf(sg.getId()))) {
            Stapling.this.actualStapleGroup = sg;
          }

          table.setHTML(table.getRowCount(), 0, "&nbsp;");
          int row = table.getRowCount();
          if (sg.getStaples().size() > 0) {
            HorizontalPanel hPanel = new HorizontalPanel();
            HTML groupTitle = new HTML("<b>" + sg.getName() + "</b>");

            hPanel.add(groupTitle);
            HTML space = new HTML("&nbsp;");
            hPanel.add(space);
            hPanel.setCellWidth(space, "10px");

            if (sg.getUser().equals(GeneralComunicator.getUser())) {
              Button addButton = new Button(GeneralComunicator.i18n("button.add"));
              addButton.setVisible(!Stapling.this.buttonStop.isVisible());

              addButton.addClickHandler(new ClickHandler()
              {
                public void onClick(ClickEvent event) {
                  Stapling.this.groupId = String.valueOf(sg.getId());
                  Window.alert("Relation" +" "+ groupId + " enabled");
                }
              });
              Stapling.this.addButtonList.add(addButton);
              hPanel.add(addButton);
              hPanel.setCellVerticalAlignment(addButton, HasAlignment.ALIGN_MIDDLE);
              addButton.setStyleName("okm-Button");

              HTML space2 = new HTML("");
              hPanel.add(space2);
              hPanel.setCellWidth(space2, "5px");

              Button deleteButton = new Button(GeneralComunicator.i18n("button.delete"));
              deleteButton.setVisible(!Stapling.this.buttonStop.isVisible());

              deleteButton.addClickHandler(new ClickHandler()
              {
                public void onClick(ClickEvent event) {
                  Stapling.this.groupIdMarkedToDelete = String.valueOf(sg.getId());
                  Stapling.this.confirmPopup.setConfirm(39);
                  Stapling.this.confirmPopup.center();
                }
              });
              Stapling.this.deleteButtonList.add(deleteButton);
              hPanel.add(deleteButton);
              hPanel.setCellVerticalAlignment(deleteButton, HasAlignment.ALIGN_MIDDLE);
              deleteButton.setStyleName("okm-Button");

              HTML space3 = new HTML("");
              hPanel.add(space3);
              hPanel.setCellWidth(space3, "5px");
            }

            Button downloadButton = new Button(GeneralComunicator.i18nExtension("Download as zip"));
            downloadButton.setVisible(!Stapling.this.buttonStop.isVisible());

            downloadButton.addClickHandler(new ClickHandler()
            {
              public void onClick(ClickEvent event) { 
            	Stapling.this.groupId = String.valueOf(sg.getId());
                String url = RPCService.StaplingDownloadService + "?sgName=" + sg.getName()+ "?sgId=" + sg.getId();
                GeneralComunicator.extensionCallOwnDownload(url);
              }
            });
            Stapling.this.downloadButtonList.add(downloadButton);
            hPanel.add(downloadButton); 
            hPanel.setCellVerticalAlignment(downloadButton, HasAlignment.ALIGN_MIDDLE);
            downloadButton.setStyleName("okm-Button");

            hPanel.setCellWidth(space, "20");
            hPanel.setCellVerticalAlignment(groupTitle, HasAlignment.ALIGN_MIDDLE);
            table.setWidget(row, 0, hPanel);
            table.getFlexCellFormatter().setColSpan(row, 0, 5);
            table.getFlexCellFormatter().setHorizontalAlignment(row, 0, HasAlignment.ALIGN_CENTER);
            for (GWTStaple st : sg.getStaples()) {
              if (st.getType().equals("openkm:document")) {
                StapleTableManager.addDocument(table, st, uuid, sg.getUser().equals(GeneralComunicator.getUser()));
              }
              else if (st.getType().equals("openkm:folder")) {
                StapleTableManager.addFolder(table, st, uuid, sg.getUser().equals(GeneralComunicator.getUser()));
              }
              else if (st.getType().equals("openkm:mail")) {
                StapleTableManager.addMail(table, st, uuid, sg.getUser().equals(GeneralComunicator.getUser()));
              }
            }
          }
        }
      }

      public void onFailure(Throwable caught)
      {
    	  Window.alert(caught.getMessage());
        GeneralComunicator.showError("getAll", caught);
      }
    });
  }

  public boolean isIntoStaplingInGroup(String uuid)
  {
    boolean found = false;

    for (GWTStaple gst : this.actualStapleGroup.getStaples()) {
      String uuidTemp = "";
      if (gst.getType().equals("openkm:document"))
        uuidTemp = gst.getDoc().getUuid();
      else if (gst.getType().equals("openkm:folder"))
        uuidTemp = gst.getFolder().getUuid();
      else if (gst.getType().equals("openkm:mail")) {
        uuidTemp = gst.getMail().getUuid();
      }
      if (uuidTemp.equals(uuid)) {
        found = true;
        break;
      }
    }

    return found;
  }

  private void stopStapling()
  {
    if ((!this.firstUUID.equals("")) || (!this.groupId.equals(""))) {
      this.firstUUID = "";
      this.firstType = "";
      this.groupId = "";
      GeneralComunicator.setStatus(GeneralComunicator.i18nExtension("stapling.status.finished"));
      if (WorkspaceComunicator.getSelectedWorkspace() == 0)
        this.buttonStart.enable(true);
      else {
        this.buttonStart.enable(false);
      }
      this.buttonStart.evaluateShowIcons();
      this.buttonStop.setVisible(false);
      enableAddButtons(true);
    }
  }


  public void enableAddButtons(boolean enable)
  {
    for (Button button : this.addButtonList) {
      button.setVisible(enable);
    }

    for (Button button : this.deleteButtonList) {
      button.setVisible(enable);
    }

    for (Button button : this.downloadButtonList)
      button.setVisible(enable);
  }

  public void onChange(HasLanguageEvent.LanguageEventConstant event)
  {
    if (event.equals(HasLanguageEvent.LANGUAGE_CHANGED)) {
      this.buttonStart.setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
      this.buttonStop.setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
      for (Button button : this.addButtonList) {
        button.setTitle(GeneralComunicator.i18n("button.add"));
      }
      for (Button button : this.deleteButtonList) {
        button.setTitle(GeneralComunicator.i18n("button.delete"));
      }
      for (Button button : this.downloadButtonList) {
        button.setTitle(GeneralComunicator.i18nExtension("button.download"));
      }
      this.tabDocument.langRefresh();
      this.tabFolder.langRefresh();
      this.tabMail.langRefresh();
      this.confirmPopup.langRefresh();
    }
  }

  public void deleteStaplingGroup()
  {
    if (!this.groupIdMarkedToDelete.equals("")) {
      this.staplingService.remove(this.groupIdMarkedToDelete, new AsyncCallback<Object>()
      {
        public void onSuccess(Object result) {
        //	tBar.executeRefresh();
        	//Window.alert("Ref");
         Stapling.this.refresh(Stapling.this.tabFolder.getTable(), Stapling.this.getUuid());
     	 //Main.get().mainPanel.desktop.browser.fileBrowser.refresh(Main.get().activeFolderTree.getActualPath());
        }

        public void onFailure(Throwable caught)
        {
          GeneralComunicator.showError("remove", caught);
        }
      });
    }
    this.groupIdMarkedToDelete = "";
    tBar.executeRefresh();
  }

  public static boolean isRegistered(List<String> uuidList)
  {
    return uuidList.contains("25af39c0-580f-431c-8852-0b6430b4dc1d");
  }

  private class ToolBarButton extends ToolBarButtonExtension
  {
    private boolean enabled = true;

    public ToolBarButton(Image image, String title, ClickHandler handler) {
      super(image, title, handler);
    }

    public void checkPermissions(GWTFolder folder, GWTFolder folderParent, int originPanel)
    {
      this.enabled = true;
      evaluateShowIcons();
    }

    public void checkPermissions(GWTDocument doc, GWTFolder folder)
    {
      this.enabled = true;
      evaluateShowIcons();
    }

    public void checkPermissions(GWTMail mail, GWTFolder folder)
    {
      this.enabled = true;
      evaluateShowIcons();
    }

    public void enable(boolean enable)
    {
      this.enabled = enable;
    }

    public boolean isEnabled()
    {
      return true;
    }

    public void evaluateShowIcons()
    {
      if (this.enabled)
        enableStapling();
      else
        disableStapling();
    }

    private void enableStapling()
    {
      setStyleName("okm-ToolBar-button");
      setResource(OKMBundleResources.INSTANCE.downloadPdf());
      setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
    }

    private void disableStapling()
    {
      setStyleName("okm-ToolBar-button-disabled");
      setResource(OKMBundleResources.INSTANCE.downloadPdf());
      setTitle(GeneralComunicator.i18nExtension("stapling.document.title"));
    }
  }
}
