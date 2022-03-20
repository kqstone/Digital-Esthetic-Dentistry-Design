package tk.kqstone.dedd.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

import javax.swing.SwingUtilities;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public abstract class FTPConnector {

	private FTPClient ftpClient;

	private String host;
	private int port;
	private String username;
	private String password;

	public FTPConnector(String host, String port, String username, String password) {
		this.host = host;
		this.port = Integer.parseInt(port);
		this.username = username;
		this.password = password;
		ftpClient = new FTPClient();
	}

	public boolean connect() {
		boolean result = false;
		try {
			ftpClient.connect(host, port);
			ftpClient.enterLocalPassiveMode();

			result = ftpClient.login(username, password);

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void disconnect() {
		try {
			ftpClient.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean downloadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
		boolean result = false;
		CountOutputStream countingoutputStream = null;
		File file = null;
		try {
			file = new File(localPath + File.separator + localFileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdir();
			}
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			// 切换文件路径
			ftpClient.changeWorkingDirectory(remotePath);

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			FTPFile[] ftpFiles = ftpClient.listFiles();
			for (FTPFile ftpFile : ftpFiles) {
				if (ftpFile.getName().equals(remoteFileName)) {
					final long ftpFileSize = ftpFile.getSize();
					// LogUtils.log(">>>>>>>>FTP-->downloadFile--文件大小：" + ftpFileSize);
					OutputStream os = new FileOutputStream(file);
					BufferedOutputStream bos = new BufferedOutputStream(os, 1024 * 512);
					countingoutputStream = new CountOutputStream(bos);
					countingoutputStream.addWriteByteListener(new WriteByteListener() {

						@Override
						public void byteWrited(long count) {
							FTPConnector.this.downloading((int) (100 * count / ftpFileSize));
						}

					});
					FTPConnector.this.downloadBegain();
					result = ftpClient.retrieveFile(ftpFile.getName(), countingoutputStream);
					if (result) {
						countingoutputStream.flush();
						FTPConnector.this.downloadFinished();
					}

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			try {
				if (countingoutputStream != null)
					countingoutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;

	}

	public String getLatestFTPFile(String remotePath, String remoteFilePrefix) {
		String name = null;
		try {
			// 切换文件路径
			ftpClient.changeWorkingDirectory(remotePath);

			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			FTPFile[] ftpFiles = ftpClient.listFiles();
			FTPFile tmp = null;
			for (FTPFile ftpFile : ftpFiles) {
				if (!ftpFile.getName().startsWith(remoteFilePrefix))
					continue;
				if (tmp == null) {
					tmp = ftpFile;
				} else {
					if (ftpFile.getTimestamp().after(tmp.getTimestamp())) {
						tmp = ftpFile;
					}
				}
			}

			if (tmp != null) {
				name = tmp.getName();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return name;

	}

	public void uploadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {

		FileInputStream inputStream = null;
		try {
			// 切换文件路径
			ftpClient.makeDirectory(remotePath);
			ftpClient.changeWorkingDirectory(remotePath);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			inputStream = new FileInputStream(new File(localPath + localFileName));
			// 可上传多文件
			boolean isUpload = ftpClient.storeFile(remoteFileName, inputStream);

			if (isUpload) {
				// LogUtils.log(">>>>>>>>FTP-->uploadFile--文件上传成功!");
			} else {
				// LogUtils.log(">>>>>>>>FTP-->uploadFile--文件上传失败!");
				throw new RuntimeException("文件上传失败!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	abstract protected void downloadBegain();

	abstract protected void downloading(int percent);

	abstract protected void downloadFinished();

}
