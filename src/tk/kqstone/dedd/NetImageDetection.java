package tk.kqstone.dedd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class NetImageDetection implements IImageDetection {

	private static final String NET_ADRESS = "kqstone.myqnapcloud.com";
//	private static final String NET_ADRESS ="localhost";
	public static final int PORT = 12061;
	
	private String netaddress;
	private int port;
	private Socket sock;

	public NetImageDetection() {
		netaddress = NET_ADRESS;
		port = PORT;
	}
	
	public NetImageDetection(String netaddress, int port) {
		this.netaddress = netaddress;
		this.port = port;
	}
	
	@Override
	public List<Rectangle> detectTeeth(BufferedImage image) {
		try (Socket sock = new Socket(NET_ADRESS, PORT)) {
			try (ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
					ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {
				oos.writeObject(image);
				oos.flush();

				String s = ois.readUTF();
				List<Rectangle> rects = JSON.parseObject("...", new TypeReference<List<Rectangle>>() {})
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}
		return null;
	}
	

}
