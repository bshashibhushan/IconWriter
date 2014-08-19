package com.ikon.frontend.client.widget.properties;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.frontend.client.bean.GWTMail;
import com.ikon.frontend.client.extension.comunicator.GeneralComunicator;
import com.ikon.frontend.client.extension.widget.tabmail.TabMailExtension;

public class TabMailStapling extends TabMailExtension
{
  private ScrollPanel scrollPanel;
  private VerticalPanel vPanel;
  private String title = "";
  private FlexTable table;

  public TabMailStapling()
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

  public void set(GWTMail mail)
  {
  }

  public void setVisibleButtons(boolean visible)
  {
  }
}