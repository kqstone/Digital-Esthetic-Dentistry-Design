package tk.kqstone.dedd;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import tk.kqstone.dedd.utils.FTPConnector;

public final class Splash extends JFrame {
	private final static LocalDate EXPIRE_DATE = LocalDate.of(2023, 10, 31);
	private static final String HASHCODE_UNEXPIRED_FILE_NAME = "e57875bfd30099af4eaf236dc6fd20cffd3e2596a298cb904ec7208d68c70dae";
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
					if (count >= 6) {
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
		
		if (!Utils.isConnectToInternet()) {
			JOptionPane.showMessageDialog(null, "无法连接至网络，请确认是否连接至因特网！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		boolean showUpdatePane = false;	
		
		if (!expirationDate(EXPIRE_DATE) && !unexpired()) {
			int r = JOptionPane.showConfirmDialog(null, Constant.EXPIRE_MESSAGE, Constant.PROG_NAME,
					JOptionPane.YES_NO_OPTION);			
			if (r == JOptionPane.YES_OPTION) {
				showUpdatePane = true;
			} else {
				return;
			}
		}
		
		Updater up = new Updater(null);
		if (showUpdatePane || up.isNew()) {
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
	
	private static boolean expirationDate(LocalDate expireDate) {
		LocalDate dateNow = LocalDate.now();
		if (dateNow.isAfter(expireDate)) {
			return false;
		} else {
			return true;
		}

	}
	
	
	private static boolean unexpired() {
		boolean unexpired = false;
		String[] filenames = new File(EnvVar.CONFIG_DIR).list();		
		for (String s:filenames) {
			if (!s.equals("aggrement.xml")) {
					String hashcode;
					try {
						hashcode = getHashCode(s);
						unexpired = hashcode.equals(HASHCODE_UNEXPIRED_FILE_NAME);
					} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			}	
		}
		return unexpired;
	}

	private static String getHashCode(String input) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(input.getBytes("UTF-8"));
        byte[] result = md.digest(); 
        return new BigInteger(1, result).toString(16);
	}

	public static void main(String[] args) {
		Splash splash = new Splash();
		splash.start();
	}

}
