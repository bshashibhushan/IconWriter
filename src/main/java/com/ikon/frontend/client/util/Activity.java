package com.ikon.frontend.client.util;

/**
 * This specifies a list of activities that can be displayed on the recent activity tab of a document.
 * @author 
 *
 */
public enum Activity {
	CANCEL_CHECKIN_DOCUMENT("Document Edit was cancelled by "),
	CHECKIN_DOCUMENT("Document was checked in by"),
	CHECKOUT_DOCUMENT("Document was checkedout by "),
	CREATE_DOCUMENT("Document was uploaded by"),
	DELETE_DOCUMENT("Document was deleted by"),
	LOCK_DOCUMENT("Document was locked by"),
	MOVE_DOCUMENT("Document was moved by"),
	PURGE_DOCUMENT("Document was purged by"),
	RENAME_DOCUMENT("Document was renamed by"),
	UNLOCK_DOCUMENT("Document was unlocked by"),
	SET_PROPERTY_GROUP_PROPERTIES("Property Group was added by"),
	DOWNLOAD_DOCUMENT("Document was downloaded by"),
	COPY_DOCUMENT("Document was copied by"),
	ADD_NOTE("Note was added by"),
	ADD_CATEGORY("Category was added by"),
	ADD_KEYWORD("Keyword was added by"),
	REMOVE_CATEGORY("Category was removed by"),
	REMOVE_KEYWORD("Keyword was removed by"),
	DELETE_NOTE("Note was deleted by"),
	RESTORE_DOCUMENT_VERSION("Document version was restored by"),
	PURGE_DOCUMENT_VERSION_HISTORY("Document History was purged by"),
	SIGN_DOCUMENT("Document was signed by"),
	APPLY_RETENTION_POLICY("Retention Policy was set by"),
	DELETE_RETENTION_POLICY("Retention Policy was deleted by"),
	STAMP_IMAGE("Document was stamped with image"),
	STAMP_TEXT("Document was stamped with text");
	
	private String activityName;
	
	private Activity(String activityName){
		this.activityName = activityName;
	}
	
	public String getActivityName() {
	    return activityName;
	}	
}
