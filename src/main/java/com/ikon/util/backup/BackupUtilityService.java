package com.ikon.util.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionFactoryImplementor;

import com.ikon.core.Config;
import com.ikon.dao.HibernateUtil;
import com.ikon.util.FTPUtil;

public class BackupUtilityService {
	InputStream input;
	static Logger log = Logger.getLogger(BackupUtilityService.class);

	public void ftpBackup(String serverId, String userName, String password)
			throws Exception {

		FTPUtil ftpUtil = new FTPUtil(serverId, userName, password);
		backupFtp(ftpUtil, serverId, userName, password);

	}

	public void backupFtp(FTPUtil ftpUtil, String serverId, String userName,
			String password) throws Exception {

		InputStream input = new FileInputStream(new File(
				Config.PROPERTY_GROUPS_XML));
		ftpUtil.UploadInputStream(input, "PropertyGroups.xml", "/");

		File folder = new File(Config.REPOSITORY_HOME);
		uploadDirectoryContents("repository", ftpUtil, folder);

		// InputStream input = new FileInputStream(new File(
		// Config.PROPERTY_GROUPS_XML));
		// ftpUtil.UploadInputStream(input, "PropertyGroups.xml",
		// "/repository/");
		String backUpFileName = "backup";

		String sqlFile = Config.REPOSITORY_HOME + "\\" + backUpFileName;
		String[] executeCmdAndExtn = getExecuteCmdAndExtn(backUpFileName,
				sqlFile);
		if (executeCmdAndExtn != null) {
			backupdb(executeCmdAndExtn[0]);
			input = new FileInputStream(
					new File(sqlFile + executeCmdAndExtn[1]));
			ftpUtil.UploadInputStream(input, backUpFileName
					+ executeCmdAndExtn[1], "/repository/");
		}

	}

	public String[] getExecuteCmdAndExtn(String backUpFileName, String sqlFile) {

		String dbName = "okmdb";
		String mySqlUserName = "root";
		String mySqlPassword = "root";

		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Dialect dialect = ((SessionFactoryImplementor) sessionFactory)
				.getDialect();
		String dialectName = dialect.getClass().getName();
		String executeCmd = null;

		String backupExtn = "";

		if (dialectName.matches("org.hibernate.dialect.MySQL.*Dialect")) {
			backupExtn = ".sql";
			sqlFile += backupExtn;
			executeCmd = "mysqldump -u " + mySqlUserName + " -p"
					+ mySqlPassword + " --add-drop-database -B " + dbName
					+ " -r " + sqlFile;
		} else if (dialectName.equals("org.hibernate.dialect.SQLServerDialect")) {
			backupExtn = ".bak";
			sqlFile += backupExtn;
			executeCmd = "osql.exe -E -Q \"BACKUP DATABASE okmdb TO DISK='"
					+ sqlFile + "' WITH FORMAT\"";
		} else {
			return null;
		}
		return new String[] { executeCmd, backupExtn };
	}

	public boolean backupdb(String executeCmd) {
		System.out.println(executeCmd);
		Process runtimeProcess;
		try {
			runtimeProcess = Runtime.getRuntime().exec(executeCmd);
			int processComplete = runtimeProcess.waitFor();

			if (processComplete == 0) {
				log.info("Backup created successfully");
				return true;
			} else {
				log.error("Could not create the backup");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	// public boolean backupdb(String dbName, String dbUserName,
	// String dbPassword, String path) {
	//
	// SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	// Dialect dialect = ((SessionFactoryImplementor) sessionFactory)
	// .getDialect();
	// String dialectName = dialect.getClass().getName();
	// String executeCmd = null;
	//
	// if (dialectName.equals("org.hibernate.dialect.MySQLDialect")) {
	// executeCmd = "mysqldump -u " + dbUserName + " -p" + dbPassword
	// + " --add-drop-database -B " + dbName + " -r " + path;
	// } else if (dialectName.equals("org.hibernate.dialect.SQLServerDialect"))
	// {
	// executeCmd = "osql.exe -E -Q \"BACKUP DATABASE okmdb TO DISK='"
	// + path + "' WITH FORMAT\"";
	// }
	//
	// System.out.println(executeCmd);
	// if (executeCmd != null) {
	// Process runtimeProcess;
	// try {
	//
	// runtimeProcess = Runtime.getRuntime().exec(executeCmd);
	// int processComplete = runtimeProcess.waitFor();
	//
	// if (processComplete == 0) {
	// log.info("Backup created successfully");
	// return true;
	// } else {
	// log.error("Could not create the backup");
	// }
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }
	// return false;
	// }

	public static void uploadDirectoryContents(String rootFolder,
			FTPUtil ftpUtil, File dir) throws Exception {
		if (rootFolder != null) {
			ftpUtil.makeDirectory(rootFolder);
		}
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				ftpUtil.changeCurrentDirectory("/"
						+ file.getParentFile().getCanonicalPath()
								.replace(Config.HOME_DIR, ""));
				if (file.isDirectory()) {
					ftpUtil.makeDirectory(file.getName());
					uploadDirectoryContents(null, ftpUtil, file);
				} else {
					ftpUtil.uploadFile(file.getCanonicalPath(), file.getName());
				}
			}
		}

	}

	public void backupStorage(String storagePath) throws IOException {
		FileUtils.copyDirectoryToDirectory(new File(Config.REPOSITORY_HOME),
				new File(storagePath));
		FileUtils.copyFile(new File(Config.PROPERTY_GROUPS_XML), new File(
				storagePath + "/PropertyGroups.xml"));
		String backUpFileName = "backup";

		String sqlFile = storagePath + "\\repository\\"
				+ backUpFileName;
		String[] executeCmdAndExtn = getExecuteCmdAndExtn(backUpFileName,
				sqlFile);
		if (executeCmdAndExtn != null) {
			backupdb(executeCmdAndExtn[0]);
		}
	}
}
