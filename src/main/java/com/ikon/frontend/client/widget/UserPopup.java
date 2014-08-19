/**
 * openkm, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2013 Paco Avila & Josep Llort
 * 
 * No bytes were intentionally harmed during the development of this application.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ikon.frontend.client.widget;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.ikon.dao.bean.MailAccount;
import com.ikon.frontend.client.Main;
import com.ikon.frontend.client.bean.GWTTestMail;
import com.ikon.frontend.client.bean.GWTWorkspace;
import com.ikon.frontend.client.service.OKMGeneralService;
import com.ikon.frontend.client.service.OKMGeneralServiceAsync;
import com.ikon.frontend.client.service.OKMWorkspaceService;
import com.ikon.frontend.client.service.OKMWorkspaceServiceAsync;

/**
 * User popup
 * 
 * @author jllort
 */
public class UserPopup extends DialogBox implements ClickHandler {
	private final OKMWorkspaceServiceAsync workspaceService = (OKMWorkspaceServiceAsync) GWT.create(OKMWorkspaceService.class);
	private final OKMGeneralServiceAsync generalService = (OKMGeneralServiceAsync) GWT.create(OKMGeneralService.class);
	
	private VerticalPanel vPanel;
	private FlexTable userFlexTable;
	private FlexTable mailFlexTable;
	private HTML userId;
	private HTML userName;
	private HTML userPassword;
	private HTML userMail;
	private HTML userRoles;
	private HTML mailHost;
	private HTML mailUser;
	private HTML mailPassword;
	private HTML mailFolder;
	private HTML mailProtocolLabel;
	private TextBox hostText;
	private TextBox mailUserText;
	private TextBox mailFolderText;
	private ListBox mailProtocol;
	private HTML userNameText;
	private PasswordTextBox userPasswordText;
	private PasswordTextBox userPasswordTextVerify;
	private TextBox userMailText;
	private VerticalPanel rolesPanel;
	private PasswordTextBox mailUserPasswordText;
	private Button update;
	private Button cancel;
	private Button delete;
	private Button test;
	private HorizontalPanel hPanel;
	private HTML passwordError;
	private HTML passwordValidationError;
	private HTML mailPassordError;
	private HTML mailError;
	private HTML mailTestError;
	private HTML mailTestOK;
	private GroupBoxPanel userGroupBoxPanel;
	private GroupBoxPanel mailGroupBoxPanel;
	
