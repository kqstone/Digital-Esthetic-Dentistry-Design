package tk.kqstone.dedd;

public class Configuration {
	public static final String SERVER_ADDR = "kqstone.myqnapcloud.com";
	public static final int FTP_PORT = 12021;
	public static final int TFTP_PORT = 69;
	public static final byte[] BYTES_USER_NAME = { 102, 116, 112, 95, 117, 115, 101, 114 }; // USER_NAME = "ftp_user";
	public static final byte[] BYTES_USER_PWD = { 102, 116, 112, 95, 52, 49, 53, 55, 48, 51 }; // USER_PWD =
																								// "ftp_415703";
	public static final String BINDIR = "bin";
	public static final String REMOTE_BINDIR ="//bin";
	public static final String TMPDIR = "tmp";
	public static final String TFTP_EXE = "tftp.exe";

}
