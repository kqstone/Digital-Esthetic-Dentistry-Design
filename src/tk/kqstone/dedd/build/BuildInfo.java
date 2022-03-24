package tk.kqstone.dedd.build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import tk.kqstone.dedd.Constant;

public class BuildInfo {
	
	public static final String KEY_BUILD_VERSION = "build_version";
	public static final String BUILD_INFO_PATH = "src/build_prop";
	public static final String KEY_BUILD_DATE = "build_date";
	
	public static String getBuildDate() {
		String buildDate = null;
		File buildInfoFile = new File(BUILD_INFO_PATH);
		InputStream os = null;
		try {
			os = new FileInputStream(buildInfoFile);
			Properties prop = new Properties();
			prop.load(os);
			buildDate = String.valueOf(prop.get(KEY_BUILD_DATE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buildDate;
	}
	

	public static String getVersion() {
		return Constant.VERSION + "." + getBuildDate();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		setBuildInfo();

	}
	
	private static String genBuildDate() {
		Calendar cld = Calendar.getInstance();
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		return s.format(cld.getTime());
		
	}
	
	private static void setBuildInfo() {
		File buildInfoFile = new File(BUILD_INFO_PATH);
		if (!buildInfoFile.exists()) {
			try {
				buildInfoFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(buildInfoFile);
			Properties prop = new Properties();
			String buildDate = genBuildDate();
			prop.put(KEY_BUILD_DATE, buildDate);
			prop.put(KEY_BUILD_VERSION, Constant.VERSION+"."+buildDate);
			prop.store(os, null);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Deprecated
	private static void setBuildDate() {
		File buildInfoFile = new File(BUILD_INFO_PATH);
		if (!buildInfoFile.exists()) {
			try {
				buildInfoFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(buildInfoFile);
			Properties prop = new Properties();
			prop.put(KEY_BUILD_DATE, genBuildDate());
			prop.store(os, null);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
