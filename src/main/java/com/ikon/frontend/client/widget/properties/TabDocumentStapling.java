package com.ikon.frontend.client.widget.properties;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.extension.widget.tabdocument.TabDocumentExtension;

public class TabDocumentStapling extends TabDocumentExtension
{
  private ScrollPanel scrollPanel;
  private VerticalPanel vPanel;
  private String title = "";
  private FlexTable table;

  public TabDocumentStapling()
  {
    this.title = "Related Documents";
    //GeneralComunicator.i18nExtension("stapling");

    this.vPanel = new VerticalPanel();
    this.scrollPanel = new ScrollPanel(this.vPanel);
    this.table = new FlexTable();
    this.vPanel.add(this.table);

    initWidget(this.scrollPanel);
    refresh();
  }

  private void refresh() {
	// TODO Auto-generated method stub
	
}

public String getTabText()
  {
    return this.title;
  }

  public void langRefresh()
  {
    this.title = "Related Documents";
    //GeneralComunicator.i18nExtension("stapling");
  }

  public FlexTable getTable()
  {
    return this.table;
  }
}