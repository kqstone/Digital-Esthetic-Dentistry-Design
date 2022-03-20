package tk.kqstone.dedd;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JTextField;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JRadioButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import javax.swing.JButton;
import javax.swing.JDialog;

public class NewProjPane extends JDialog {
	private JLabel lblName;
	private JTextField txtName;
	private JLabel lblGender;
	private JLabel lblAge;
	private JTextField txtAge;
	private JLabel lblTel;
	private JTextField txtTel;
	private JRadioButton rdbtnMale;
	private JRadioButton rdbtnFemale;
	private final ButtonGroup buttonGroupGender = new ButtonGroup();
	private JTextField txtPath;
	private JButton btnPathSelection;
	private JButton btnOK;
	private JButton btnCancel;

	private File saveFile;

//	private ProjImpl projImpl;

	private IContext context;

//	public NewProjPane(JFrame frame, ProjImpl projImpl) {
//		this(frame);
//		this.projImpl = projImpl;
//	}

	public NewProjPane(JFrame frame, IContext context) {
		this(frame);
		this.context = context;
	}

	public NewProjPane(JFrame frame) {
		super(frame, Constant.TITLE_NEW_PROJ, true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setType(Type.POPUP);
		if (frame.getIconImage() != null)
			setIconImage(frame.getIconImage());
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		JPanel root = new JPanel();
		getContentPane().add(root, BorderLayout.CENTER);
		root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		root.setLayout(new BorderLayout(5, 5));

		JPanel buttonPane = new JPanel();
		root.add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));

		JLabel lblSavePath = new JLabel(Constant.SAVE_PATH);
		buttonPane.add(lblSavePath);

		txtPath = new JTextField();
		txtPath.setColumns(30);
		txtPath.setText(Utils.getPrjDir() + Constant.UNNAME + "." + Constant.PROJ_FILE_SUFFIX);
		buttonPane.add(txtPath);

		btnPathSelection = new JButton("...");
		buttonPane.add(btnPathSelection);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		buttonPane.add(horizontalStrut_1);

		btnOK = new JButton(Constant.OK);
		btnOK.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPane.add(btnOK);

		Component horizontalStrut = Box.createHorizontalStrut(5);
		buttonPane.add(horizontalStrut);

		btnCancel = new JButton(Constant.CANCEL);
		btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPane.add(btnCancel);

		JPanel infoPane = new JPanel();
		infoPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), Constant.BASICINFO,
				TitledBorder.CENTER, TitledBorder.TOP));
		root.add(infoPane, BorderLayout.CENTER);
		infoPane.setLayout(new BorderLayout(0, 0));

		JPanel infoCtnPane = new JPanel();
		infoPane.add(infoCtnPane, BorderLayout.CENTER);
		infoCtnPane.setLayout(new BoxLayout(infoCtnPane, BoxLayout.Y_AXIS));

		JPanel namePane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) namePane.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		infoCtnPane.add(namePane);

		lblName = new JLabel(Constant.NAME + ": ");
		namePane.add(lblName);

		txtName = new JTextField();
		namePane.add(txtName);
		txtName.setColumns(10);

		JPanel genderPane = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) genderPane.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		infoCtnPane.add(genderPane);

		lblGender = new JLabel(Constant.GENDER + ": ");
		genderPane.add(lblGender);

		rdbtnMale = new JRadioButton(Constant.MALE);
		buttonGroupGender.add(rdbtnMale);
		genderPane.add(rdbtnMale);

		rdbtnFemale = new JRadioButton(Constant.FEMALE);
		buttonGroupGender.add(rdbtnFemale);
		genderPane.add(rdbtnFemale);

		JPanel agePane = new JPanel();
		FlowLayout fl_agePane = (FlowLayout) agePane.getLayout();
		fl_agePane.setAlignment(FlowLayout.LEFT);
		infoCtnPane.add(agePane);

		lblAge = new JLabel(Constant.AGE + ": ");
		agePane.add(lblAge);

		txtAge = new JTextField();
		txtAge.setColumns(2);
		agePane.add(txtAge);

		JPanel phonePane = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) phonePane.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		infoCtnPane.add(phonePane);

		lblTel = new JLabel(Constant.PHONE + ": ");
		phonePane.add(lblTel);

		txtTel = new JTextField();
		txtTel.setColumns(13);
		phonePane.add(txtTel);

		Component verticalGlue = Box.createVerticalGlue();
		infoCtnPane.add(verticalGlue);

		pack();

		saveFile = new File(GlobalVariables.sWorkingDir + Constant.UNNAME + "." + Constant.PROJ_FILE_SUFFIX);

		ButtonClickListener listener = new ButtonClickListener();
		btnPathSelection.addMouseListener(listener);
		btnOK.addMouseListener(listener);
		btnCancel.addMouseListener(listener);

		txtName.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				setFilePath(GlobalVariables.sWorkingDir);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				setFilePath(GlobalVariables.sWorkingDir);

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub

			}

		});

		txtAge.setDocument(new PlainDocument() {
			public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
				try {
					Integer.parseInt(s);
				} catch (Exception ex) {
					System.out.println("Age must be number");
					return;
				}
				super.insertString(offset, s, attributeSet);
			}
		});
	}

	private BasicInfo collectData() {
		String name = txtName.getText().isBlank() ? Constant.UNNAME : txtName.getText();
		Gender gender;
		boolean isMale = rdbtnMale.isSelected();
		boolean isFemale = rdbtnFemale.isSelected();
		if (isMale) {
			gender = Gender.MALE;
		} else if (isFemale) {
			gender = Gender.FEMALE;
		} else {
			gender = Gender.UNKNOW;
		}

		int age = 0;

		try {
			age = Integer.parseInt(txtAge.getText());
		} catch (NumberFormatException e) {

		}
		String pn = this.txtTel.getText();
		BasicInfo info = new BasicInfo(name, age, gender, pn);
		return info;
	}

	public File getSaveFile() {
		return saveFile;
	}

	private void setFilePath(String dir) {
		String name = txtName.getText().isBlank() ? Constant.UNNAME : txtName.getText();
		String tmp = dir + name + "." + Constant.PROJ_FILE_SUFFIX;
		txtPath.setText(tmp);
		saveFile = new File(tmp);
	}

	class ButtonClickListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1)
				return;
			Object source = e.getSource();
			if (source.equals(btnPathSelection)) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setSelectedFile(saveFile);
				int result = chooser.showSaveDialog(NewProjPane.this);
				if (result == JFileChooser.CANCEL_OPTION)
					return;
				File dir = chooser.getSelectedFile();
				try {
					GlobalVariables.sWorkingDir = dir.getCanonicalPath() + File.separator;
					setFilePath(GlobalVariables.sWorkingDir);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else if (source.equals(btnOK)) {
				NewProjPane.this.setVisible(false);
				BasicInfo info = collectData();
				if (context != null) {
					File dir = saveFile.getParentFile();
					if (!dir.exists())
						dir.mkdirs();
					context.OK(info, saveFile);
				}
				dispose();
			} else if (source.equals(btnCancel)) {
				dispose();
			}
		}

	}

}
