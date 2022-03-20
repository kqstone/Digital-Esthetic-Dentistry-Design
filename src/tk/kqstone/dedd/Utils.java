package tk.kqstone.dedd;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
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

}
