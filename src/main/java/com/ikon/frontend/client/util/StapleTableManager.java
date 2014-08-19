package com.ikon.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.bean.GWTStaple;
import com.ikon.frontend.client.service.OKMStaplingService;
import com.ikon.frontend.client.service.OKMStaplingServiceAsync;
import com.ikon.frontend.client.bean.GWTDocument;
import com.ikon.frontend.client.bean.GWTFolder;
import com.ikon.frontend.client.bean.GWTMail;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.extension.comunicator.TabDocumentComunicator;
import com.ikon.frontend.client.util.OKMBundleResources;
import com.ikon.frontend.client.util.Util;

public class StapleTableManager
{
  private static final OKMStaplingServiceAsync staplingService = (OKMStaplingServiceAsync)GWT.create(OKMStaplingService.class);

  public static void addDocument(FlexTable table, final GWTStaple staple, final String uuid, boolean enableDelete)
  {
    int row = table.getRowCount();
    final GWTDocument doc = staple.getDoc();

    if (doc.isCheckedOut())
      table.setHTML(row, 0, Util.imageItemHTML("img/icon/edit.png"));
    else if (doc.isLocked())
      table.setHTML(row, 0, Util.imageItemHTML("img/icon/lock.gif"));
    else {
      table.setHTML(row, 0, "&nbsp;");
    }

    if (doc.isSubscribed()) {
      table.setHTML(row, 0, table.getHTML(row, 0) + Util.imageItemHTML("img/icon/subscribed.gif"));
    }

    if (doc.isHasNotes()) {
      table.setHTML(row, 0, table.getHTML(row, 0) + Util.imageItemHTML("img/icon/note.gif"));
    }

    table.setHTML(row, 1, Util.mimeImageHTML(doc.getMimeType()));
    Anchor anchor = new Anchor();
    anchor.setHTML(doc.getName());
    anchor.addClickHandler(new ClickHandler()
    {
      public void onClick(ClickEvent event) {
        String docPath = doc.getPath();
        String path = docPath.substring(0, docPath.lastIndexOf("/"));
        GeneralComunicator.openPath(path, doc.getPath());
      }
    });
    anchor.setStyleName("okm-KeyMap-ImageHover");
    table.setWidget(row, 2, anchor);
    table.setHTML(row, 3, Util.formatSize(doc.getActualVersion().getSize()));

    if (enableDelete) {
      Image delete = new Image(OKMBundleResources.INSTANCE.deleteIcon());
      delete.setStyleName("okm-KeyMap-ImageHover");
      delete.addClickHandler(new ClickHandler()
      {
        public void onClick(ClickEvent event) {
          StapleTableManager.staplingService.removeStaple(String.valueOf(staple.getId()), new AsyncCallback<Object>()
          {
            public void onSuccess(Object result) {
              	GWTDocument doc1 = TabDocumentComunicator.getDocument();
            	Main.get().mainPanel.desktop.browser.tabMultiple.enableTabDocument();
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setProperties(doc1);
				Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(doc1,Main.get().activeFolderTree.getFolder());                           }
            public void onFailure(Throwable caught)
            {
              GeneralComunicator.showError("remove", caught);
            }
          });
        }
      });
      table.setWidget(row, 4, delete);
    } else {
      table.setHTML(row, 4, "");
    }

    table.getCellFormatter().setWidth(row, 0, "60");
    table.getCellFormatter().setWidth(row, 1, "25");
    table.getCellFormatter().setWidth(row, 4, "25");

    table.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
    table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
    table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
    table.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
    table.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
  }

