package tk.kqstone.dedd;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;

import tk.kqstone.dedd.utils.TFTPADapter;

public class MarkDataUpload {

	private static final String tmpDir = "tmp";
	private static final String remoteDir = "\\marked_teeth";

	private BufferedImage image;
	private List<String> markdata;
	private String id;
	private String name;

	private String fileNamePrefix;
	private TFTPADapter tFTPADapter;

	public MarkDataUpload() {
		fileNamePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +"-"+ Utils.getLocalMac() +"-";
		tFTPADapter = new TFTPADapter();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public void setMarkdata(List<String> markdata) {
		this.markdata = markdata;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void upload() {
		uploadImage();
		uploadLabel();
	}

	private void uploadLabel() {
		String labelFileName = fileNamePrefix + name + id;
//		String localFile = tmpDir + File.separator + labelFileName;
		String remoteFile = remoteDir + File.separator + labelFileName + ".txt";
		try {
			File tmpFile = File.createTempFile(labelFileName, ".txt");
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile))) {
				for (String s : markdata) {
					bw.write(s);
					bw.newLine();
				}
				bw.flush();
				tFTPADapter.uploadFileWithRuntimeProcess(remoteFile, tmpFile.getAbsolutePath());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private void uploadImage() {
		String imgFileName = fileNamePrefix + name + id;
//		String localFile = tmpDir + File.separator + imgFileName;
		String remoteFile = remoteDir + File.separator + imgFileName + ".jpg";
		try {
			File tmpFile = File.createTempFile(imgFileName, ".jpg");
			ImageIO.write(image, "jpg", tmpFile);
			tFTPADapter.uploadFileWithRuntimeProcess(remoteFile, tmpFile.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
