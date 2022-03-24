package tk.kqstone.dedd;

import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Window.Type;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import tk.kqstone.dedd.build.BuildInfo;

import java.awt.Component;
import javax.swing.Box;

/**
 * About this Software
 * 
 * @author kqstone
 *
 */
public final class About extends JFrame {

	private static final String ABOUT_FILE_PATH = "/text/about";

	/**
	 * @param context Context to show this frame
	 */
	public About(Component context) {
		super();
		this.setTitle(Constant.ABOUT);
		setResizable(false);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setPreferredSize(new Dimension(450, 320));
		this.pack();

		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		JButton btnClose = new JButton(Constant.CLOSE);
		btnClose.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}

		});
		buttonPanel.add(btnClose);

		JPanel upperPanel = new JPanel();
		getContentPane().add(upperPanel, BorderLayout.NORTH);
		upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));

		Component horizontalStrut = Box.createHorizontalStrut(8);
		upperPanel.add(horizontalStrut);

		Component verticalStrut = Box.createVerticalStrut(8);
		upperPanel.add(verticalStrut);

		JLabel projName = new JLabel(Constant.PROG_NAME);
		projName.setHorizontalAlignment(SwingConstants.LEFT);
		projName.setFont(new Font("等线", Font.BOLD, 18));
		upperPanel.add(projName);

		Component verticalStrut_1 = Box.createVerticalStrut(8);
		upperPanel.add(verticalStrut_1);

		JLabel progVersion = new JLabel(Constant.VERSION_NAME + ": " + BuildInfo.getVersion());
		progVersion.setFont(new Font("等线", Font.PLAIN, 13));
		upperPanel.add(progVersion);

		JLabel progAuthor = new JLabel(Constant.PROG_AUTHOR);
		progAuthor.setFont(new Font("等线", Font.PLAIN, 13));
		upperPanel.add(progAuthor);

		JLabel progEmail = new JLabel(Constant.PROG_EMAIL);
		progEmail.setFont(new Font("等线", Font.PLAIN, 13));
		upperPanel.add(progEmail);

		Component verticalStrut_2 = Box.createVerticalStrut(10);
		upperPanel.add(verticalStrut_2);

		JTextPane progInfo = new JTextPane();
		progInfo.setFont(new Font("宋体", Font.PLAIN, 13));
		progInfo.setEditable(false);
		progInfo.setOpaque(false);
		getContentPane().add(progInfo, BorderLayout.CENTER);

		Component horizontalStrut_1 = Box.createHorizontalStrut(5);
		getContentPane().add(horizontalStrut_1, BorderLayout.WEST);
		this.setLocationRelativeTo(context);

		this.setVisible(true);

		InputStream input = this.getClass().getResourceAsStream(ABOUT_FILE_PATH);
		try {
			progInfo.setText(Utils.readText(input, "UTF-8"));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
