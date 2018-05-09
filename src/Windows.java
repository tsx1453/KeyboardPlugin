import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;

public class Windows {

    private Project mProject;
    private Module mModule;
    private JFrame frame;
    private JTextField moduleNameInput;
    private JTextField packageName;
    private JTextField appNameInput;
    private JFileChooser mJFileChooser;
    private JTextField drawablePath;

    public Windows(Project mProject, Module mModule) {
        this.mProject = mProject;
        this.mModule = mModule;
        initView();
        show();
    }

    private void initView() {
        frame = new JFrame("New KeyBoard Skin");
        frame.setSize(400, 350);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        JPanel jPanel = new JPanel();
        jPanel.setLayout(null);
        jPanel.setBounds(0, 0, 400, 350);
        JLabel label1 = new JLabel("module name:");
        label1.setBounds(5, 0, 100, 50);
        label1.setVerticalTextPosition(JLabel.CENTER);
        jPanel.add(label1);
        moduleNameInput = new JTextField();
//        moduleNameInput.setPreferredSize(new Dimension(200,50));
        moduleNameInput.setBounds(105, 10, 200, 50);
        jPanel.add(moduleNameInput);
        JLabel label2 = new JLabel("package name:");
        label2.setVerticalTextPosition(JLabel.CENTER);
        label2.setBounds(5, 60, 100, 50);
        jPanel.add(label2);
        packageName = new JTextField();
//        packageName.setPreferredSize(new Dimension(200,50));
        packageName.setBounds(105, 70, 200, 50);
        jPanel.add(packageName);
        JLabel label3 = new JLabel("app name:");
        label3.setVerticalTextPosition(JLabel.CENTER);
        label3.setBounds(5, 130, 100, 50);
        jPanel.add(label3);
        appNameInput = new JTextField();
        appNameInput.setBounds(105, 130, 200, 50);
        jPanel.add(appNameInput);
        JLabel label4 = new JLabel("drawable path:");
        label4.setBounds(5, 200, 100, 50);
        jPanel.add(label4);
        drawablePath = new JTextField();
        drawablePath.setBounds(105, 190, 200, 50);
        JButton fileBtn = new JButton("...");
        fileBtn.setBounds(310, 190, 50, 50);
        fileBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDialog();
            }
        });
        jPanel.add(fileBtn);
        jPanel.add(drawablePath);
        JButton okBtn = new JButton("OK");
        okBtn.setBounds(5, 250, 370, 50);
        okBtn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pkgName = packageName.getText();
                String appName = appNameInput.getText();
                String modName = moduleNameInput.getText();
                String draPath = drawablePath.getText();
                if (pkgName.length()==0||appName.length()==0||modName.length()==0){
                    Messages.showMessageDialog("check your input!","warning",Messages.getErrorIcon());
                    return;
                }
                new FileOperate(mProject,mModule,modName,draPath,appName,pkgName);
                hide();
            }
        });
        jPanel.add(okBtn);
        frame.add(jPanel);
    }

    private void show() {
        if (frame != null) {
            frame.setVisible(true);
        }
    }

    private void hide() {
        if (frame != null) {
            frame.setVisible(false);
        }
    }

    private void showDialog() {
        if (mJFileChooser == null) {
            mJFileChooser = new JFileChooser();
        }
        mJFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = mJFileChooser.showDialog(new JLabel(), "选择");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File resultFile = null;
            File selectedFile = mJFileChooser.getSelectedFile();
            if (selectedFile.isDirectory() && selectedFile.exists()) {
                resultFile = selectedFile;
            } else {
                resultFile = mJFileChooser.getCurrentDirectory();
            }
            if (new File(resultFile.getAbsolutePath()).listFiles().length<1){
                Messages.showMessageDialog("path is unuseless!","Error!",Messages.getErrorIcon());
                return;
            }
            drawablePath.setText(resultFile.getAbsolutePath());
        }
    }
}
