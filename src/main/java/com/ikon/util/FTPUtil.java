package com.ikon.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.ikon.core.Config;

public class FTPUtil {
	FTPClient ftp = null;
	String remoteDirPath = "/repository";
	//String saveDirPath = "E:/Test";

	public FTPUtil(String host, String user, String pwd) throws Exception {
		ftp = new FTPClient();
		// ftp.addProtocolCommandListener(new PrintCommandListener(
		// new PrintWriter(System.out)));
		int reply;
		try {
			ftp.connect(host);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new Exception("Exception in connecting to FTP Server");
			}
			if (ftp.login(user, pwd)) {
				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				ftp.enterLocalPassiveMode();
			} else {
				throw new Exception("Invalid Credentials");
			}
		} catch (UnknownHostException e) {
			throw new Exception("Could not connect to FTP Server : Unknow Host");
		}

	}

	public void restoreFile() throws IOException {
		// readFiles(files, null);
		downloadDirectory(ftp, remoteDirPath, "", Config.HOME_DIR);
		ftp.logout();
		ftp.disconnect();

	}

	/**
	 * Download a single file from the FTP server
	 * 
	 * @param ftpClient
	 *            an instance of org.apache.commons.net.ftp.FTPClient class.
	 * @param remoteFilePath
	 *            path of the file on the server
	 * @param savePath
	 *            path of directory where the file will be stored
	 * @return true if the file was downloaded successfully, false otherwise
	 * @throws IOException
	 *             if any network or IO error occurred.
	 */
	public static boolean downloadSingleFile(FTPClient ftpClient,
			String remoteFilePath, String savePath) throws IOException {
		File downloadFile = new File(savePath);

		File parentDir = downloadFile.getParentFile();
		if (!parentDir.exists()) {
			parentDir.mkdir();
		}

		OutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(downloadFile));
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			return ftpClient.retrieveFile(remoteFilePath, outputStream);
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public static void downloadDirectory(FTPClient ftpClient, String parentDir,
			String currentDir, String saveDir) throws IOException {
		String dirToList = parentDir;
		if (!currentDir.equals("")) {
			dirToList += "/" + currentDir;
		}

		FTPFile[] subFiles = ftpClient.listFiles(dirToList);

		if (subFiles != null && subFiles.length > 0) {
			for (FTPFile aFile : subFiles) {
				String currentFileName = aFile.getName();
				if (currentFileName.equals(".") || currentFileName.equals("..")) {
					// skip parent directory and the directory itself
					continue;
				}
				String filePath = parentDir + "/" + currentDir + "/"
						+ currentFileName;
				if (currentDir.equals("")) {
					filePath = parentDir + "/" + currentFileName;
				}

				String newDirPath = saveDir + parentDir + File.separator
						+ currentDir + File.separator + currentFileName;
				if (currentDir.equals("")) {
					newDirPath = saveDir + parentDir + File.separator
							+ currentFileName;
				}

				if (aFile.isDirectory()) {
					// create the directory in saveDir
					File newDir = new File(newDirPath);
					boolean created = newDir.mkdirs();
					if (created) {
						System.out.println("CREATED the directory: "
								+ newDirPath);
					} else {
						System.out.println("COULD NOT create the directory: "
								+ newDirPath);
					}

					// download the sub directory
					downloadDirectory(ftpClient, dirToList, currentFileName,
							saveDir);
				} else {
					// download the file
					boolean success = downloadSingleFile(ftpClient, filePath,
							newDirPath);
					if (success) {
						System.out.println("DOWNLOADED the file: " + filePath);
					} else {
						System.out.println("COULD NOT download the file: "
								+ filePath);
					}
				}
			}
		}
	}

	/**
	 * public void readFiles(FTPFile[] files, String parent) throws IOException
	 * { for (FTPFile file : files) { String fileName = file.getName(); if
	 * (file.isDirectory()) { System.out.println(fileName); if (parent != null)
	 * { FTPFile[] sub = ftp .listFiles("/" + parent + "/" + fileName);
	 * readFiles(files, parent); } } else {
	 * 
	 * }
	 * 
	 * }
	 * 
	 * }
	 **/

	public void uploadFile(String localFileFullName, String fileName)
			throws Exception {
		InputStream input = new FileInputStream(new File(localFileFullName));
		this.ftp.storeFile(fileName, input);
	}

	public void uploadFile(String localFileFullName, String fileName,
			String hostDir) throws Exception {
		InputStream input = new FileInputStream(new File(localFileFullName));
		this.ftp.storeFile(hostDir + fileName, input);

	}

	public void makeDirectory(String pathName) throws IOException {
		this.ftp.makeDirectory(pathName);
	}

	public void changeCurrentDirectory(String pathName) throws IOException {
		this.ftp.changeWorkingDirectory(pathName);
	}

	public void UploadInputStream(InputStream input, String fileName,
			String directory) throws IOException {
		this.ftp.storeFile(directory + fileName, input);
	}

	public void disconnect() {
		if (this.ftp.isConnected()) {
			try {
				this.ftp.logout();
				this.ftp.disconnect();
			} catch (IOException f) {
				// do nothing as file is already saved to server
			}
		}
	}

	public static void main(String[] args) {
		try {
			FTPUtil ftpUtil = new FTPUtil("localhost", "", "hello123");
			System.out.println("main method");
			ftpUtil.restoreFile();
			System.out.println("inside method");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