  public static void addFolder(FlexTable table, final GWTStaple staple, final String uuid, boolean enableDelete)
  {
    int row = table.getRowCount();
    final GWTFolder folder = staple.getFolder();

    if (folder.isSubscribed())
      table.setHTML(row, 0, Util.imageItemHTML("img/icon/subscribed.gif"));
    else {
      table.setHTML(row, 0, "&nbsp;");
    }

    if ((folder.getPermissions() & 0x2) == 2) {
      if (folder.isHasChildren())
        table.setHTML(row, 1, Util.imageItemHTML("img/menuitem_childs.gif"));
      else {
        table.setHTML(row, 1, Util.imageItemHTML("img/menuitem_empty.gif"));
      }
    }
    else if (folder.isHasChildren())
      table.setHTML(row, 1, Util.imageItemHTML("img/menuitem_childs_ro.gif"));
    else {
      table.setHTML(row, 1, Util.imageItemHTML("img/menuitem_empty_ro.gif"));
    }

    Anchor anchor = new Anchor();
    anchor.setHTML(folder.getName());
    anchor.addClickHandler(new ClickHandler()
    {
      public void onClick(ClickEvent arg0) {
        GeneralComunicator.openPath(folder.getPath(), null);
      }
    });
    anchor.setStyleName("okm-KeyMap-ImageHover");
    table.setWidget(row, 2, anchor);
    table.setHTML(row, 3, "&nbsp;");

    if (enableDelete) {
      Image delete = new Image(OKMBundleResources.INSTANCE.deleteIcon());
      delete.setStyleName("okm-KeyMap-ImageHover");
      delete.addClickHandler(new ClickHandler()
      {
        public void onClick(ClickEvent event) {
          StapleTableManager.staplingService.removeStaple(String.valueOf(staple.getId()), new AsyncCallback<Object>()
          {
            public void onSuccess(Object result) {
            	Window.alert(result.getClass().toString());
            }

            public void onFailure(Throwable caught)
            {
              GeneralComunicator.showError("remove", caught);
            }
          });
        }
      });
      table.setWidget(row, 4, delete);
    } else {
      table.setHTML(row, 4, "");
    }

    table.getCellFormatter().setWidth(row, 0, "60");
    table.getCellFormatter().setWidth(row, 1, "25");
    table.getCellFormatter().setWidth(row, 4, "25");

    table.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
    table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
    table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
    table.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
    table.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
  }

  public static void addMail(FlexTable table, final GWTStaple staple, final String uuid, boolean enableDelete)
  {
    int row = table.getRowCount();
    final GWTMail mail = staple.getMail();

    table.setHTML(row, 0, "&nbsp;");

    if (mail.getAttachments().size() > 0)
      table.setHTML(row, 1, Util.imageItemHTML("img/email_attach.gif"));
    else {
      table.setHTML(row, 1, Util.imageItemHTML("img/email.gif"));
    }

    Anchor anchor = new Anchor();
    anchor.setHTML(mail.getSubject());
    anchor.addClickHandler(new ClickHandler()
    {
      public void onClick(ClickEvent arg0) {
        String docPath = mail.getPath();
        String path = docPath.substring(0, docPath.lastIndexOf("/"));
        GeneralComunicator.openPath(path, docPath);
      }
    });
    anchor.setStyleName("okm-KeyMap-ImageHover");
    table.setWidget(row, 2, anchor);
    table.setHTML(row, 3, Util.formatSize(mail.getSize()));

    if (enableDelete) {
      Image delete = new Image(OKMBundleResources.INSTANCE.deleteIcon());
      delete.setStyleName("okm-KeyMap-ImageHover");
      delete.addClickHandler(new ClickHandler()
      {
        public void onClick(ClickEvent event) {
          StapleTableManager.staplingService.removeStaple(String.valueOf(staple.getId()), new AsyncCallback<Object>()
          {
            public void onSuccess(Object result) {
            	Window.alert(result.getClass().toString());
            }

            public void onFailure(Throwable caught)
            {
              GeneralComunicator.showError("remove", caught);
            }
          });
        }
      });
      table.setWidget(row, 4, delete);
    } else {
      table.setHTML(row, 4, "");
    }

    table.getCellFormatter().setWidth(row, 0, "60");
    table.getCellFormatter().setWidth(row, 1, "25");
    table.getCellFormatter().setWidth(row, 4, "25");

    table.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
    table.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
    table.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
    table.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
    table.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
  }
}