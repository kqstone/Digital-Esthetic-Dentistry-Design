package tk.kqstone.dedd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class NetTeethOptimizer extends TeethOptimizer {
	private static final String NET_ADRESS = "kqstone.myqnapcloud.com";
//	private static final String NET_ADRESS ="localhost";
	public static final int PORT = 12061;

	@Override
	public void optimize() {
		process(Teeth.FUNC_OPTIMIZE);
	}

	@Override
	public void align() {
		process(Teeth.FUNC_ALIGN);
	}

	@Override
	public void analysis() {
		// TODO Auto-generated method stub

	}

	private void process(int function) {
		try (Socket sock = new Socket(NET_ADRESS, PORT)) {
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
		}
	}

}
