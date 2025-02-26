package tk.kqstone.dedd.setting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import java.io.File;

public class Settings extends JDialog {
    // 原有成员变量
    private JRadioButton defaultServerRadioButton;
    private JRadioButton customServerRadioButton;
    private JTextField customServerTextField;
    private JButton saveButton;
    private JButton cancelButton;
    private Preferences userPreferences;
    
    // 新增语言相关组件
    private JComboBox<String> languageComboBox;

    public Settings() {
        File configFile = new File("config");
        if (!configFile.exists() || !configFile.isDirectory()) {
            configFile.mkdir();
        }

        userPreferences = Preferences.userRoot().node("/config/settings");

        setTitle("Settings"); // 标题国际化
        setLayout(new BorderLayout());

        // 创建语言设置面板
        JPanel languagePanel = createLanguagePanel();
        
        // 服务器设置标签
        JLabel serverSettingsLabel = new JLabel("Server Settings");
        serverSettingsLabel.setHorizontalAlignment(JLabel.CENTER);

        // 北部面板（包含语言设置和服务器标签）
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(languagePanel);
        northPanel.add(serverSettingsLabel);
        add(northPanel, BorderLayout.NORTH);

        // 原有服务器设置组件
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
        JPanel radioPanel = new JPanel(new FlowLayout());
        radioPanel.add(defaultServerRadioButton);
        radioPanel.add(customServerRadioButton);
        centerPanel.add(radioPanel);
        centerPanel.add(customServerTextField);
        add(centerPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 事件处理
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
        setResizable(false);
        setAlwaysOnTop(true);
        pack();
        setLocationRelativeTo(null);
    }
    
    public static String getCurrentLanguage() {
        return Preferences.userRoot().node("/config/settings").get("language", "zh"); // 默认中文
    }

    private JPanel createLanguagePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("UI Language:");
        languageComboBox = new JComboBox<>(new String[]{"中文", "English"});
        
        panel.add(label);
        panel.add(languageComboBox);
        return panel;
    }

    private void saveSettings() {
        // 保存语言设置
        String lang = languageComboBox.getSelectedItem().equals("中文") ? "zh" : "en";
        userPreferences.put("language", lang);
        
        // 原有服务器设置保存逻辑
        if (customServerRadioButton.isSelected()) {
            userPreferences.put("serverType", "custom");
            userPreferences.put("customServerAddress", customServerTextField.getText());
        } else {
            userPreferences.put("serverType", "default");
        }
    }

    private void loadSettings() {
        // 加载语言设置
        String savedLang = userPreferences.get("language", "zh");
        languageComboBox.setSelectedItem(savedLang.equals("zh") ? "中文" : "English");
        
        // 原有服务器设置加载逻辑
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
        this.dispose();
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