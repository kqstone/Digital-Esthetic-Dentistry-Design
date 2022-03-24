/**
 * 
 */
package tk.kqstone.dedd;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import tk.kqstone.dedd.build.BuildInfo;

import javax.swing.BoxLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;

/**
 * @author kqstone
 *
 */
public class InformBar extends JPanel {
	private Container ctnPatientInfo = new Container();;
	private JLabel version;
	private JLabel name;
	private JLabel age;
	private JLabel gender;

	public InformBar() {
		super();
		this.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		this.add(ctnPatientInfo);

		name = new JLabel();
		age = new JLabel();
		gender = new JLabel();
		ctnPatientInfo.setLayout(new BoxLayout(ctnPatientInfo, BoxLayout.X_AXIS));
		ctnPatientInfo.add(name);

		ctnPatientInfo.add(Box.createHorizontalStrut(25));
		ctnPatientInfo.add(gender);

		ctnPatientInfo.add(Box.createHorizontalStrut(25));
		ctnPatientInfo.add(age);

		add(Box.createHorizontalGlue());

		version = new JLabel(Constant.VERSION_NAME + ": " + BuildInfo.getVersion());
		this.add(version);
	}

	public void setBasicInfo(BasicInfo info) {

		name.setText(Constant.NAME + ": " + info.name);
		gender.setText(Constant.GENDER + ": " + info.gender.value);
		String strAge = info.age == 0 ? "未知" : (info.age + Constant.AGE_SUFFIX);
		age.setText(Constant.AGE + ": " + strAge);
	}

	public void clear() {
		name.setText(null);
		gender.setText(null);
		age.setText(null);
	}

}
