package tk.kqstone.dedd.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.tftp.TFTP;
import org.apache.commons.net.tftp.TFTPClient;

import tk.kqstone.dedd.Configuration;

public class TFTPADapter {
	private static final int DEFAULT_TIMEOUT = 10000;
	private static final String DEFAULT_HOST = Configuration.SERVER_ADDR;
	private static final int DEFAULT_PORT = Configuration.TFTP_PORT;
	private TFTPClient tftp;
	private String hostname;
	private int port;

	public TFTPADapter() {
		tftp = new TFTPClient();
		tftp.setMaxTimeouts(DEFAULT_TIMEOUT);
		this.hostname = DEFAULT_HOST;
		this.port = DEFAULT_PORT;
	}

	public TFTPADapter(String hostname, int port) {
		this();
		this.hostname = hostname;
		this.port = port;
	}

	public boolean downloadFile(String localFilename, String remoteFilename) {

		// 打开本地socket
		try {
			tftp.open();
		} catch (SocketException e) {
			System.err.println("无法打开本地 UDP socket!");
			System.err.println(e.getMessage());
		}

		boolean closed, success;
		closed = false;
		success = false;
		FileOutputStream output = null;
		File file;

		file = new File(localFilename);
		if (file.exists()) {
			System.err.println("文件: " + localFilename + " 已经存在!");
			return success;
		}

		try {
			output = new FileOutputStream(file);
		} catch (IOException e) {
			tftp.close();
			System.err.println("无法打开要写入的本地文件!");
			System.err.println(e.getMessage());
			return success;
		}

		try {
			tftp.receiveFile(remoteFilename, TFTP.BINARY_MODE, output, hostname, port);
			// tftp.receiveFile(remoteFilename, TFTP.BINARY_MODE, output, hostname);
			success = true;
		} catch (UnknownHostException e) {
			System.err.println("无法解析主机!");
			System.err.println(e.getMessage());
			return success;
		} catch (IOException e) {
			System.err.println("接收文件时有I/O异常!");
			System.err.println(e.getMessage());
			return success;
		} finally {
			// 关闭本地 socket 和输出的文件
			tftp.close();
			try {
				if (null != output) {
					output.close();
				}
				closed = true;
			} catch (IOException e) {
				closed = false;
				System.err.println("关闭文件时出错!");
				System.err.println(e.getMessage());
			}
		}
		if (!closed)
			return false;

		return success;
	}

	public boolean uploadFile(String remoteFilename, InputStream input) {

		// 打开本地socket
		try {
			tftp.open();
		} catch (SocketException e) {
			System.err.println("无法打开本地 UDP socket!");
			System.err.println(e.getMessage());
		}

		boolean success, closed;
		closed = false;
		success = false;

		try {
			tftp.sendFile(remoteFilename, TFTP.BINARY_MODE, input, hostname, port);
			success = true;
		} catch (UnknownHostException e) {
			System.err.println("无法解析主机!");
			System.err.println(e.getMessage());
			return success;
		} catch (IOException e) {
			System.err.println("发送文件时有I/O异常!");
			System.err.println(e.getMessage());
			return success;
			// System.exit(1);
		} finally {
			// 关闭本地 socket 和输出的文件
			tftp.close();
			try {
				if (null != input) {
					input.close();
				}
				closed = true;
			} catch (IOException e) {
				closed = false;
				System.err.println("关闭文件时出错!");
				System.err.println(e.getMessage());
			}
		}

		if (!closed)
			return false;

		return success;
	}

	public boolean uploadFileWithRuntimeProcess(String remoteFilename, String localFilename) {
		StringBuilder sb = new StringBuilder();
		sb.append(Configuration.BINDIR).append(File.separator).append(Configuration.TFTP_EXE).append(" -i ").append(hostname).append(" put ").append(localFilename).append(" ").append(remoteFilename);
		String cmd = sb.toString();
		try {			
		    Process process = Runtime.getRuntime().exec(cmd);
		    return true;
		} catch (IOException e) {
		    e.printStackTrace();
		    return false;
		}
	}
	
	public boolean uploadFile(String remoteFilename, String localFilename) {
		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(localFilename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return uploadFile(remoteFilename, fileInput);
	}

	public boolean uploadDir(String remoteDir, String localDir) {
		File dir = new File(localDir);
		File[] files = dir.listFiles(); 
		boolean result = true;
		for (File file : files) {
			String filename = file.getAbsolutePath();
			if (!uploadFileWithRuntimeProcess(remoteDir + filename, filename))
				result = false;
		}
		return result;
	}
}