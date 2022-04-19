package tk.kqstone.dedd;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import tk.kqstone.dedd.ProjDataHelper.DataCheckException;
import tk.kqstone.dedd.ui.IMethod;
import tk.kqstone.dedd.ui.SuspendTip;
import tk.kqstone.dedd.ui.TabEvent;
import tk.kqstone.dedd.ui.TabPanel;
import tk.kqstone.dedd.ui.TabStateChangeListener;
import tk.kqstone.dedd.ui.TitleBar;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.Toolkit;

/**
 * 
 * MainUI for DEDD
 * 
 * @author kqstone
 * @email kqstone@163.com
 *
 */
public class MainUI extends JFrame {
	private final static String ICON = "/img/icon.png";

	private JMenuBar menuBar;
	private Container root; // Root Container
	private TabPanel tabPanel;
	private WorkSpace workspace; // WorkSpace Container
	private ResultPanel resultPanel;
	private InformBar infoBar;

	private File projFile; // File for project saving

	private JMenuItem newMenuItem;
	private JMenuItem exitMenuItem;
	private JMenuItem aboutMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem readFromMenuItem;
	private JMenuItem closeMenuItem;
	private JMenuItem updateMenuItem;
	private JMenuItem redoMenuItem;
	private JMenuItem undoMenuItem;

	private IController controller;

	private BasicInfo basicInfo;

