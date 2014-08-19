package com.ikon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMDocument;
import com.ikon.api.OKMFolder;
import com.ikon.core.DatabaseException;
import com.ikon.core.ItemExistsException;
import com.ikon.dao.HotFoldersDAO;
import com.ikon.dao.bean.HotFolders;

public class HotFolderTimer extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(HotFolderTimer.class);
	private static volatile boolean running = false;
	OKMDocument document = OKMDocument.getInstance();
    OKMFolder okmfolder = OKMFolder.getInstance();
	
	public void executeHotFolders(){
		List<HotFolders> folders;
		String folderPath = null;
		try {
			folders = HotFoldersDAO.findAll();
			for(HotFolders folder : folders){
				if(folder.getDestinationPath().endsWith("/"))
					folderPath = folder.getDestinationPath();
				else
					folderPath = folder.getDestinationPath() + "/";
				autoImport(folderPath, new File(folder.getSourcePath()));
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}     
         
	 }
	
	public void autoImport(String okmPath, File fldpath){

        try{

          for (File file : fldpath.listFiles()) {

            try {
              if (file.isDirectory()){
                try {
                  okmfolder.createSimple(null, okmPath + file.getName());
                } catch (ItemExistsException ie) {
                  // Folder already exists - just ignore exception
                }
                autoImport( okmPath + file.getName() + "/", file);
            } else {
                // Check if file is still being written to
                long length = file.length();
                Thread.sleep(1000);
           
                if (file.length() > length) continue;  // Skip file this time
                document.createSimple(null, okmPath + "/" +  file.getName(), new FileInputStream(file));
              }
            } catch (IOException e) {
             
              // Something bad happened to prevent import. Skip to next file.
              continue;
            }

            file.delete();
          }
        } catch (Exception e) {
        }
      }

	 @Override
	 public void run() {
		if (running) {
			log.warn("*** Hot Folder Timer already running ***");
		} else {
			running = true;
			log.info("*** Hot Folders Timer started***");
			
			try{
				executeHotFolders();
			} finally {
				running = false;
			}	
			
			log.info("*** End Hot Folders Timer ***");
		}		
	 }

}
