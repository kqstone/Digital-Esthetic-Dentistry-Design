package tk.kqstone.dedd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

public class NetTeethOptimizer extends TeethOptimizer {
	private static final String NET_ADRESS = Constant.DEFAULT_SERVER;
//	private static final String NET_ADRESS ="localhost";
	public static final int PORT = 12061;
	
	private Preferences userPreferences = Preferences.userRoot().node("/config/settings");

	@Override
	public void optimize() throws Exception {
		process(Teeth.FUNC_OPTIMIZE);
	}

	@Override
	public void align() throws Exception {
		process(Teeth.FUNC_ALIGN);
	}

	@Override
	public void analysis() {
		super.analysis();

	}

	private void process(int function) throws Exception {
		String netaddress = NET_ADRESS;
		String savedServerType = userPreferences.get("serverType", "default");
		if (!savedServerType.equals("default")) {
			netaddress = userPreferences.get("customServerAddress", NET_ADRESS);
        } 
		try (Socket sock = new Socket(netaddress, PORT)) {
			try (ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
					ObjectInputStream ois = new ObjectInputStream(sock.getInputStream())) {
				Teeth teeth = new Teeth();
				teeth.setTeeth((List<Tooth>) this.getSrc());
				teeth.setFun(function);
				oos.writeObject(teeth);
				oos.flush();

				Teeth dstTeeth = (Teeth) ois.readObject();
				this.dstTeeth = dstTeeth.getTeeth();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}
	}

}
