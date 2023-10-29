package tk.kqstone.dedd.setting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import java.io.File;

public class Settings extends JDialog {
    private JRadioButton defaultServerRadioButton;
    private JRadioButton customServerRadioButton;
    private JTextField customServerTextField;
    private JButton saveButton;
    private JButton cancelButton;
    private Preferences userPreferences;

    public Settings() {
        File configFile = new File("config");
        if (!configFile.exists() || !configFile.isDirectory()) {
            configFile.mkdir();
        }

        userPreferences = Preferences.userRoot().node("/config/settings");

        setTitle("设置");
        setLayout(new BorderLayout());

        JLabel serverSettingsLabel = new JLabel("服务器设置");
        serverSettingsLabel.setHorizontalAlignment(JLabel.CENTER);
        add(serverSettingsLabel, BorderLayout.NORTH);

        defaultServerRadioButton = new JRadioButton("Default");
        customServerRadioButton = new JRadioButton("Custom");
        customServerTextField = new JTextField(20);

        ButtonGroup serverGroup = new ButtonGroup();
        serverGroup.add(defaultServerRadioButton);
        serverGroup.add(customServerRadioButton);

        defaultServerRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customServerTextField.setEnabled(false);
            }
        });

        customServerRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customServerTextField.setEnabled(true);
            }
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout());
        radioPanel.add(defaultServerRadioButton);
        radioPanel.add(customServerRadioButton);

        centerPanel.add(radioPanel);
        centerPanel.add(customServerTextField);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        saveButton = new JButton("保存");
        cancelButton = new JButton("取消");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
                closeSettingsPanel();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSettings();
                closeSettingsPanel();
            }
        });

        loadSettings();

        setResizable(false); // 设置窗口不可调整大小
        setAlwaysOnTop(true); // 窗口位于最顶端

        pack();
        setLocationRelativeTo(null);
    }

    private void saveSettings() {
        if (customServerRadioButton.isSelected()) {
            userPreferences.put("serverType", "custom");
            userPreferences.put("customServerAddress", customServerTextField.getText());
        } else {
            userPreferences.put("serverType", "default");
        }
    }

    private void loadSettings() {
        String savedServerType = userPreferences.get("serverType", "default");
        if (savedServerType.equals("default")) {
            defaultServerRadioButton.setSelected(true);
            customServerTextField.setEnabled(false);
        } else {
            customServerRadioButton.setSelected(true);
            customServerTextField.setEnabled(true);
            customServerTextField.setText(userPreferences.get("customServerAddress", ""));
        }
    }

    private void closeSettingsPanel() {
        this.dispose(); // 关闭设置面板
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Settings settingsPanel = new Settings();
                settingsPanel.setVisible(true);
            }
        });
    }
}
