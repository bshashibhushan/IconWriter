package com.ikon.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMDocument;
import com.ikon.api.OKMFolder;
import com.ikon.api.OKMNotification;
import com.ikon.api.OKMRepository;
import com.ikon.bean.Document;
import com.ikon.bean.Folder;
import com.ikon.core.AccessDeniedException;
import com.ikon.dao.RetentionPolicyDAO;
import com.ikon.dao.bean.RetentionPolicy;
import com.ikon.module.DocumentModule;
import com.ikon.module.FolderModule;
import com.ikon.module.ModuleManager;

public class RetentionPolicyTimer extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(RetentionPolicyTimer.class);
	private static volatile boolean running = false;
	
	/**
	 * This method is called by the crontab every 10 hours. It gets the list of policies and if a policy is active, 
	 * it is going to check for type of node. If it is folder, document list is got through iterator and if document is expired, 
	 * it will move to the destination and notify subscribers. If it is a document, properties of document is fetched, 
	 * and above process is repeated.
	 */
	public void applyRetentionPolicy(){
		String message = null;
		DateTimeFormatter formatter = DateTimeFormat.forPattern("d MMM yyyy");
		
		try {
			List<RetentionPolicy> policies = RetentionPolicyDAO.findAll();
			message = "According to the Retention Policies of this document, this document has been moved.";
	
			for(RetentionPolicy policy : policies){
				if(policy.isActive()){
					DateTime expiryDate = formatter.parseDateTime(policy.getExpiryDate());
					
					if(policy.getNodeType().equals("openkm:folder")){
						try{
							DocumentModule dm = ModuleManager.getDocumentModule();
							for (Iterator<Document> it = dm.getChildren(null, policy.getSourcePath()).iterator(); it.hasNext();) {
								Document doc = it.next();
								List<String> userList = new ArrayList<String>();
								userList.addAll(doc.getSubscriptors()); //get subscribers
										
								if(expiryDate.isBeforeNow()){
									if(!doc.isLocked()) {
										OKMDocument.getInstance().move(null, doc.getPath(), policy.getDestinationPath() + "/" + doc.getTitle());
										OKMNotification.getInstance().notify(null, doc.getPath(), userList, message, false);
									} 
								} else if(getDifference(expiryDate) == 1){
									message = "According to the Retention Policies of this document, this document will be moved in one day.";
									OKMNotification.getInstance().notify(null, doc.getPath(), userList, message, false);
								}			
							}
							
							FolderModule fm = ModuleManager.getFolderModule();
							for (Iterator<Folder> it = fm.getChildren(null, policy.getSourcePath()).iterator(); it.hasNext();) {
								Folder folder = it.next();
								List<String> userList = new ArrayList<String>();
								userList.addAll(folder.getSubscriptors()); //get subscribers
										
								if(expiryDate.isBeforeNow()){
									OKMFolder.getInstance().move(null, folder.getPath(), policy.getDestinationPath() + "/");
									OKMNotification.getInstance().notify(null, folder.getPath(), userList, message, false);									
								} else if(getDifference(expiryDate) == 1){
									message = "According to the Retention Policies of this document, this document will be moved in one day.";
									OKMNotification.getInstance().notify(null, folder.getPath(), userList, message, false);
								}			
							}
						} catch (Exception e){
							
						}					
					} else {	
						try{
							String path = OKMRepository.getInstance().getNodePath(null, policy.getNodeUuid());
							Document doc = OKMDocument.getInstance().getProperties(null, path);
							List<String> userList = new ArrayList<String>();
							userList.addAll(doc.getSubscriptors());//get subscribers
							
							if(expiryDate.isBeforeNow()){
								OKMDocument.getInstance().move(null, doc.getPath(), policy.getDestinationPath() + "/" + doc.getTitle());
								RetentionPolicyDAO.delete(doc.getUuid());
								OKMNotification.getInstance().notify(null, doc.getPath(), userList, message, false);								
							} else if(getDifference(expiryDate) == 1){
								message = "According to the Retention Policies of this document, this document will be moved in one day.";
								OKMNotification.getInstance().notify(null, doc.getPath(), userList, message, false);
							}
						} catch (Exception e){
							
						}
					}
				}			
			}
		} catch (Exception e){
			e.printStackTrace();
		} 
	 }
	 
	/*
	 * return days 
	 */
	 private long getDifference(DateTime expiryDate){	
		 return Days.daysBetween(new DateTime().toDateMidnight(), expiryDate.toDateMidnight()).getDays();
	 }

	 @Override
	 public void run() {
		if (running) {
			log.warn("*** Retention Policy Timer already running ***");
		} else {
			running = true;
			log.info("*** Retention Policy Timer started***");
			
			try{
				applyRetentionPolicy();
			} finally {
				running = false;
			}	
			
			log.info("*** End Retention Policy Timer ***");
		}		
	 }
}
