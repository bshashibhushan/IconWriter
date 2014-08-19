/**
 *  openkm, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2013  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.ikon.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * OKMBundleResources
 * 
 * @author jllort
 *
 */
public interface OKMBundleResources extends ClientBundle {
	public static final OKMBundleResources INSTANCE =  GWT.create(OKMBundleResources.class);
	
	@Source("com/ikon/frontend/public/img/icon/actions/delete.gif")
	public ImageResource deleteIcon();
	
	@Source("com/ikon/frontend/public/img/icon/actions/clean.png")
	public ImageResource cleanIcon();
	
	@Source("com/ikon/frontend/public/img/icon/stackpanel/book_open.gif")
	public ImageResource bookOpenIcon();
	
	@Source("com/ikon/frontend/public/img/icon/stackpanel/table_key.gif")
	public ImageResource tableKeyIcon();
	
	@Source("com/ikon/frontend/public/img/icon/toolbar/user.png")
	public ImageResource userIcon();

	@Source("com/ikon/frontend/public/img/icon/toolbar/mail.png")
	public ImageResource mailIcon();

	@Source("com/ikon/frontend/public/img/icon/toolbar/news.png")
	public ImageResource newsIcon();

	@Source("com/ikon/frontend/public/img/icon/toolbar/general.png")
	public ImageResource generalIcon();

	@Source("com/ikon/frontend/public/img/icon/toolbar/workflow.png")
	public ImageResource workflowIcon();
	
	@Source("com/ikon/frontend/public/img/icon/toolbar/keyword_map.png")
	public ImageResource keywordMapIcon();
	
	@Source("com/ikon/frontend/public/img/icon/actions/add_folder.gif")
	public ImageResource createFolder();
	
	@Source("com/ikon/frontend/public/img/icon/actions/add_folder_disabled.gif")
	public ImageResource createFolderDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/folder_find.gif")
	public ImageResource findFolder();
	
	@Source("com/ikon/frontend/public/img/icon/actions/folder_find_disabled.gif")
	public ImageResource findFolderDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/document_find.png")
	public ImageResource findDocument();
	
	@Source("com/ikon/frontend/public/img/icon/actions/document_find_disabled.png")
	public ImageResource findDocumentDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/lock.gif")
	public ImageResource lock();
	
	@Source("com/ikon/frontend/public/img/icon/actions/lock_disabled.gif")
	public ImageResource lockDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/unlock.gif")
	public ImageResource unLock();
	
	@Source("com/ikon/frontend/public/img/icon/actions/unlock_disabled.gif")
	public ImageResource unLockDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/add_document.gif")
	public ImageResource addDocument();
	
	@Source("com/ikon/frontend/public/img/icon/actions/add_document_disabled.gif")
	public ImageResource addDocumentDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/delete.gif")
	public ImageResource delete();
	
	@Source("com/ikon/frontend/public/img/icon/actions/delete_disabled.gif")
	public ImageResource deleteDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/checkout.gif")
	public ImageResource checkout();
	
	@Source("com/ikon/frontend/public/img/icon/actions/checkout_disabled.gif")
	public ImageResource checkoutDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/checkin.gif")
	public ImageResource checkin();
	
	@Source("com/ikon/frontend/public/img/icon/actions/checkin_disabled.gif")
	public ImageResource checkinDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/cancel_checkout.gif")
	public ImageResource cancelCheckout();
	
	@Source("com/ikon/frontend/public/img/icon/actions/cancel_checkout_disabled.gif")
	public ImageResource cancelCheckoutDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/download.gif")
	public ImageResource download();
	
	@Source("com/ikon/frontend/public/img/icon/actions/download_disabled.gif")
	public ImageResource downloadDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/download_pdf.gif")
	public ImageResource downloadPdf();
	
	@Source("com/ikon/frontend/public/img/icon/actions/download_pdf_disabled.gif")
	public ImageResource downloadPdfDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/add_property_group.gif")
	public ImageResource addPropertyGroup();
	
	@Source("com/ikon/frontend/public/img/icon/actions/add_property_group_disabled.gif")
	public ImageResource addPropertyGroupDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/remove_property_group.gif")
	public ImageResource removePropertyGroup();
	
	@Source("com/ikon/frontend/public/img/icon/actions/remove_property_group_disabled.gif")
	public ImageResource removePropertyGroupDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/start_workflow.gif")
	public ImageResource startWorkflow();
	
