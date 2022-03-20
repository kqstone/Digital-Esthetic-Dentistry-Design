package tk.kqstone.dedd;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.ScrollPaneConstants;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JSplitPane;
import javax.swing.Box;
import java.awt.Container;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ResultPanel extends JScrollPane {

	private JPanel contentPane;
	private JLabel lblTitle;
	private JLabel name;
	private JLabel gender;
	private JLabel age;
	private JLabel phoneNumber;
	private JLabel date;

	private JLabel lblDesign;
	private MarkableImageView imageDesign;

	private JLabel lblBaseCompare;
	private JPanel panel;
	private ImageView baseImageBefore;
	private ImageView baseImageAfter;
	private JPanel frontCompareView;
	private JLabel lblFrontCompare;
	private JPanel panel_1;
	private ImageView frontImageBefore;
	private ImageView frontImageAfter;
	private JPanel buttonPanel;

	public ResultPanel() {
		setViewportBorder(null);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		contentPane = new JPanel();
		setViewportView(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));

		lblTitle = new JLabel(Constant.TITLE_DESIGN_SCHEME);
		lblTitle.setFont(new Font("微软雅黑", Font.BOLD, 22));
		lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblTitle);

		JPanel infoPanel = new JPanel();
		contentPane.add(infoPanel);
		infoPanel.setLayout(new BorderLayout(0, 0));

		JPanel patientInfo = new JPanel();
		infoPanel.add(patientInfo, BorderLayout.WEST);
		patientInfo.setLayout(new BoxLayout(patientInfo, BoxLayout.Y_AXIS));

		name = new JLabel();
		patientInfo.add(name);

		gender = new JLabel();
		patientInfo.add(gender);

		age = new JLabel();
		patientInfo.add(age);

		phoneNumber = new JLabel();
		patientInfo.add(phoneNumber);

		date = new JLabel();
		date.setVerticalAlignment(SwingConstants.BOTTOM);
		this.setDate();
		infoPanel.add(date, BorderLayout.EAST);

		contentPane.add(Box.createVerticalStrut(15));

		JPanel designView = new JPanel();
		contentPane.add(designView);
		designView.setLayout(new BorderLayout(0, 0));

		lblDesign = new JLabel(Constant.DESIGN_LABEL);
		lblDesign.setFont(new Font("宋体", Font.BOLD, 16));
		lblDesign.setHorizontalAlignment(SwingConstants.CENTER);
		designView.add(lblDesign, BorderLayout.NORTH);

		imageDesign = new MarkableImageView();
		designView.add(imageDesign, BorderLayout.CENTER);

		contentPane.add(Box.createVerticalStrut(15));

		JPanel baseCompareView = new JPanel();
		contentPane.add(baseCompareView);
		baseCompareView.setLayout(new BorderLayout(0, 0));

		lblBaseCompare = new JLabel("口内对比图");
		lblBaseCompare.setFont(new Font("宋体", Font.BOLD, 16));
		lblBaseCompare.setHorizontalAlignment(SwingConstants.CENTER);
		baseCompareView.add(lblBaseCompare, BorderLayout.NORTH);

		panel = new JPanel();
		baseCompareView.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));

		baseImageBefore = new ImageView();
		panel.add(baseImageBefore);

		baseImageAfter = new ImageView();
		panel.add(baseImageAfter);

		contentPane.add(Box.createVerticalStrut(15));

		frontCompareView = new JPanel();
		contentPane.add(frontCompareView);
		frontCompareView.setLayout(new BorderLayout(0, 0));

		lblFrontCompare = new JLabel("正面对比图");
		lblFrontCompare.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrontCompare.setFont(new Font("宋体", Font.BOLD, 16));
		frontCompareView.add(lblFrontCompare, BorderLayout.NORTH);

		panel_1 = new JPanel();
		frontCompareView.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.LINE_AXIS));

		frontImageBefore = new ImageView();
		panel_1.add(frontImageBefore);

		frontImageAfter = new ImageView();
		panel_1.add(frontImageAfter);

		Border border = BorderFactory.createLineBorder(Color.BLUE);
		imageDesign.setBorder(border);
		baseImageBefore.setBorder(border);
		baseImageAfter.setBorder(border);
		frontImageBefore.setBorder(border);
		frontImageAfter.setBorder(border);

		contentPane.add(Box.createVerticalStrut(15));

		buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		contentPane.add(buttonPanel);

		final JButton saveButton = new JButton(Constant.SAVE);
		saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setSelectedFile(new File(GlobalVariables.sWorkingDir + " "));
				chooser.setFileFilter(new FileNameExtensionFilter("图片文件(.jpg)", "jpg"));
				int result = chooser.showSaveDialog(contentPane);
				if (result == JFileChooser.APPROVE_OPTION) {
					GlobalVariables.sWorkingDir = chooser.getSelectedFile().getParent() + File.separator;
					String path;
					try {
						path = chooser.getSelectedFile().getCanonicalPath();
						if (!path.endsWith(".jpg"))
							path += ".jpg";
						saveButton.setVisible(false);
						BufferedImage image = new BufferedImage(contentPane.getWidth(), contentPane.getHeight(),
								BufferedImage.TYPE_INT_RGB);
						Graphics g = image.getGraphics();
						contentPane.print(g);
						g.dispose();
						saveButton.setVisible(true);
						ImageIO.write(image, "jpg", new File(path));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		buttonPanel.add(saveButton);

	}

	public void init() {
		this.revalidate();
		this.repaint();
	}

	private void setImage(ImageView view, BufferedImage image) {
		int ImgWidth = image.getWidth();
		int ImgHeight = image.getHeight();
		int width = view.getWidth();
		int height = ImgHeight * width / ImgWidth;
		view.setPreferredSize(new Dimension(width, height));
		view.setImage(image);
		view.revalidate();
	}

	public void setDesignImage(BufferedImage image) {
		setImage(imageDesign, image);
	}

	public void initMarkPanel(List<SectScale> sectScales) {
		imageDesign.initMark();
		int imageWidth = imageDesign.getImage().getWidth();
		int panelWidth = imageDesign.getWidth();
		double ratio = panelWidth / (double) imageWidth;
//		List<SectScale> tmp = new ArrayList<>();
		for (SectScale sectScale : sectScales) {
			sectScale.x1 *= ratio;
			sectScale.x2 *= ratio;
			sectScale.scale /= ratio;

		}
		imageDesign.setSectScale(sectScales);
	}

	public void setBeforeBaseImage(BufferedImage image) {
		setImage(baseImageBefore, image);
	}

	public void setAfterBaseImage(BufferedImage image) {
		setImage(baseImageAfter, image);
	}

	public void setBeforeFrontImage(BufferedImage image) {
		setImage(frontImageBefore, image);
	}

	public void setAfterFrontImage(BufferedImage image) {
		setImage(frontImageAfter, image);
	}

	public void setName(String name) {
		this.name.setText(Constant.NAME + ": " + name);
	}

	public void setGender(Gender gender) {
		this.gender.setText(Constant.GENDER + ": " + (gender.value));
	}

	public void setAge(int age) {
		String strAge = age == 0 ? "未知" : String.valueOf(age);
		this.age.setText(Constant.AGE + ": " + strAge);
	}

	public void setDate() {
		LocalDate dateNow = LocalDate.now();
		this.date.setText(Constant.DATE + ": " + dateNow.toString());
	}

	public void setPhone(String phone) {
		this.phoneNumber.setText(Constant.PHONE + ": " + phone);
	}

}
