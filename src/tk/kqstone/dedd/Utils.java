package tk.kqstone.dedd;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.filechooser.FileSystemView;

public class Utils {

	public static String readText(InputStream inputStream, String encoding) throws IOException {
		StringBuilder text = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStreamReader read = new InputStreamReader(inputStream, encoding);
			bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				text.append(lineTxt);
				text.append("\n");
			}
			return text.toString();
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}
	}

	public static String getUsrDocumentDir() {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		String dir = null;
		try {
			dir = fsv.getDefaultDirectory().getCanonicalPath() + File.separator;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dir;
	}

	public static String getPrjDir() {
		return Utils.getUsrDocumentDir() + Constant.PROJ_DIR + File.separator;
	}

	public static String formatString(double number) {
		NumberFormat fmt = new DecimalFormat("#0.00");
		return fmt.format(number);
	}

	public static void zip(File zipFile, List<File> files) throws IOException {
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new FileOutputStream(zipFile));
			for (File file : files) {
				if (!file.exists())
					continue;
				zos.putNextEntry(new ZipEntry(file.getName()));
				BufferedInputStream inputStream = null;
				try {
					inputStream = new BufferedInputStream(new FileInputStream(file));
					int count;
					byte[] data = new byte[1024];
					while ((count = inputStream.read(data, 0, 1024)) != -1) {
						zos.write(data, 0, count);
					}

					zos.closeEntry();
				} finally {
					if (inputStream != null)
						inputStream.close();
				}
			}
		} finally {
			if (zos != null)
				zos.close();
		}
	}

	public static void unzip(File zipFile, List<File> files) throws IOException {
		ZipInputStream zis = null;
		String dir = zipFile.getParent();
		try {
			zis = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry = null;
			while ((entry = zis.getNextEntry()) != null) {
				String name = entry.getName();
				File file = new File(dir + File.separator + name);
				files.add(file);
				BufferedOutputStream bos = null;
				try {
					bos = new BufferedOutputStream(new FileOutputStream(file));
					int count;
					byte data[] = new byte[1024];
					while ((count = zis.read(data, 0, 1024)) != -1) {
						bos.write(data, 0, count);
					}
				} finally {
					if (bos != null)
						bos.close();
				}

			}
		} finally {
			if (zis != null)
				zis.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deepCloneObject(Object object) throws IOException {
		T deepClone = null;
		ObjectInputStream ois = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);) {
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ois = new ObjectInputStream(bais);
			deepClone = (T) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
		return deepClone;
	}
	
	
	public static String getLocalMac() {
		try {
			// 获取网卡，获取地址
			byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mac.length; i++) {
				if (i != 0) {
					sb.append("-");
				}
				// 字节转换为整数
				int temp = mac[i] & 0xff;
				String str = Integer.toHexString(temp);
				if (str.length() == 1) {
					sb.append("0" + str);
				} else {
					sb.append(str);
				}
			}
			return sb.toString();
		} catch (Exception exception) {
		}
		return null;
	}
	
	public static String cnToUnicode(String cn) {
		if (cn == null || cn == "") {
			return "null";
		}
        char[] chars = cn.toCharArray();
        StringBuilder returnStr = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            returnStr.append(Integer.toString(chars[i], 16));
        }
        return returnStr.toString();
    }
	
	public static boolean isConnectToInternet() {
		boolean r = false;
		try {
	         URL url = new URL("https://www.baidu.com");
	         URLConnection connection = url.openConnection();
	         connection.connect();
	         System.out.println("Internet is connected");
	         r = true;
	      } catch (MalformedURLException e) {
	         System.out.println("Internet is not connected");
	      } catch (IOException e) {
	         System.out.println("Internet is not connected");
	      }
		return r;
	}

}
