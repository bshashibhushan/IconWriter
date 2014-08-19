package com.ikon.util.backup;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionFactoryImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.core.Config;
import com.ikon.dao.HibernateUtil;
import com.ikon.servlet.admin.BackupUtilServlet;
import com.ikon.util.FTPUtil;

public class RestoreUtilityService {
	private static Logger log = LoggerFactory
			.getLogger(BackupUtilServlet.class);

	public void restoreftp(String serverId, String userName, String password)
			throws Exception {
		FTPUtil ftpUtil = new FTPUtil(serverId, userName, password);
		ftpUtil.restoreFile();
		String backUpFileName = "backup";
		String sqlFile = Config.REPOSITORY_HOME + "\\" + backUpFileName;
		String[] executeCmdAndExtn = getExecuteCmdAndExtn(sqlFile);
		if (restore(executeCmdAndExtn) == false) {
			throw new Exception("Database Restore not Done");
		}

	}

	public String[] getExecuteCmdAndExtn(String sqlFile) {

		String dbName = "okmdb";
		String mySqlUserName = "root";
		String mySqlPassword = "root";

		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Dialect dialect = ((SessionFactoryImplementor) sessionFactory)
				.getDialect();
		String dialectName = dialect.getClass().getName();
		String[] executeCmd = null;

		String backupExtn = "";

		if (dialectName.matches("org.hibernate.dialect.MySQL.*Dialect")) {
			backupExtn = ".sql";
			sqlFile += backupExtn;

			executeCmd = new String[] { "mysql", dbName, "-u" + mySqlUserName,
					"-p" + mySqlPassword, "-e", " source " + sqlFile };

		} else if (dialectName.equals("org.hibernate.dialect.SQLServerDialect")) {
			backupExtn = ".bak";
			sqlFile += backupExtn;
			// executeCmd = "SqlCmd -E -S"
			// + "localhost"
			// + "–Q"
			// + " RESTORE DATABASE dbName FROM DISK=Config.REPOSITORY_HOME"
			// + "\\backup.bak";

			executeCmd = new String[] { "SqlCmd", " -E", " -S", " localhost",
					" -Q", " \"RESTORE DATABASE ", dbName, " FROM DISK=\'",
					sqlFile, "\'\"" };
		} else {
			return null;
		}
		return executeCmd;
	}

	public boolean restore(String[] executeCmd) {
		Process runtimeProcess;
		try {
			runtimeProcess = Runtime.getRuntime().exec(executeCmd);
			int processComplete = runtimeProcess.waitFor();

			if (processComplete == 0) {
				log.info("Restore success");
				return true;
			} else {
				log.error("Fail restores");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	public void restoreStorage(String storagePath) throws Exception {
		FileUtils.copyDirectory(new File(storagePath + "/repository"),
				new File(Config.REPOSITORY_HOME));
		FileUtils.copyFile(new File(storagePath + "/PropertyGroups.xml"),
				new File(Config.HOME_DIR + "/PropertyGroups.xml"));
		String backUpFileName = "backup";
		String sqlFile = Config.REPOSITORY_HOME + "\\" + backUpFileName;
		String[] executeCmdAndExtn = getExecuteCmdAndExtn(sqlFile);
		if (restore(executeCmdAndExtn) == false) {
			throw new Exception("Database Restore not Done");
		}

	}
}