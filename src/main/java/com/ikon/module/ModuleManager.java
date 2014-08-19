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

package com.ikon.module;

import com.ikon.core.Config;

/**
 * Choose between Native Repository or Jackrabbit implementations.
 * 
 * @author pavila
 */
public class ModuleManager {
	private static AuthModule authModule = null;
	private static RepositoryModule repositoryModule = null;
	private static FolderModule folderModule = null;
	private static DocumentModule documentModule = null;
	private static NoteModule noteModule = null;
	private static SearchModule searchModule = null;
	private static PropertyGroupModule propertyGroupModule= null;
	private static NotificationModule notificationModule = null;
	private static BookmarkModule bookmarkModule = null;
	private static DashboardModule dashboardModule = null;
	private static WorkflowModule workflowModule = null;
	private static ScriptingModule scriptingModule = null;
	private static StatsModule statsModule = null;
	private static MailModule mailModule = null;
	private static PropertyModule propertyModule = null;
	private static UserConfigModule userConfigModule = null;
	
	/**
	 * 
	 */
	public static synchronized AuthModule getAuthModule() {
		if (authModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				authModule = new com.ikon.module.db.DbAuthModule();
			} else {
				authModule = new com.ikon.module.jcr.JcrAuthModule();
			}
		}
		
		return authModule;
	}

	/**
	 * 
	 */
	public static synchronized RepositoryModule getRepositoryModule() {
		if (repositoryModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				repositoryModule = new com.ikon.module.db.DbRepositoryModule();
			} else {
				repositoryModule = new com.ikon.module.jcr.JcrRepositoryModule();
			}
		}
		
		return repositoryModule;
	}

	/**
	 * 
	 */
	public static synchronized FolderModule getFolderModule() {
		if (folderModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				folderModule = new com.ikon.module.db.DbFolderModule();
			} else {
				folderModule = new com.ikon.module.jcr.JcrFolderModule();
			}
		}
		
		return folderModule;
	}

	/**
	 * 
	 */
	public static synchronized DocumentModule getDocumentModule() {
		if (documentModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				documentModule = new com.ikon.module.db.DbDocumentModule();
			} else {
				documentModule = new com.ikon.module.jcr.JcrDocumentModule();
			}
		}
		
		return documentModule;
	}
	
	/**
	 * 
	 */
	public static synchronized NoteModule getNoteModule() {
		if (noteModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				noteModule = new com.ikon.module.db.DbNoteModule();
			} else {
				noteModule = new com.ikon.module.jcr.JcrNoteModule();
			}
		}
		
		return noteModule;
	}

	/**
	 * 
	 */
	public static synchronized SearchModule getSearchModule() {
		if (searchModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				searchModule = new com.ikon.module.db.DbSearchModule();
			} else {
				searchModule = new com.ikon.module.jcr.JcrSearchModule();
			}
		}
		
		return searchModule;
	}
	
	/**
	 * 
	 */
	public static synchronized PropertyGroupModule getPropertyGroupModule() {
		if (propertyGroupModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				propertyGroupModule = new com.ikon.module.db.DbPropertyGroupModule();
			} else {
				propertyGroupModule = new com.ikon.module.jcr.JcrPropertyGroupModule();
			}
		}
		
		return propertyGroupModule;
	}	

	/**
	 * 
	 */
	public static synchronized NotificationModule getNotificationModule() {
		if (notificationModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				notificationModule = new com.ikon.module.db.DbNotificationModule();
			} else {
				notificationModule = new com.ikon.module.jcr.JcrNotificationModule();
			}
		}
		
		return notificationModule;
	}
	
	/**
	 * 
	 */
	public static synchronized BookmarkModule getBookmarkModule() {
		if (bookmarkModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				bookmarkModule = new com.ikon.module.db.DbBookmarkModule();
			} else {
				bookmarkModule = new com.ikon.module.jcr.JcrBookmarkModule();
			}
		}
		
		return bookmarkModule;
	}
	
	/**
	 * 
	 */
	public static synchronized DashboardModule getDashboardModule() {
		if (dashboardModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				dashboardModule = new com.ikon.module.db.DbDashboardModule();
			} else {
				dashboardModule = new com.ikon.module.jcr.JcrDashboardModule();
			}
		}
		
		return dashboardModule;
	}
	
	/**
	 * 
	 */
	public static synchronized WorkflowModule getWorkflowModule() {
		if (workflowModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				workflowModule = new com.ikon.module.db.DbWorkflowModule();
			} else {
				workflowModule = new com.ikon.module.jcr.JcrWorkflowModule();
			}
		}
		
		return workflowModule;
	}
	
	/**
	 * 
	 */
	public static synchronized ScriptingModule getScriptingModule() {
		if (scriptingModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				scriptingModule = new com.ikon.module.db.DbScriptingModule();
			} else {
				scriptingModule = new com.ikon.module.jcr.JcrScriptingModule();
			}
		}
		
		return scriptingModule;
	}

	/**
	 * 
	 */
	public static synchronized StatsModule getStatsModule() {
		if (statsModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				statsModule = new com.ikon.module.db.DbStatsModule();
			} else {
				statsModule = new com.ikon.module.jcr.JcrStatsModule();
			}
		}
		
		return statsModule;
	}

	/**
	 * 
	 */
	public static synchronized MailModule getMailModule() {
		if (mailModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				mailModule = new com.ikon.module.db.DbMailModule();
			} else {
				mailModule = new com.ikon.module.jcr.JcrMailModule();
			}
		}
		
		return mailModule;
	}
	
	/**
	 * 
	 */
	public static synchronized PropertyModule getPropertyModule() {
		if (propertyModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				propertyModule = new com.ikon.module.db.DbPropertyModule();
			} else {
				propertyModule = new com.ikon.module.jcr.JcrPropertyModule();
			}
		}
		
		return propertyModule;
	}
	
	/**
	 * 
	 */
	public static synchronized UserConfigModule getUserConfigModule() {
		if (userConfigModule == null) {
			if (Config.REPOSITORY_NATIVE) {
				userConfigModule = new com.ikon.module.db.DbUserConfigModule();
			} else {
				userConfigModule = new com.ikon.module.jcr.JcrUserConfigModule();
			}
		}
		
		return userConfigModule;
	}
}
