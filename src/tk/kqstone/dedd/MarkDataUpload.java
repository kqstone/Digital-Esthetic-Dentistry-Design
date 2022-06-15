package tk.kqstone.dedd;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.JSON;

public class MarkDataUpload {

	private static final String NET_ADRESS = "kqstone.myqnapcloud.com";
	private static final String LOCAL_NET_ADRESS = "localhost";
	private static final File tmpFile = new File("/tmp/tmp.jpg");
	public static final int PORT = 12069;

	private String netaddress;
	private int port;
	private BufferedImage image;
	private Map<String, Rectangle2D> markdata;

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

	public void setMarkdata(Map<String, Rectangle2D> markdata) {
		this.markdata = markdata;
	}

	public void upload() {
		try (Socket sock = new Socket(netaddress, port)) {
			try (OutputStream os = sock.getOutputStream()) {
				File tmpFile = File.createTempFile("tmp_pic", ".jpg");
				ImageIO.write(image, "jpg", tmpFile);
				FileInputStream fis = new FileInputStream(tmpFile);
				int len = 0;
				byte[] bytes = new byte[1024];
				BufferedOutputStream bos = new BufferedOutputStream(os);
				while ((len = fis.read(bytes)) != -1) {
					bos.write(bytes, 0, len);
				}
				fis.close();
				bos.flush();
				
				String str = JSON.toJSONString(markdata);
				ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeUTF(str);
				oos.flush();
				sock.shutdownOutput();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
