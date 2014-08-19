package com.ikon.frontend.client.widget.properties;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.bean.GWTFolder;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.extension.widget.tabfolder.TabFolderExtension;

public class TabFolderStapling extends TabFolderExtension
{
  private ScrollPanel scrollPanel;
  private VerticalPanel vPanel;
  private String title = "";
  private FlexTable table;

  public TabFolderStapling()
  {
    this.title = GeneralComunicator.i18nExtension("stapling");

    this.vPanel = new VerticalPanel();
    this.scrollPanel = new ScrollPanel(this.vPanel);
    this.table = new FlexTable();
    this.vPanel.add(this.table);

    initWidget(this.scrollPanel);
  }

  public String getTabText()
  {
    return this.title;
  }

  public void langRefresh()
  {
    this.title = GeneralComunicator.i18nExtension("stapling");
  }

  public FlexTable getTable()
  {
    return this.table;
  }

  public void set(GWTFolder doc)
  {
  }

  public void setVisibleButtons(boolean visible)
  {
  }
}