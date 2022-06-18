package tk.kqstone.dedd;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import tk.kqstone.dedd.utils.FTPConnector;

public final class Splash extends JFrame {
	private static final int WIDTH = 500;
	private static final int HEIGHT = 300;
	private static final String IMG_FILE = "/img/splash.png";
	private ImageView imageView;
	private JLabel label;
	private static String tip = Constant.STARTING;

	private static int count = 0;

	private static BufferedImage image;

	static {
		URL url = Splash.class.getResource(IMG_FILE);
		try {
			image = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Splash() {
		super();
		this.setType(Type.UTILITY);
		this.setUndecorated(true);
		this.setSize(WIDTH, HEIGHT);
		this.setBackground(new Color(0, 0, 0, 0));
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - WIDTH) / 2;
		int y = (screen.height - HEIGHT * 3 / 2) / 2;
		this.setLocation(x, y);
		this.getContentPane().setLayout(null);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		imageView = new ImageView();
		imageView.setImage(image);
		imageView.setBounds(0, 0, WIDTH, HEIGHT);
		this.getContentPane().add(imageView, -1);
		label = new JLabel(tip);
		label.setFont(new Font("等线", Font.BOLD, 15));
		label.setForeground(Color.DARK_GRAY);
		label.setBounds(WIDTH / 30, HEIGHT * 7 / 8, WIDTH, HEIGHT / 10);
		this.getContentPane().add(label, 0);
	}

	private void start() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (count >= 10) {
						Splash.this.dispose();
						break;

					}

					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (count % 5 == 0) {
								tip = Constant.STARTING;
							} else {
								tip += ". . ";
							}
							label.setText(tip);

						}
					});
					count++;

				}

			}
		});
		thread.start();
		Thread downloadThread = new Thread() {

			@Override
			public void run() {
				downloadTftpExe();
			}
			
		};
		downloadThread.start();
		
		UpdatePane up = new UpdatePane(null);
		if (up.isNew()) {
			up.setAlwaysOnTop(true);
			up.setVisible(true);
			up.checkUpdate();
		} 
		StartUp.startUI();

	}
	

	private static void downloadTftpExe() {
		String tftpExePath = Configuration.BINDIR + File.separator + Configuration.TFTP_EXE;
		File file = new File(tftpExePath);
		if (file.exists() && file.length() == 16896)
			return;
		FTPConnector connector = new FTPConnector(Configuration.SERVER_ADDR, String.valueOf(Configuration.FTP_PORT),
				new String(Configuration.BYTES_USER_NAME), new String(Configuration.BYTES_USER_PWD)) {

			@Override
			protected void downloadBegain() {
				// TODO Auto-generated method stub

			}

			@Override
			protected void downloading(int percent) {
				// TODO Auto-generated method stub

			}

			@Override
			protected void downloadFinished() {
				// TODO Auto-generated method stub

			}
		};
		connector.connect();
		try {
			connector.downloadFile(Configuration.REMOTE_BINDIR, Configuration.TFTP_EXE, Configuration.BINDIR,
					Configuration.TFTP_EXE, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			connector.disconnect();
		}

	}


	public static void main(String[] args) {
		Splash splash = new Splash();
		splash.start();
	}

}
