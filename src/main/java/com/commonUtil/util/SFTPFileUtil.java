package com.commonUtil.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;


public class SFTPFileUtil {

	/**
	 * 连接sftp服务器
	 * 
	 * @param host
	 *            主机
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	public static ChannelSftp connect(String host, int port, String username, String password) {
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			Session sshSession = jsch.getSession(username, host, port);
			System.out.println("Session created.");
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			System.out.println("Session connected.");
			System.out.println("Opening Channel.");
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			System.out.println("Connected to " + host + ".");
		} catch (Exception e) {

		}
		return sftp;
	}

	/**
	 * 上传文件
	 * 
	 * @param directory
	 *            上传的目录
	 * @param uploadFile
	 *            要上传的文件
	 * @param sftp
	 */
	public static void upload(String directory, String uploadFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			File file = new File(uploadFile);
			sftp.put(new FileInputStream(file), file.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param directory
	 *            下载目录
	 * @param downloadFile
	 *            下载的文件
	 * @param saveFile
	 *            存在本地的路径
	 * @param sftp
	 */
	public static void download(String directory, String downloadFile, String saveFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			File file = new File(saveFile);
			sftp.get(downloadFile, new FileOutputStream(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param directory
	 *            要删除文件所在目录
	 * @param deleteFile
	 *            要删除的文件
	 * @param sftp
	 */
	public static void delete(String directory, String deleteFile, ChannelSftp sftp) {
		try {
			sftp.cd(directory);
			sftp.rm(deleteFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory
	 *            要列出的目录
	 * @param sftp
	 * @return
	 * @throws SftpException
	 */
	public static Vector listFiles(String directory, ChannelSftp sftp) throws SftpException {
		return sftp.ls(directory);
	}
	public static void ftpDownLoad(String filePate, String filename, String ftpdir, String hostname, String pwd, String user, int port) {
		ChannelSftp sftp = connect(hostname, port, user, pwd);
		download(ftpdir, filename, filePate+filename, sftp);

	}
//	public static void main(String[] args) {
//		SFTPFileService sf = new SFTPFileService();
//		/*String host = "192.168.0.1";
//		int port = 22;
//		String username = "root";
//		String password = "root";
//		String directory = "/home/httpd/test/";
//		String uploadFile = "D:\\tmp\\upload.txt";
//		String downloadFile = "upload.txt";
//		String saveFile = "D:\\tmp\\download.txt";
//		String deleteFile = "delete.txt";*/
//		String localdir = "D:\\home\\epaysch\\sharefile\\checkfile\\tiancheng\\vas\\000000_20151230.txt";
//		String ftpdir = "/home/epaysch/vas/zywater/20151230";
//		String filename = "000000_20151230.txt";
//		String filename1 = "D:\\home\\epaysch\\sharefile\\checkfile\\tiancheng\\vas\\000000_20151230123.txt";
//		String hostname = "119.254.115.116";
//		String pwd = "BOco11jkl";
//		String user = "epaysch";
//		int port = 22;
//		ChannelSftp sftp = sf.connect(hostname, port, user, pwd);
//		sf.download(ftpdir, filename, localdir, sftp);
//		System.out.println("finished");
//		/*try {
//			sftp.cd(ftpdir);
//			sftp.mkdir("ss");
//			System.out.println("finished");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}*/
//	}
}
