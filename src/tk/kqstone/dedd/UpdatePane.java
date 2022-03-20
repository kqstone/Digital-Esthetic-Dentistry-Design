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
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.border.EmptyBorder;

import org.apache.commons.net.ftp.FTPClient;

import tk.kqstone.dedd.utils.FTPConnector;
import tk.kqstone.dedd.utils.LogUtils;

import javax.swing.Box;
import java.awt.Color;

public class UpdatePane extends JDialog {
	private static final String ADDR = "kqstone.myqnapcloud.com";
	private static final int PORT = 12021;
	private static final String USER_NAME = "ftp_user";
	private static final String USER_PWD = "ftp_qnap";
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

		JLabel lblCurrentVer = new JLabel(Constant.CURRENT_VERSION + Constant.VERSION);
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

		connector = new FTPConnector(ADDR, String.valueOf(PORT), USER_NAME, USER_PWD) {

			@Override
			protected void downloadBegain() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						lblUpdate.setText(Constant.UPDATE_PERCENT + "0%");
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
				if (!connector.connect()) {
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
				String latestName = connector.getLatestFTPFile(REMOTE_PATH, BIN_FILE);
				connector.disconnect();
				if (latestName != null) {
					latestVersion = latestName.substring(BIN_FILE.length());
					try {
						hasUpdate = isNew(latestVersion, Constant.VERSION);
					} catch (Exception e) {
						e.printStackTrace();
					}

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							lblLatestVer.setText(Constant.LATEST_VERSION + latestVersion);
							setUpdateableState(hasUpdate);
						}

					});
				}
			}

		}).start();
	}

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
							result = connector.downloadFile(REMOTE_PATH, BIN_FILE + latestVersion, TMP_PATH, BIN_FILE);
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
	}

	private void setFailState() {
		lblUpdate.setText(Constant.UPDATE_FAIL);
		btnUpdate.setText(Constant.RETRY);
	}

	public static void main(String[] args) {
		UpdatePane up = new UpdatePane(null);
		up.checkUpdate();
	}

}