	@Source("com/ikon/frontend/public/img/icon/actions/start_workflow_disabled.gif")
	public ImageResource startWorkflowDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/add_subscription.gif")
	public ImageResource addSubscription();
	
	@Source("com/ikon/frontend/public/img/icon/actions/add_subscription_disabled.gif")
	public ImageResource addSubscriptionDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/remove_subscription.gif")
	public ImageResource removeSubscription();
	
	@Source("com/ikon/frontend/public/img/icon/actions/remove_subscription_disabled.gif")
	public ImageResource removeSubscriptionDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/propose_subscription.png")
	public ImageResource proposeSubscription();
	
	@Source("com/ikon/frontend/public/img/icon/actions/bookmark_go.gif")
	public ImageResource home();
	
	@Source("com/ikon/frontend/public/img/icon/actions/bookmark_go_disabled.gif")
	public ImageResource homeDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/refresh.gif")
	public ImageResource refresh();
	
	@Source("com/ikon/frontend/public/img/icon/actions/refresh_disabled.gif")
	public ImageResource refreshDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/scanner_disabled.gif")
	public ImageResource scannerDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/scanner.gif")
	public ImageResource scanner();
	
	@Source("com/ikon/frontend/public/img/icon/actions/upload_disabled.gif")
	public ImageResource uploaderDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/upload.gif")
	public ImageResource uploader();
	
	@Source("com/ikon/frontend/public/img/icon/chat/chat_disconnected.png")
	public ImageResource chatDisconnected();
	
	@Source("com/ikon/frontend/public/img/icon/chat/chat_connected.png")
	public ImageResource chatConnected();
	
	@Source("com/ikon/frontend/public/img/icon/chat/new_chat_room.png")
	public ImageResource newChatRoom();
	
	@Source("com/ikon/frontend/public/img/icon/chat/add_user.png")
	public ImageResource addUserToChatRoom();
	
	@Source("com/ikon/frontend/public/img/icon/connect.gif")
	public ImageResource openkmConnected();
	
	@Source("com/ikon/frontend/public/img/icon/user_repository.gif")
	public ImageResource repositorySize();
	
	@Source("com/ikon/frontend/public/img/icon/subscribed.gif")
	public ImageResource subscribed();
	
	@Source("com/ikon/frontend/public/img/icon/news_alert.gif")
	public ImageResource newsAlert();
	
	@Source("com/ikon/frontend/public/img/icon/news.gif")
	public ImageResource news();
	
	@Source("com/ikon/frontend/public/img/icon/workflow_tasks.gif")
	public ImageResource workflowTasks();
	
	@Source("com/ikon/frontend/public/img/icon/workflow_tasks_alert.gif")
	public ImageResource workflowTasksAlert();
	
	@Source("com/ikon/frontend/public/img/icon/workflow_pooled_tasks.gif")
	public ImageResource workflowPooledTasks();
	
	@Source("com/ikon/frontend/public/img/icon/workflow_pooled_tasks_alert.gif")
	public ImageResource workflowPooledTasksAlert();
	
	@Source("com/ikon/frontend/public/img/icon/warning.gif")
	public ImageResource warning();
	
	@Source("com/ikon/frontend/public/img/icon/toolbar/separator.gif")
	public ImageResource separator();
	
	@Source("com/ikon/frontend/public/img/zoom_out.gif")
	public ImageResource zoomOut();
	
	@Source("com/ikon/frontend/public/img/zoom_in.gif")
	public ImageResource zoomIn();
	
	@Source("com/ikon/frontend/public/img/viewed.gif")
	public ImageResource viewed();
	
	@Source("com/ikon/frontend/public/img/viewed_pending.gif")
	public ImageResource pending();
	
	@Source("com/ikon/frontend/public/img/feed.png")
	public ImageResource feed();
	
	@Source("com/ikon/frontend/public/img/icon/loaded.gif")
	public ImageResource loadedIcon();

	@Source("com/ikon/frontend/public/img/icon/loaded_disabled.gif")
	public ImageResource loadedDisabledIcon();
	
	@Source("com/ikon/frontend/public/img/icon/loaded_error.gif")
	public ImageResource loadedErrorIcon();
	
	@Source("com/ikon/frontend/public/img/icon/security/add.gif")
	public ImageResource add();
	
