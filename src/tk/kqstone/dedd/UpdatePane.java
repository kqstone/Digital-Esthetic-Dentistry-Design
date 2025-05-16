package tk.kqstone.dedd;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.border.EmptyBorder;

import tk.kqstone.dedd.build.BuildInfo;
import tk.kqstone.dedd.utils.FTPConnector;
import javax.swing.Box;
import java.awt.Color;

public class UpdatePane extends JDialog {
	private static final String ADDR = "kqstone.myqnapcloud.com";
	private static final int PORT = 12021;
	private static final byte[] BYTES_USER_NAME = {102,116,112,95,117,115,101,114};
	private static final byte[] BYTES_USER_PWD = {102,116,112,95,52,49,53,55,48,51};
	private static final String REMOTE_PATH = "//dedd";
	private static final String TMP_PATH = "tmp";
	private static final String BIN_FILE = "dedd.bin";
	private static final String BIN_PATH = "bin";

	private JLabel lblUpdate;
	private JLabel lblLatestVer;
	private JButton btnUpdate;
	private JButton btnCancel;

	private boolean hasUpdate = false;

	private FTPConnector connector;

	private String latestVersion;
	
	private String currentVersion = BuildInfo.getVersion();

	public UpdatePane(Frame owner) {
		super(owner);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setType(Type.POPUP);
		BorderLayout borderLayout = (BorderLayout) getContentPane().getLayout();
		borderLayout.setVgap(5);

		JPanel buttonContainer = new JPanel();
		buttonContainer.setBackground(Color.LIGHT_GRAY);
		FlowLayout fl_buttonContainer = (FlowLayout) buttonContainer.getLayout();
		fl_buttonContainer.setHgap(30);
		getContentPane().add(buttonContainer, BorderLayout.SOUTH);

		btnUpdate = new JButton(Constant.UPDATE);
		buttonContainer.add(btnUpdate);
		btnUpdate.setEnabled(false);

		btnCancel = new JButton(Constant.CANCEL);
		buttonContainer.add(btnCancel);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(12, 20, 12, 20));
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		lblUpdate = new JLabel(Constant.CHECK_UPDATE_STATE);
		lblUpdate.setHorizontalAlignment(SwingConstants.CENTER);
		lblUpdate.setFont(new Font("宋体", Font.BOLD, 16));
		panel.add(lblUpdate);

		Component verticalStrut = Box.createVerticalStrut(12);
		panel.add(verticalStrut);

		JLabel lblCurrentVer = new JLabel(Constant.CURRENT_VERSION + currentVersion);
		lblCurrentVer.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblCurrentVer);

		lblLatestVer = new JLabel(Constant.LATEST_VERSION + Constant.CHECKING);
		lblLatestVer.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblLatestVer);
		pack();
		this.setLocationRelativeTo(owner);
		this.setVisible(true);

		ButtonListener bl = new ButtonListener();
		btnUpdate.addActionListener(bl);
		btnCancel.addActionListener(bl);

		connector = new FTPConnector(ADDR, String.valueOf(PORT), new String(BYTES_USER_NAME), new String(BYTES_USER_PWD)) {

			@Override
			protected void downloadBegain() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						lblUpdate.setText(Constant.UPDATE_PERCENT + "0%");
						btnUpdate.setEnabled(false);
					}

				});

			}

			@Override
			protected void downloading(final int percent) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						lblUpdate.setText(Constant.UPDATE_PERCENT + percent + "%");
					}

				});

			}

			@Override
			protected void downloadFinished() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						lblUpdate.setText(Constant.UPDATE_PERCENT + "100%");
					}

				});
			}
		};
	}

	public void checkUpdate() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String remoteBuildDate = getRemoteBuildDate();
				if (remoteBuildDate == null) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							lblLatestVer.setText(Constant.LATEST_VERSION);
							lblUpdate.setText(Constant.CONNECT_FAIL);
							UpdatePane.this.pack();
						}

					});
					return;
				}
				hasUpdate = isNew();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						lblLatestVer.setText(Constant.LATEST_VERSION + latestVersion);
						setUpdateableState(hasUpdate);
					}
				});
			}

		}).start();
	}
	
	private String getRemoteBuildDate() {
		if(!connector.connect())
			return null;
		String date = null;
		connector.downloadFile(REMOTE_PATH, "build_prop", TMP_PATH, "build_prop", false);
		Properties prop = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(TMP_PATH + File.separator + "build_prop");
			prop.load(is);
			date = prop.getProperty(BuildInfo.KEY_BUILD_DATE);
			latestVersion = prop.getProperty(BuildInfo.KEY_BUILD_VERSION);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			connector.disconnect();
		}
		
		return date;
		
	}
	

	private boolean isNew() {
		boolean is = false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentBuild=BuildInfo.getBuildDate();
		try {
			Date currentBuildDate = sdf.parse(currentBuild);
			Date remoteBuildDate = sdf.parse(getRemoteBuildDate());
			is = remoteBuildDate.after(currentBuildDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}

	@Deprecated
	private static boolean isNew(String latestVersion, String currentVersion) throws Exception {
		latestVersion = latestVersion.substring(1);
		currentVersion = currentVersion.substring(1);
		String[] ls = latestVersion.split("\\.");
		String[] cs = currentVersion.split("\\.");
		if (ls.length != cs.length)
			throw new Exception("version format exception");
		for (int i = 0; i < ls.length; i++) {
			if (Integer.parseInt(ls[i]) > Integer.parseInt(cs[i])) {
				return true;
			}
		}
		return false;
	}

	class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source.equals(btnUpdate)) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						boolean result = false;
						if (connector.connect()) {
							result = connector.downloadFile(REMOTE_PATH, BIN_FILE, TMP_PATH, BIN_FILE, true);
							connector.disconnect();
						}

						if (result) {
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									setSucessState();
								}

							});
						} else {
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									setFailState();
								}

							});
						}
					}
				}).start();

			} else if (source.equals(btnCancel)) {
				UpdatePane.this.dispose();
			}
		}
	}

	private void setUpdateableState(boolean updateable) {
		lblUpdate.setText(updateable ? Constant.UPDATEABLE : Constant.NO_UPDATE);
		btnUpdate.setEnabled(updateable);
		this.pack();
	}

	private void setSucessState() {
		lblUpdate.setText(Constant.UPDATE_SUCCESS);
		btnUpdate.setVisible(false);
		btnCancel.setText(Constant.OK);
		this.pack();
	}

	private void setFailState() {
		lblUpdate.setText(Constant.UPDATE_FAIL);
		btnUpdate.setText(Constant.RETRY);
		this.pack();
	}

	/*
	 * public static void main(String[] args) { UpdatePane up = new
	 * UpdatePane(null); up.checkUpdate(); }
	 */

}
