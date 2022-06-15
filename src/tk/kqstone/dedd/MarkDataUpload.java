package tk.kqstone.dedd;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class MarkDataUpload {

	private static final String NET_ADRESS = "kqstone.myqnapcloud.com";
	private static final String LOCAL_NET_ADRESS = "localhost";
	private static final File tmpDir = new File("/tmp/");
	public static final int PORT = 12069;

	private String netaddress;
	private int port;
	private BufferedImage image;
	private List<String> markdata;
	private String id;

	public MarkDataUpload() {
		netaddress = NET_ADRESS;
		port = PORT;
	}

	public MarkDataUpload(String netaddress, int port) {
		this.netaddress = netaddress;
		this.port = port;
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


	public void upload() {
		String date = LocalDate.now().toString();
		String filenamePrefix = Utils.getLocalMac() + LocalDate.now().toString() + id;
		File labelFile = new File(tmpDir + filenamePrefix + ".txt");
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(labelFile))) {
			for (String s:markdata) {
				bw.write(s);
				bw.newLine();
				bw.flush();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		
	}

}