	@Source("com/ikon/frontend/public/img/icon/security/remove.gif")
	public ImageResource remove();
	
	@Source("com/ikon/frontend/public/img/icon/quota/quota1.gif")
	public ImageResource quota1();
	
	@Source("com/ikon/frontend/public/img/icon/quota/quota2.gif")
	public ImageResource quota2();
	
	@Source("com/ikon/frontend/public/img/icon/quota/quota3.gif")
	public ImageResource quota3();
	
	@Source("com/ikon/frontend/public/img/icon/quota/quota4.gif")
	public ImageResource quota4();
	
	@Source("com/ikon/frontend/public/img/icon/quota/quota5.gif")
	public ImageResource quota5();
	
	@Source("com/ikon/frontend/public/img/icon/quota/quota6.gif")
	public ImageResource quota6();
	
	@Source("com/ikon/frontend/public/img/logo_ikon_tiny.gif")
	public ImageResource logoopenkm();
	
	@Source("com/ikon/frontend/public/img/icon/search/calendar.gif")
	public ImageResource calendar();
	
	@Source("com/ikon/frontend/public/img/icon/search/calendar_disabled.gif")
	public ImageResource calendarDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/security/yes.gif")
	public ImageResource yes();
	
	@Source("com/ikon/frontend/public/img/icon/security/no.gif")
	public ImageResource no();
	
	@Source("com/ikon/frontend/public/img/icon/actions/comment_edit.gif")
	public ImageResource noteEdit();
	
	@Source("com/ikon/frontend/public/img/icon/actions/comment_delete.gif")
	public ImageResource noteDelete();
	
	@Source("com/ikon/frontend/public/img/icon/search/folder_explore.gif")
	public ImageResource folderExplorer();
	
	@Source("com/ikon/frontend/public/img/indicator.gif")
	public ImageResource indicator();
	
	@Source("com/ikon/frontend/public/img/icon/actions/share_query.gif")
	public ImageResource sharedQuery();
	
	@Source("com/ikon/frontend/public/img/icon/actions/printer.png")
	public ImageResource print();
	
	@Source("com/ikon/frontend/public/img/icon/actions/printer_disabled.png")
	public ImageResource printDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/editor/justifyCenter.gif")
	public ImageResource justifyCenter();
	
	@Source("com/ikon/frontend/public/img/icon/editor/justify.gif")
	public ImageResource justify();
	
	@Source("com/ikon/frontend/public/img/icon/editor/justifyLeft.gif")
	public ImageResource justifyLeft();
	
	@Source("com/ikon/frontend/public/img/icon/editor/justifyRight.gif")
	public ImageResource justifyRight();
	
	@Source("com/ikon/frontend/public/img/icon/editor/bold.gif")
	public ImageResource bold();
	
	@Source("com/ikon/frontend/public/img/icon/editor/italic.gif")
	public ImageResource italic();
	
	@Source("com/ikon/frontend/public/img/icon/editor/underline.gif")
	public ImageResource underline();
	
	@Source("com/ikon/frontend/public/img/icon/editor/stroke.gif")
	public ImageResource stroke();
	
	@Source("com/ikon/frontend/public/img/icon/editor/subscript.gif")
	public ImageResource subScript();
	
	@Source("com/ikon/frontend/public/img/icon/editor/superscript.gif")
	public ImageResource superScript();
	
	@Source("com/ikon/frontend/public/img/icon/editor/unordered.gif")
	public ImageResource unOrdered();
	
	@Source("com/ikon/frontend/public/img/icon/editor/ordered.gif")
	public ImageResource ordered();
	
	@Source("com/ikon/frontend/public/img/icon/editor/identLeft.gif")
	public ImageResource identLeft();
	
	@Source("com/ikon/frontend/public/img/icon/editor/identRight.gif")
	public ImageResource identRight();
	
	@Source("com/ikon/frontend/public/img/icon/editor/createEditorLink.gif")
	public ImageResource createEditorLink();
	
	@Source("com/ikon/frontend/public/img/icon/editor/breakEditorLink.gif")
	public ImageResource breakEditorLink();
	
	@Source("com/ikon/frontend/public/img/icon/editor/line.gif")
	public ImageResource line();
	
	@Source("com/ikon/frontend/public/img/icon/editor/html.gif")
	public ImageResource html();
	