	MainUI() {
		super(Constant.PROG_NAME);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainUI.class.getResource(ICON)));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setUndecorated(true);
		this.setResizable(false);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());
		this.setBounds(0, 0, screen.width, screen.height - insets.bottom);

		Container bg = this.getContentPane();
		bg.setLayout(new BorderLayout());

		TitleBar titleBar = new TitleBar(this);
		bg.add(titleBar, BorderLayout.NORTH);

		root = new Container();
		bg.add(root, BorderLayout.CENTER);
		root.setLayout(new BorderLayout(0, 4));
		root.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (projFile != null)
					return;
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					readProj();
				}
			}
		});

		initMenuBar();
		root.add(menuBar, BorderLayout.NORTH);

		infoBar = new InformBar();
		infoBar.setPreferredSize(new Dimension(0, 25));
		root.add(infoBar, BorderLayout.SOUTH);

		this.setVisible(true);

		tabPanel = new TabPanel();
		root.add(tabPanel, BorderLayout.CENTER);
		root.add(Box.createHorizontalStrut(4), BorderLayout.WEST);
		root.add(Box.createHorizontalStrut(4), BorderLayout.EAST);
		tabPanel.addTab(Constant.LOADPICT).addTab(Constant.EDITPICT).addTab(Constant.MARKLIP).addTab(Constant.MARKTEETH)
				.addTab(Constant.ADJUSTTEETH).addTab(Constant.EXPORTDESIGN);
		MyListener listener = new MyListener();
		tabPanel.addTabStateChangeListener(listener);
		tabPanel.setVisible(false);
		
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {

			@Override
			public void eventDispatched(AWTEvent event) {
				
				if (((KeyEvent)event).getID() == KeyEvent.KEY_PRESSED) {
					if((((KeyEvent)event).getModifiersEx() & InputEvent.CTRL_DOWN_MASK) !=0) {
						System.out.println("MainUI: " + "Ctrl pressed!!!!!!!!!!!!!");
						MainUI.this.requestFocus();
						switch(((KeyEvent)event).getKeyCode()) {
						case KeyEvent.VK_Z:
							workspace.undo();
							break;
						case KeyEvent.VK_Y:
							workspace.redo();
							break;
							default:
								break;
						}
					}
					
				}
				
			}}, AWTEvent.KEY_EVENT_MASK);

	}

	private void initMenuBar() {
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu(Constant.MENU_FILE);
		JMenu editMenu = new JMenu(Constant.MENU_EDIT);
		JMenu aboutMenu = new JMenu(Constant.MENU_ABOUT);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(aboutMenu);		
		newMenuItem = new JMenuItem(Constant.NEW);
		saveMenuItem = new JMenuItem(Constant.SAVE);
		saveMenuItem.setEnabled(false);
		readFromMenuItem = new JMenuItem(Constant.READFROM);
		exitMenuItem = new JMenuItem(Constant.EXIT);
		aboutMenuItem = new JMenuItem(Constant.ABOUT);
		closeMenuItem = new JMenuItem(Constant.CLOSE);
		closeMenuItem.setEnabled(false);
		updateMenuItem = new JMenuItem(Constant.CHECK_UPDATE);
		redoMenuItem = new JMenuItem(Constant.REDO);
		redoMenuItem.setEnabled(false);
		undoMenuItem = new JMenuItem(Constant.UNDO);
		undoMenuItem.setEnabled(false);
		fileMenu.add(newMenuItem);
		fileMenu.add(readFromMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(closeMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);
		aboutMenu.add(updateMenuItem);
		aboutMenu.add(aboutMenuItem);
		editMenu.add(undoMenuItem);
		editMenu.add(redoMenuItem);

		MenuListener l = new MenuListener();
		newMenuItem.addActionListener(l);
		readFromMenuItem.addActionListener(l);
		exitMenuItem.addActionListener(l);
		aboutMenuItem.addActionListener(l);
		saveMenuItem.addActionListener(l);
		closeMenuItem.addActionListener(l);
		updateMenuItem.addActionListener(l);
		undoMenuItem.addActionListener(l);
		redoMenuItem.addActionListener(l);

	}

	public File getProjFile() {
		return projFile;
	}

	private void setProjFile(File projFile) {
		this.projFile = projFile;
		boolean isNull = projFile == null ? true : false;
		saveMenuItem.setEnabled(!isNull);
		closeMenuItem.setEnabled(!isNull);
	}

	public BasicInfo getBasicInfo() {
		return basicInfo;
	}

	public void addController(IController controller) {
		this.controller = controller;
	}

	public WorkSpace getWorkspace() {
		return workspace;
	}

	public void setBasicInfo(BasicInfo info) {
		this.basicInfo = info;
		infoBar.setBasicInfo(info);
	}

	private void newProject() {
		if (projFile != null) {
			int r = JOptionPane.showConfirmDialog(this, Constant.CONFIRM_SAVE, Constant.SAVE,
					JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.CLOSED_OPTION) {
				return;
			} else if (r != 0) {
				closeProj();
				newProjPane();
				return;
			}
		}
		this.saveProjAndInvokeLater(new Runnable() {

			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						closeProj();
						newProjPane();
					}
				});
			}
		});

	}

	private void newProjPane() {
		NewProjPane npp = new NewProjPane(this, new IContext() {

			@Override
			public void OK(final BasicInfo info, final File file) {
				SuspendTip spt = new SuspendTip(MainUI.this, Constant.INITIALIZE_UI);
				spt.addMethod(new IMethod() {

					@Override
					public void run() {
						basicInfo = info;
						setProjFile(file);
						newProj();
						infoBar.setBasicInfo(info);
					}
				});
				spt.run();
			}
		});
		npp.setLocationRelativeTo(this);
		npp.setVisible(true);
	}

	@Override
	public void dispose() {
		if (projFile == null) {
			System.exit(0);
		} else {
			int r = JOptionPane.showConfirmDialog(this, Constant.CONFIRM_EXIT, Constant.EXIT,
					JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.CLOSED_OPTION) {
				return;
			} else if (r == 1) {
				System.exit(0);
			}
			this.saveProjAndInvokeLater(new Runnable() {

				@Override
				public void run() {
					System.exit(0);

				}
			});
		}

	}

	// Tab Focus changed Listener
	class MyListener implements TabStateChangeListener {

		@Override
		public void onFocused(TabEvent e) {
			String cmd = e.getTabLabel();
			switch (cmd) {
			case Constant.LOADPICT:
				if (!workspace.isVisible())
					workspace.setVisible(true);
				workspace.load();
				if (resultPanel.isVisible())
					resultPanel.setVisible(false);
				break;
			case Constant.EDITPICT:
				if (!workspace.isVisible())
					workspace.setVisible(true);

				workspace.edit();
				if (resultPanel.isVisible())
					resultPanel.setVisible(false);
				break;
			case Constant.MARKLIP:
				if (!workspace.isVisible())
					workspace.setVisible(true);

				workspace.marklip();
				;
				if (resultPanel.isVisible())
					resultPanel.setVisible(false);
				break;
			case Constant.MARKTEETH:
				if (!workspace.isVisible()) {
					workspace.setVisible(true);
				}

				workspace.mark();
				if (resultPanel.isVisible())
					resultPanel.setVisible(false);
				redoMenuItem.setEnabled(true);
				undoMenuItem.setEnabled(true);
				break;
			case Constant.ADJUSTTEETH:
				if (!workspace.isVisible()) {
					workspace.setVisible(true);
				}
				workspace.adjust();
				if (resultPanel.isVisible())
					resultPanel.setVisible(false);
				break;
			case Constant.EXPORTDESIGN:
				resultPanel.setName(basicInfo.name);
				resultPanel.setAge(basicInfo.age);
				resultPanel.setGender(basicInfo.gender);
				resultPanel.setPhone(basicInfo.phone);

				workspace.setVisible(true);
				BufferedImage imageDesign = workspace.getBasePanel().genImage(TeethAdjustPanel.MODE_CONTOUR);
				BufferedImage baseImageBefore = workspace.getBasePanel().genPreImage();
				BufferedImage baseImageAfter = workspace.getBasePanel().genImage(TeethAdjustPanel.MODE_REAL);
				BufferedImage frontImageBefore = workspace.getFrontPanel().genPreImage();
				BufferedImage frontImageAfter = workspace.getFrontPanel().genImage(TeethAdjustPanel.MODE_REAL);
				resultPanel.setDesignImage(imageDesign);
				resultPanel.setAfterBaseImage(baseImageAfter);
				resultPanel.setBeforeBaseImage(baseImageBefore);
				resultPanel.setAfterFrontImage(frontImageAfter);
				resultPanel.setBeforeFrontImage(frontImageBefore);

				List<SectScale> sectScales = workspace.getBasePanel().getAdjustPanel().getSectScales();
				resultPanel.initMarkPanel(sectScales);

				workspace.setVisible(false);
				resultPanel.setVisible(true);
				break;
			}

		}

		@Override
		public void unFocused(TabEvent e) {
			String cmd = e.getTabLabel();
			switch (cmd) {
			case Constant.MARKLIP:
				workspace.addLipMaskToAdjustPanel();
				break;
			case Constant.MARKTEETH:
				redoMenuItem.setEnabled(false);
				undoMenuItem.setEnabled(false);
				break;
			}

		}

	}

	class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if (source.equals(newMenuItem)) {
				newProject();
			} else if (source.equals(exitMenuItem)) {
				dispose();
			} else if (source.equals(aboutMenuItem)) {
				new About(root);
			} else if (source.equals(saveMenuItem)) {
				saveProj();
			} else if (source.equals(readFromMenuItem)) {
				readProj();
			} else if (source.equals(closeMenuItem)) {
				if (projFile != null) {
					int r = JOptionPane.showConfirmDialog(MainUI.this, Constant.CONFIRM_CLOSE, Constant.CLOSE,
							JOptionPane.YES_NO_OPTION);
					if (r == 0) {
						closeProj();
					}
				}
			} else if (source.equals(updateMenuItem)) {
				checkUpdate();
			} else if (source.equals(redoMenuItem)) {
				workspace.redo();
			} else if (source.equals(undoMenuItem)) {
				workspace.undo();
			}

		}

	}

	private void readProj() {
		if (projFile != null) {
			int r = JOptionPane.showConfirmDialog(this, Constant.CONFIRM_SAVE, Constant.SAVE,
					JOptionPane.YES_NO_OPTION);
			if (r == JOptionPane.CLOSED_OPTION) {
				return;
			} else if (r != 0) {
				closeProj();
				readProjPane();
				return;
			}
		}
		this.saveProjAndInvokeLater(new Runnable() {

			@Override
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						closeProj();
						readProjPane();
					}
				});
			}
		});
	}

	private void checkUpdate() {
		UpdatePane updatePane = new UpdatePane(this);
		updatePane.setVisible(true);
		updatePane.checkUpdate();
	}

	private void readProjPane() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setSelectedFile(new File(GlobalVariables.sWorkingDir + " "));
		chooser.setFileFilter(
				new FileNameExtensionFilter(Constant.PROG_NAME + "工程文件(.depx)", Constant.PROJ_FILE_SUFFIX));
		int result = chooser.showOpenDialog(root);
		if (result == JFileChooser.APPROVE_OPTION) {
			setProjFile(chooser.getSelectedFile());
			GlobalVariables.sWorkingDir = projFile.getParent() + File.separator;
			System.out.println("WorkingDir:" + GlobalVariables.sWorkingDir);

			if (controller != null) {
				SuspendTip stip = new SuspendTip(this, Constant.LOADING);
				stip.addMethod(new IMethod() {

					@Override
					public void run() {
						ProjData data;
						try {
							data = controller.readData();
							newProj();
							setData(data);
							workspace.addLipMaskToAdjustPanel();
						} catch (IOException | ParserConfigurationException | SAXException | DataCheckException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				stip.run();
			}

		}
	}

	private void setData(ProjData data) {
		BasicInfo info = data.getBasicInfo();
		this.setBasicInfo(info);

		WorkSpace workspace = this.getWorkspace();
		BufferedImage baseImage = data.getBaseImage();
		BufferedImage frontImage = data.getFrontImage();
		if (baseImage != null) {
			workspace.setImage(WorkPanel.BASE_VIEW, baseImage);
			Rectangle baseContentRect = workspace.getContentRect(WorkPanel.BASE_VIEW);
			List<Tooth> baseMarkedTeeth = Controller.convert2Teeth(data.getBaseMarkedTeeth(), baseContentRect);
			List<Tooth> baseAdjustedTeeth = Controller.convert2Teeth(data.getBaseAdjustedTeeth(), baseContentRect);
			int baseAdjustedTeethBright = data.getBaseAdjustedTeethBright();
			int baseAdjustedTeethYellow = data.getBaseAdjustedTeethYellow();
			workspace.setMarkedTeeth(WorkPanel.BASE_VIEW, baseMarkedTeeth);
			workspace.setAdjustedTeethBrightAndYellow(WorkPanel.BASE_VIEW, baseAdjustedTeethBright,
					baseAdjustedTeethYellow);
			workspace.setAdjustedTeeth(WorkPanel.BASE_VIEW, baseAdjustedTeeth);
		}

		if (frontImage != null) {
			workspace.setImage(WorkPanel.FRONT_VIEW, frontImage);
			Rectangle frontContentRect = workspace.getContentRect(WorkPanel.FRONT_VIEW);
			List<Point> lipPoints = Controller.convert2P(data.getLipPoints(), frontContentRect);
			workspace.setLipPoints(WorkPanel.FRONT_VIEW, lipPoints);
			List<Tooth> frontMarkedTeeth = Controller.convert2Teeth(data.getFrontMarkedTeeth(), frontContentRect);
			List<Tooth> frontAdjustedTeeth = Controller.convert2Teeth(data.getFrontAdjustedTeeth(), frontContentRect);
			int frontAdjustedTeethBright = data.getFrontAdjustedTeethBright();
			int frontAdjustedTeethYellow = data.getFrontAdjustedTeethYellow();
			workspace.setMarkedTeeth(WorkPanel.FRONT_VIEW, frontMarkedTeeth);
			workspace.setAdjustedTeethBrightAndYellow(WorkPanel.FRONT_VIEW, frontAdjustedTeethBright,
					frontAdjustedTeethYellow);
			workspace.setAdjustedTeeth(WorkPanel.FRONT_VIEW, frontAdjustedTeeth);
		}
	}

	public void newProj() {

		tabPanel.setVisible(true);
		workspace = new WorkSpace();
		resultPanel = new ResultPanel();
		tabPanel.addContent(workspace);
		tabPanel.addContent(resultPanel);
		workspace.init();
		workspace.load();
		resultPanel.init();
		tabPanel.revalidate();
		tabPanel.repaint();
		workspace.setVisible(true);
		resultPanel.setVisible(false);

		tabPanel.setFocus(0);
	}

	public void saveProj() {
		if (controller != null) {
			SuspendTip stip = new SuspendTip(this, Constant.SAVING_DATA);
			stip.addMethod(new IMethod() {

				@Override
				public void run() {
					try {
						controller.saveData();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			stip.run();

		}
	}

	public void saveProjAndInvokeLater(final Runnable runnable) {
		if (projFile != null && controller != null) {

			final SuspendTip stip = new SuspendTip(this, Constant.SAVING_DATA);
			stip.addMethod(new IMethod() {

				@Override
				public void run() {
					try {
						controller.saveData();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			stip.run();
			final Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {

					while (true) {
						if (stip.isFinished())
							break;
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					runnable.run();
					;
				}
			});
			thread.start();
		} else {
			runnable.run();
		}
	}

	public void delProj() {
		// TODO Auto-generated method stub

	}

	public void closeProj() {
		if (workspace != null) {
			tabPanel.removeContent(workspace);
			workspace = null;
		}
		if (resultPanel != null) {
			tabPanel.removeContent(resultPanel);
			resultPanel = null;
		}
		tabPanel.setVisible(false);
		infoBar.clear();
		this.setProjFile(null);
	}
}
