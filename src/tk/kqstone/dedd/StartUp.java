package tk.kqstone.dedd;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class StartUp extends JFrame {
	private final static LocalDate EXPIRE_DATE = LocalDate.of(2021, 5, 31);

	private static final String AGREMENT_FILE_PATH = "/text/aggrement";
	private static final File configFile = new File("config" + File.separator + "aggrement.xml");
	private static final File unexpiredFile = new File("config" + File.separator + "unexpired_dedd");

	public StartUp() {
		super();
		this.setTitle(Constant.PROG_NAME);
		setResizable(false);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setPreferredSize(new Dimension(600, 400));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x1 = (screenSize.width - 600) / 2;
		int y1 = (screenSize.height - 400) / 2;
		this.setLocation(x1, y1);
		this.pack();

		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		JButton btnAccept = new JButton(Constant.ACCEPT);
		btnAccept.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				File dir = configFile.getParentFile();
				if (!dir.exists())
					dir.mkdir();

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db;
				try {
					db = dbf.newDocumentBuilder();
					Document doc = db.newDocument();
					Element agreement = doc.createElement("user_agreement");
					Element agreed = doc.createElement("agreed");
					agreed.setTextContent("accept");
					agreement.appendChild(agreed);
					doc.appendChild(agreement);
					TransformerFactory tff = TransformerFactory.newInstance();
					Transformer tf = tff.newTransformer();
					tf.setOutputProperty(OutputKeys.INDENT, "yes");
					tf.transform(new DOMSource(doc), new StreamResult(configFile));
				} catch (ParserConfigurationException | TransformerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Controller.start();
				dispose();
			}

		});
		buttonPanel.add(btnAccept);

		JButton btnRefuse = new JButton(Constant.REFUSE);
		btnRefuse.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}

		});
		buttonPanel.add(btnRefuse);

		JPanel upperPanel = new JPanel();
		getContentPane().add(upperPanel, BorderLayout.NORTH);
		upperPanel.setLayout(new BorderLayout());

		JLabel titleAgreement = new JLabel(Constant.USER_AGREMENT);
		titleAgreement.setHorizontalAlignment(SwingConstants.CENTER);
		titleAgreement.setFont(new Font("等线", Font.BOLD, 18));
		upperPanel.add(titleAgreement);

		Component verticalStrut = Box.createVerticalStrut(10);
		upperPanel.add(verticalStrut, BorderLayout.NORTH);

		Component verticalStrut_1 = Box.createVerticalStrut(10);
		upperPanel.add(verticalStrut_1, BorderLayout.SOUTH);

		JTextPane agreementText = new JTextPane();
		agreementText.setFont(new Font("宋体", Font.PLAIN, 12));
		agreementText.setEditable(false);
		agreementText.setOpaque(false);
		getContentPane().add(agreementText, BorderLayout.CENTER);

		Component horizontalStrut_1 = Box.createHorizontalStrut(5);
		getContentPane().add(horizontalStrut_1, BorderLayout.WEST);
		Component horizontalStrut = Box.createHorizontalStrut(5);
		getContentPane().add(horizontalStrut, BorderLayout.EAST);

		this.setVisible(true);

		InputStream input = this.getClass().getResourceAsStream(AGREMENT_FILE_PATH);
		try {
			String text = Utils.readText(input, "UTF-8");
			agreementText.setText(text);

		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public static void startUI() {
		if (!unexpiredFile.exists()) {
			if (!expirationDate(EXPIRE_DATE)) {
				JOptionPane.showMessageDialog(null, Constant.EXPIRE_MESSAGE, Constant.PROG_NAME,
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		boolean agreed = false;
		if (configFile.exists()) {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc = db.parse(configFile);
				NodeList nodelist = doc.getElementsByTagName("user_agreement").item(0).getChildNodes();
				for (int i = 0; i < nodelist.getLength(); i++) {
					if (nodelist.item(i).getNodeName().equals("agreed")) {
						if (nodelist.item(i).getTextContent().equals("accept")) {
							agreed = true;
						}
					}
				}
			} catch (ParserConfigurationException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}

		if (agreed) {
			Controller.start();
		} else {
			new StartUp();
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

}