	@Source("com/ikon/frontend/public/img/icon/editor/picture.gif")
	public ImageResource picture();
	
	@Source("com/ikon/frontend/public/img/icon/editor/removeFormat.gif")
	public ImageResource removeFormat();
	
	@Source("com/ikon/frontend/public/img/icon/actions/folder_edit.png")
	public ImageResource folderEdit();
	
	@Source("com/ikon/frontend/public/img/icon/actions/new_record.png")
	public ImageResource newRecord();
	
	@Source("com/ikon/frontend/public/img/icon/actions/database_record.png")
	public ImageResource databaseRecord();
	
	@Source("com/ikon/frontend/public/img/icon/actions/search.png")
	public ImageResource search();
	
	@Source("com/ikon/frontend/public/img/icon/actions/search_disabled.png")
	public ImageResource searchDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/application_resize.png")
	public ImageResource splitterResize();
	
	@Source("com/ikon/frontend/public/img/icon/actions/application_resize_disabled.png")
	public ImageResource splitterResizeDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/arrow_up.png")
	public ImageResource arrowUp();
	
	@Source("com/ikon/frontend/public/img/icon/actions/massive_actions.png")
	public ImageResource massive();
	
	@Source("com/ikon/frontend/public/img/icon/actions/arrow_down.png")
	public ImageResource arrowDown();
	
	@Source("com/ikon/frontend/public/img/icon/actions/find.png")
	public ImageResource find();
	
	@Source("com/ikon/frontend/public/img/icon/actions/find_disabled.png")
	public ImageResource findDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/similar_find.png")
	public ImageResource findSimilarDocument();
	
	@Source("com/ikon/frontend/public/img/icon/actions/folder.png")
	public ImageResource folder();
	
	@Source("com/ikon/frontend/public/img/icon/actions/folder_disabled.png")
	public ImageResource folderDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/document.png")
	public ImageResource document();
	
	@Source("com/ikon/frontend/public/img/icon/actions/document_disabled.png")
	public ImageResource documentDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/mail.png")
	public ImageResource mail();
	
	@Source("com/ikon/frontend/public/img/icon/actions/mail_disabled.png")
	public ImageResource mailDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/search/resultset_next.gif")
	public ImageResource next();
	
	@Source("com/ikon/frontend/public/img/icon/search/resultset_next_disabled.gif")
	public ImageResource nextDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/search/resultset_previous.gif")
	public ImageResource previous();
	
	@Source("com/ikon/frontend/public/img/icon/search/resultset_previous_disabled.gif")
	public ImageResource previousDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/search/goto_end.gif")
	public ImageResource gotoEnd();
	
	@Source("com/ikon/frontend/public/img/icon/search/goto_end_disabled.gif")
	public ImageResource gotoEndDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/search/goto_start.gif")
	public ImageResource gotoStart();
	
	@Source("com/ikon/frontend/public/img/icon/search/goto_start_disabled.gif")
	public ImageResource gotoStartDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/filter.png")
	public ImageResource filter();
	
	@Source("com/ikon/frontend/public/img/icon/actions/omr.png")
	public ImageResource omr();
	
	@Source("com/ikon/frontend/public/img/icon/actions/omr_disabled.png")
	public ImageResource omrDisabled();
	
	@Source("com/ikon/frontend/public/img/icon/actions/preview.gif")
	public ImageResource searchPreview();

	@Source("com/ikon/frontend/public/img/icon/actions/sign_preview.png")
	public ImageResource sign();
	
	@Source("com/ikon/frontend/public/img/icon/actions/stamp_preview.gif")
	public ImageResource stamp();

	@Source("com/ikon/frontend/public/img/icon/actions/loading.gif")
	public ImageResource loadingGif();
	
	@Source("com/ikon/frontend/public/img/icon/actions/addRelation.gif")
	 public ImageResource staple();
	 
	 @Source("com/ikon/frontend/public/img/icon/actions/addRelation_disabled.gif")
	 public ImageResource stapleDisabled();
	 
	 @Source("com/ikon/frontend/public/img/icon/actions/newRelation.gif")
	 public ImageResource stapleGroup();
	 
	 @Source("com/ikon/frontend/public/img/icon/actions/newRelation_disabled.gif")
	 public ImageResource stapleGroupDisabled();
	 
	 @Source("com/ikon/frontend/public/img/icon/actions/close.png")
	 public ImageResource closePreview();
}