	/**
	 * User popup
	 */
	public UserPopup() {
		
		// Establishes auto-close when click outside
		super(false, true);
		int left = (Window.getClientWidth() - 400) / 2;
		int top = (Window.getClientHeight() - 220) / 2;
		
		vPanel = new VerticalPanel();
		userFlexTable = new FlexTable();
		mailFlexTable = new FlexTable();
		
		userGroupBoxPanel = new GroupBoxPanel();
		userGroupBoxPanel.setCaption(Main.i18n("user.preferences.user.data"));
		userGroupBoxPanel.add(userFlexTable);
		
		mailGroupBoxPanel = new GroupBoxPanel();
		mailGroupBoxPanel.setCaption(Main.i18n("user.preferences.mail.data"));
		mailGroupBoxPanel.add(mailFlexTable);
		
		userId = new HTML(Main.i18n("user.preferences.user"));
		userName = new HTML(Main.i18n("user.preferences.name"));
		userPassword = new HTML(Main.i18n("user.preferences.password"));
		userMail = new HTML(Main.i18n("user.preferences.mail"));
		userRoles = new HTML(Main.i18n("user.preferences.roles"));
		mailHost = new HTML(Main.i18n("user.preferences.mail.host"));
		mailUser = new HTML(Main.i18n("user.preferences.mail.user"));
		mailPassword = new HTML(Main.i18n("user.preferences.mail.user.password"));
		mailFolder = new HTML(Main.i18n("user.preferences.mail.folder"));
		mailProtocolLabel = new HTML(Main.i18n("user.preferences.mail.protocol"));
		mailProtocol = new ListBox();
		userPasswordText = new PasswordTextBox();
		userPasswordTextVerify = new PasswordTextBox();
		userNameText = new HTML("");
		userMailText = new TextBox();
		rolesPanel = new VerticalPanel();
		mailUserPasswordText = new PasswordTextBox();
		passwordError = new HTML(Main.i18n("user.preferences.password.error"));
		passwordValidationError = new HTML("");
		mailPassordError = new HTML(Main.i18n("user.preferences.mail.password.error.void"));
		mailError = new HTML(Main.i18n("user.preferences.mail.error"));
		mailTestError = new HTML(Main.i18n("user.preferences.mail.test.error"));
		mailTestOK = new HTML(Main.i18n("user.preferences.mail.test.ok"));
		
		passwordError.setVisible(false);
		passwordValidationError.setVisible(false);
		mailPassordError.setVisible(false);
		mailError.setVisible(false);
		mailTestError.setVisible(false);
		mailTestOK.setVisible(false);
		
		hostText = new TextBox();
		mailUserText = new TextBox();
		mailFolderText = new TextBox();
		
		mailProtocol.addItem(MailAccount.PROTOCOL_IMAP);
		mailProtocol.addItem(MailAccount.PROTOCOL_IMAPS);
		mailProtocol.addItem(MailAccount.PROTOCOL_POP3);
		mailProtocol.addItem(MailAccount.PROTOCOL_POP3S);
		
		update = new Button(Main.i18n("button.update"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				passwordError.setVisible(false);
				passwordValidationError.setVisible(false);
				mailPassordError.setVisible(false);
				mailError.setVisible(false);
				mailTestError.setVisible(false);
				mailTestOK.setVisible(false);
				// Password always must be equals
				if (!userPasswordText.getText().equals(userPasswordTextVerify.getText())) {
					passwordError.setVisible(true);
					// Case creation
				} else if (Main.get().workspaceUserProperties.getWorkspace().getMailID() < 0
						&& mailUserPasswordText.getText().equals("")
						&& (mailFolderText.getText().length() > 0 || mailUserText.getText().length() > 0 || hostText
								.getText().length() > 0)) {
					mailPassordError.setVisible(true);
					// Case update
				} else if ((mailUserPasswordText.getText().length() > 0 || mailFolderText.getText().length() > 0
						|| mailUserText.getText().length() > 0 || hostText.getText().length() > 0)
						&& !(mailFolderText.getText().length() > 0 && mailUserText.getText().length() > 0 && hostText
								.getText().length() > 0)) {
					mailError.setVisible(true);
				} else {
					final GWTWorkspace workspace = new GWTWorkspace();
					workspace.setUser(Main.get().workspaceUserProperties.getUser());
					workspace.setEmail(userMailText.getText());
					workspace.setMailProtocol(mailProtocol.getItemText(mailProtocol.getSelectedIndex()));
					workspace.setMailFolder(mailFolderText.getText());
					workspace.setMailHost(hostText.getText());
					workspace.setMailUser(mailUserText.getText());
					workspace.setMailPassword(mailUserPasswordText.getText());
					workspace.setPassword(userPasswordText.getText());
					workspace.setMailID(Main.get().workspaceUserProperties.getWorkspace().getMailID());
					// First must validate password
					workspaceService.isValidPassword(userPasswordText.getText(), new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							if (result.equals("")) {
								workspaceService.updateUserWorkspace(workspace, callbackUpdateUserWorkspace);
							} else {
								passwordValidationError.setHTML(result);
								passwordValidationError.setVisible(true);
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("callbackIsValidPassword", caught);
						}
					});
				}
			}
		});
		
		cancel = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		test = new Button(Main.i18n("button.test"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mailTestError.setVisible(false);
				mailTestOK.setVisible(false);
				test.setEnabled(false);
				generalService.testMailConnection(hostText.getText(), mailUserText.getText(),
						mailUserPasswordText.getText(), mailFolderText.getText(), mailProtocol.getItemText(mailProtocol.getSelectedIndex()), new AsyncCallback<GWTTestMail>() {
							@Override
							public void onSuccess(GWTTestMail result) {
								if (!result.isError()) {
									mailTestError.setVisible(false);
									mailTestOK.setVisible(true);
								} else {
									mailTestError.setHTML(Main.i18n("user.preferences.mail.test.error") + "<br>"
											+ result.getErrorMsg());
									mailTestError.setVisible(true);
									mailTestOK.setVisible(false);
								}
								test.setEnabled(true);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								mailTestError.setVisible(false);
								mailTestOK.setVisible(false);
								test.setEnabled(true);
								Main.get().showError("testmailConnection", caught);
							}
						});
			}
		});
		
		delete = new Button(Main.i18n("button.delete"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				long Id = Main.get().workspaceUserProperties.getWorkspace().getMailID();
				
				if (Id >= 0) {
					workspaceService.deleteMailAccount(Id, callbackDeleteMailAccount);
				}
			}
		});
		
		hPanel = new HorizontalPanel();
		hPanel.add(update);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(cancel);
		
		userFlexTable.setCellPadding(0);
		userFlexTable.setCellSpacing(2);
		userFlexTable.setWidth("455");
		
		userFlexTable.setWidget(0, 0, userId);
		userFlexTable.setWidget(1, 0, userName);
		userFlexTable.setWidget(2, 0, userPassword);
		userFlexTable.setWidget(3, 0, userMail);
		userFlexTable.setWidget(4, 0, userRoles);
		
		userFlexTable.setWidget(1, 1, userNameText);
		userFlexTable.setWidget(2, 1, userPasswordText);
		userFlexTable.setWidget(2, 2, userPasswordTextVerify);
		userFlexTable.setWidget(3, 1, userMailText);
		userFlexTable.setWidget(4, 1, rolesPanel);
		
		userFlexTable.getFlexCellFormatter().setVerticalAlignment(4, 0, HasAlignment.ALIGN_TOP);
		userFlexTable.getFlexCellFormatter().setColSpan(3, 1, 2);
		userFlexTable.getFlexCellFormatter().setColSpan(4, 1, 2);
		
		mailFlexTable.setCellPadding(0);
		mailFlexTable.setCellSpacing(2);
		mailFlexTable.setWidth("455");
		
		mailFlexTable.setWidget(1, 0, mailProtocolLabel);
		mailFlexTable.setWidget(2, 0, mailHost);
		mailFlexTable.setWidget(3, 0, mailUser);
		mailFlexTable.setWidget(4, 0, mailPassword);
		mailFlexTable.setWidget(5, 0, mailFolder);
		
		mailFlexTable.setWidget(1, 1, mailProtocol);
		mailFlexTable.setWidget(2, 1, hostText);
		mailFlexTable.setWidget(3, 1, mailUserText);
		mailFlexTable.setWidget(4, 1, mailUserPasswordText);
		mailFlexTable.setWidget(5, 1, mailFolderText);
		mailFlexTable.setWidget(6, 0, new HTML("&nbsp;"));
		mailFlexTable.setWidget(6, 1, delete);
		mailFlexTable.setWidget(6, 2, test);
		
		mailFlexTable.getFlexCellFormatter().setColSpan(1, 1, 1);
		
		
		userMailText.setWidth("275");
		hostText.setWidth("275");
		rolesPanel.setWidth("275");
		userGroupBoxPanel.setWidth("460px");
		mailGroupBoxPanel.setWidth("460px");
		
		vPanel.setWidth("470px");
		vPanel.setHeight("195px");
		
		vPanel.add(new HTML("<br>"));
		vPanel.add(userGroupBoxPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(mailGroupBoxPanel);
		vPanel.add(passwordError);
		vPanel.add(passwordValidationError);
		vPanel.add(mailPassordError);
		vPanel.add(mailError);
		vPanel.add(mailTestError);
		vPanel.add(mailTestOK);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(userGroupBoxPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailGroupBoxPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(passwordError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(passwordValidationError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailPassordError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailTestError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(mailTestOK, HasAlignment.ALIGN_CENTER);
		
		userId.addStyleName("okm-NoWrap");
		userName.addStyleName("okm-NoWrap");
		userPassword.addStyleName("okm-NoWrap");
		userMail.addStyleName("okm-NoWrap");
		mailHost.addStyleName("okm-NoWrap");
		mailUser.addStyleName("okm-NoWrap");
		mailPassword.addStyleName("okm-NoWrap");
		mailFolder.addStyleName("okm-NoWrap");
		userPasswordText.setStyleName("okm-Input");
		userPasswordTextVerify.setStyleName("okm-Input");
		userMailText.setStyleName("okm-Input");
		hostText.setStyleName("okm-Input");
		mailUserText.setStyleName("okm-Input");
		mailUserPasswordText.setStyleName("okm-Input");
		mailFolderText.setStyleName("okm-Input");
		mailProtocolLabel.setStyleName("okm-NoWrap");
		passwordError.setStyleName("okm-Input-Error");
		passwordValidationError.setStyleName("okm-Input-Error");
		mailPassordError.setStyleName("okm-Input-Error");
		mailError.setStyleName("okm-Input-Error");
		mailTestError.setStyleName("okm-Input-Error");
		mailTestOK.setStyleName("okm-Input-Ok");
		update.setStyleName("okm-ChangeButton");
		cancel.setStyleName("okm-NoButton");
		delete.setStyleName("okm-DeleteButton");
		test.setStyleName("okm-Button");
		
		setPopupPosition(left, top);
		
		super.hide();
		setWidget(vPanel);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
	 */
	public void onClick(ClickEvent event) {
		super.hide();
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("user.preferences.label"));
		userId.setHTML(Main.i18n("user.preferences.user"));
		userPassword.setHTML(Main.i18n("user.preferences.password"));
		userMail.setHTML(Main.i18n("user.preferences.mail"));
		mailHost.setHTML(Main.i18n("user.preferences.mail.host"));
		mailUser.setHTML(Main.i18n("user.preferences.mail.user"));
		mailPassword.setHTML(Main.i18n("user.preferences.mail.user.password"));
		mailFolder.setHTML(Main.i18n("user.preferences.mail.folder"));
		mailProtocolLabel.setHTML(Main.i18n("user.preferences.mail.protocol"));
		passwordError.setHTML(Main.i18n("user.preferences.password.error"));
		passwordValidationError.setHTML("");
		mailPassordError.setHTML(Main.i18n("user.preferences.mail.password.error.void"));
		mailError.setHTML(Main.i18n("user.preferences.mail.error"));
		mailTestError.setHTML(Main.i18n("user.preferences.mail.error"));
		mailTestOK.setHTML(Main.i18n("user.preferences.mail.ok"));
		update.setText(Main.i18n("button.update"));
		cancel.setText(Main.i18n("button.cancel"));
		delete.setText(Main.i18n("button.delete"));
		test.setText(Main.i18n("button.test"));
		userGroupBoxPanel.setCaption(Main.i18n("user.preferences.user.data"));
		mailGroupBoxPanel.setCaption(Main.i18n("user.preferences.mail.data"));
	}
	
	/**
	 * Reset values
	 */
	private void reset() {
		userPasswordText.setText("");
		userPasswordTextVerify.setText("");
		mailUserPasswordText.setText("");
	}
	
	/**
	 * Show the popup user preferences
	 * 
	 */
	public void show() {
		setText(Main.i18n("user.preferences.label"));
		GWTWorkspace workspace = Main.get().workspaceUserProperties.getWorkspace();
		
		reset();
		hostText.setText(workspace.getMailHost());
		mailUserText.setText(workspace.getMailUser());
		mailFolderText.setText(workspace.getMailFolder());
		userFlexTable.setText(0, 1, workspace.getUser().getId());
		userFlexTable.getFlexCellFormatter().setColSpan(0, 1, 2);
		userNameText.setText(workspace.getUser().getUsername());
		userMailText.setText(workspace.getEmail());
		
		for (Iterator<String> it = workspace.getRoleList().iterator(); it.hasNext();) {
			rolesPanel.add(new HTML(it.next()));
		}
		
		passwordError.setVisible(false);
		passwordValidationError.setVisible(false);
		mailPassordError.setVisible(false);
		mailError.setVisible(false);
		mailTestError.setVisible(false);
		mailTestOK.setVisible(false);
		
		if (workspace.isChangePassword()) {
			userMail.setVisible(true);
			userMailText.setVisible(true);
			userPassword.setVisible(true);
			userPasswordText.setVisible(true);
			userPasswordTextVerify.setVisible(true);
		} else {
			userMail.setVisible(true);
			userMailText.setVisible(false);
			userPassword.setVisible(false);
			userPasswordText.setVisible(false);
			userPasswordTextVerify.setVisible(false);
		}
		
		// Enables delete button only if there's some mail server configured to be removed
		if (workspace.getMailID() >= 0) {
			delete.setVisible(true);
		} else {
			delete.setVisible(false);
		}
		
		super.show();
	}
	
	/**
	 * Call back update user workspace data
	 */
	final AsyncCallback<Object> callbackUpdateUserWorkspace = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().workspaceUserProperties.refreshUserWorkspace(); // Refreshing workspace saved values
			hide();
		}
		
		public void onFailure(Throwable caught) {
			Main.get().showError("callbackUpdateUserWorkspace", caught);
		}
	};
	
	/**
	 * Call back delete mail account
	 */
	final AsyncCallback<Object> callbackDeleteMailAccount = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().workspaceUserProperties.getUserWorkspace(); // Refreshing workspace saved values
			hostText.setText("");
			mailUserText.setText("");
			mailUserPasswordText.setText("");
			mailFolderText.setText("");
			delete.setVisible(false);
		}
		
		public void onFailure(Throwable caught) {
			Main.get().showError("callbackDeleteMailAccount", caught);
		}
	};
	
}
