package com.commonUtil.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

/**
 * ftp文件上传下载
 * 
 * @author chenxq
 * 
 */
public class FtpFileDealUtil {

	private final static Logger logger = Logger.getLogger(FtpFileDealUtil.class);
	private String user;// 用户名
	private String pwd;// 密码
	private String charset;// 编码集
	private String hostname;
	private int port;

	private FTPClient ftp;

	public FTPClient getFtp() {
		return ftp;
	}

	/**
	 * 
	 * @param hostname
	 *            地址
	 * @param port
	 *            端口
	 * @param user
	 *            用户名
	 * @param pwd
	 *            密码
	 */
	public FtpFileDealUtil(String hostname, int port, String user, String pwd) {
		this.user = user;
		this.pwd = pwd;
		this.charset = "utf-8";
		this.hostname = hostname;
		this.port = port;
	}

	public boolean login() throws IOException {
		this.ftp = new FTPClient();
		ftp.connect(this.hostname, this.port);
		return ftp.login(this.user, this.pwd);
	}

	public void loginOut() throws IOException {
		if (ftp != null)
			ftp.disconnect();
	}

	/**
	 * 文件上传
	 * 
	 * @param hostname
	 *            地址
	 * @param port
	 *            端口
	 * @param ftpdir
	 *            ftp目录
	 * @param localdir
	 *            本地目录
	 * @param filename
	 *            文件名
	 * @param ftpmode
	 *            ftp模式 PASV表示被动方式,PORT为主动方式(建议使用PASV)
	 * @return
	 */
	public boolean ftpUpload(String localdir, String filename, String ftpdir,
			String ftpmode) {
		logger.info("ftpdir=" + ftpdir + ",filename=" + filename + ",localdir="
				+ localdir);
		FileInputStream fis = null;
		try {
			if (!login()) {
				logger.info("login faile:hostname=" + hostname + ",port="
						+ port + ",user=" + user + ",pwd=" + pwd);
				return false;
			}
			makeDir(localdir);
			String filepath = localdir + filename;

			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (ftpmode.equals("PASV")) {// 上传模式
				ftp.enterLocalPassiveMode();
			} else {
				ftp.enterLocalActiveMode();
			}
			if (!createDir(ftpdir)) {
				return false;
			}
			fis = new FileInputStream(filepath);
			ftp.setBufferSize(1024);
			boolean isSuccess = ftp.storeFile(filename, fis);
			logger.info("isSuccess=" + isSuccess);
			return isSuccess;

		} catch (Exception e) {
			logger.error("ftp上传文件出现异常", e);
			return false;
		} finally {
			try {
				if (fis != null)
					fis.close();
				loginOut();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 文件下载
	 * 
	 * @param hostname
	 *            地址
	 * @param port
	 *            端口
	 * @param ftpdir
	 *            ftp目录
	 * @param localdir
	 *            本地目录
	 * @param filename
	 *            文件名
	 * @param ftpmode
	 *            ftp模式
	 * @return
	 */
	public boolean ftpDownload(String hostname, int port, String ftpdir,
			String localdir, String filename, String ftpmode) {
		FTPClient ftp = new FTPClient();
		FileOutputStream fos = null;
		makeDir(localdir);
		String filepath = localdir + filename;
		try {
			ftp.connect(hostname, port);
			ftp.login(user, pwd);
			if (ftpmode.equals("PASV")) {// /下载模式
				ftp.enterLocalPassiveMode();
			} else {
				ftp.enterLocalActiveMode();
			}
			boolean isdir = ftp.changeWorkingDirectory(ftpdir);
			fos = new FileOutputStream(filepath);
			ftp.setBufferSize(1024);
			ftp.setControlEncoding(charset);
			// 设置文件类型（二进制）
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			boolean isSuccess = ftp.retrieveFile(filename, fos);
			return isSuccess;
		} catch (Exception e) {
			logger.error("ftp下载文件出现异常", e);
			return false;
		} finally {
			try {
				fos.close();
				ftp.disconnect();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 在当前目录下创建文件夹
	 * 
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	public boolean createDir(String dir) {
		try {
			if (dir.contains(ftp.printWorkingDirectory())
					&& !"/".equals(ftp.printWorkingDirectory())) {
				dir = dir.replace(ftp.printWorkingDirectory(), "");
			}
			StringTokenizer s = new StringTokenizer(dir, "/"); // sign
			s.countTokens();
			String pathName = ftp.printWorkingDirectory();
			while (s.hasMoreElements()) {
				pathName = pathName + "/" + (String) s.nextElement();
				try {
					if (!ftp.changeWorkingDirectory(pathName)) {
						ftp.makeDirectory(pathName);
						ftp.changeWorkingDirectory(pathName);
					}
				} catch (Exception e) {
					logger.error(e);
					return false;
				}
			}
			return true;
		} catch (Exception e1) {
			logger.error(e1);
			return false;
		}
	}

	private void makeDir(String dir) {
		File fdir = new File(dir);
		if (!fdir.exists()) {
			fdir.mkdir();
		}
	}

	public static void main(String[] args) throws IOException {
		String hostname = "218.70.82.178";
		int port = 40621;
		String user = "liantiao";
		String pwd = "qq123#21";
		// String ftpdir = "/home/epaysch/mahai/res/20141014/";
		FtpFileDealUtil ftp = new FtpFileDealUtil(hostname, port, user, pwd);
		System.out.println(ftp.login());
		FTPClient ftpClient = ftp.getFtp();
		System.out.println(ftpClient.printWorkingDirectory());
		System.out.println(ftp
				.createDir("/ppsettle/settle/20130611020055113602/20141014/"));
		System.out.println(ftpClient.printWorkingDirectory());
		System.out
				.println(ftpClient
						.changeWorkingDirectory("/ppsettle/settle/20130611020055113602/20141014/"));
		System.out.println(ftpClient.printWorkingDirectory());
		// System.out.println(ftp.createDir(ftpdir));
		ftp.loginOut();

	}

}
