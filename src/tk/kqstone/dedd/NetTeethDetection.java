package tk.kqstone.dedd;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NetTeethDetection implements ITeethDetection {

	private static final String NET_ADRESS = Constant.DEFAULT_SERVER;
	private static final String LOCAL_NET_ADRESS = "localhost";
	private static final File tmpFile = new File("/tmp/tmp.jpg");
	public static final int PORT = 12062;

	private String netaddress;
	private int port;
	private String type;//图片种类，口内照(intraoral)、面部照(face)

	public NetTeethDetection() {
		netaddress = NET_ADRESS;
		port = PORT;
	}

	public NetTeethDetection(String netaddress, int port) {
		this.netaddress = netaddress;
		this.port = port;
	}
	
	public NetTeethDetection(String netaddress, int port, String type) {
		this(netaddress, port);
		this.type = type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public List<Rectangle> detectTeeth(BufferedImage image) throws Exception {
		List<Rectangle> rects = null;
		try (Socket sock = new Socket(netaddress, port)) {
			try (OutputStream bos = sock.getOutputStream();
					ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {
				bos.write(this.type.getBytes());
				
				File tmpFile = File.createTempFile("tmp_pic", ".jpg");
				ImageIO.write(image, "jpg", tmpFile);
				FileInputStream fis = new FileInputStream(tmpFile);
				int len = 0;
		        byte[] bytes = new byte[1024];
		        while ((len = fis.read(bytes)) > 0){
		            bos.write(bytes,0,len);
		            bos.flush();
		        }
		        sock.shutdownOutput();

				String s = ois.readUTF();
				Gson gson = new Gson();
				Type type = new TypeToken<ArrayList<Rectangle>>(){}.getType();
				rects = gson.fromJson(s, type);				

		        fis.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}
		return rects;
	}

}
