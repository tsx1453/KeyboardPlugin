import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static com.intellij.openapi.util.io.StreamUtil.closeStream;

public class FileOperate {

    private static String ModulePkgName = "com.amber.keyboard.skin.moudle";
    private static String AppModuleName = "AppModuleName";
    private static String ThemeModuleId = "ThemeIdModule";
    private final Project project;
    private final Module module;
    private String mProjectPath;
    private String mModuleName;
    private String moduleRootPath;
    private String drawablePath;
    private String appName;
    private String pkgName;
    private List<String> messages;

    public FileOperate(Project project, Module module, String mModuleName, String drawablePath, String appName, String pkgName) {
        this.project = project;
        this.module = module;
        this.mModuleName = mModuleName;
        this.drawablePath = drawablePath;
        this.appName = appName;
        this.pkgName = pkgName;
        messages = new ArrayList<>();
        mProjectPath = project.getBasePath();
        moduleRootPath = mProjectPath + File.separator + mModuleName + File.separator + "src" + File.separator +
                "main" + File.separator + "res";
        try {
            copyModuleFile();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    changeAppName();
                    setId();
                }
            }).run();
            copyDrawableFile();
            deleteTest();
            icons();
            prview();
            changeBuildGradle();
            changeManifest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyModuleFile() {
        try {
            String templatePath = this.getClass().getClassLoader().getResource("/template").getPath();
            File moduleDirs = new File(mProjectPath + File.separator + mModuleName);
            if (!moduleDirs.exists()) {
                moduleDirs.mkdirs();
            }
            File templateDirs = new File(templatePath);
            FileUtils.copyDirectory(templateDirs, moduleDirs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyDrawableFile() {
        try {
            String targetPath = moduleRootPath + File.separator + "drawable-xxhdpi";
            File targetDir = new File(targetPath);
//            if (!targetDir.exists()) {
//                targetDir.mkdirs();
//            }
            File srcDir = new File(drawablePath);
            FileUtils.copyDirectory(srcDir, targetDir, new FileFilter() {
                @Override
                public boolean accept(File pathname) {
//                    aTodo re filter for the files
                    String fileName = pathname.getName();
                    String regstr = "[a-z]+(\\.|[0-9]|[a-z]|_)*\\.(jpg|png)";
//                    try {
//                        logging(fileName + ":" + String.valueOf(Pattern.matches(regstr, fileName)));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    return Pattern.matches(regstr, fileName);
//                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTest() {
        try {
            File test = new File(mProjectPath + File.separator + mModuleName + File.separator
                    + "test");
            if (test.exists()) {
                test.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeBuildGradle() {
        String gpath = mProjectPath + File.separator + mModuleName + File.separator + "build.gradle";
        try {
            changeContext(gpath, FileOperate.ModulePkgName, pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeContext(String path, String srcStr, String targetStr) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line;
            while ((line = reader.readLine()) != null) {
//                debug(String.valueOf(index));
                if (line.contains(srcStr)) {
                    line = line.replace(srcStr, targetStr);
//                    debug(line);
                }
                sb.append(line + "\n");
            }
            writeFile(new File(path), sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeManifest() {
        String path = mProjectPath + File.separator + mModuleName + File.separator + "src" + File.separator +
                "main" + File.separator + "AndroidManifest.xml";
        try {
            changeContext(path, FileOperate.ModulePkgName, pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeAppName() {
        String path = mProjectPath + File.separator + mModuleName + File.separator + "src" + File.separator +
                "main" + File.separator + "res" + File.separator + "values" + File.separator + "strings.xml";
        try {
            changeContext(path, FileOperate.AppModuleName, appName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeFile(File src, String text) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(src), "UTF-8"));
            bw.write(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(bw);
        }
    }

    private void setId() {
        Map<String, String> map = new HashMap<>();
        map.put("__VIEWSTATE", "/wEPDwUJMjE1NTQ0MjIxZBgBBR5fX0NvbnRyb2xzUmVxdWlyZVBvc3RCYWNrS2V5X18WBgUMY2hrVXBwZXJjYXNlBQtjaGtCcmFja2V0cwUJY2hrSHlwZW5zBQljaGtCYXNlNjQFCmNoa1JGQzc1MTUFBmNoa1VSTBFFiCwfZ+dToorVhn7l31l2W3H3");
        map.put("__VIEWSTATEGENERATOR", "247C709F");
        map.put("__EVENTVALIDATION", "/wEWCgLg2er8CwLJkuW4AwKyzJeLDALcw7KJAgKm/OiABwLfgo3zCALh/K6KCALk14fZCQLu3NjFAQL6g7v1AwauOOi5YkWtK2DAl7QRp7CcUmMJ");
        map.put("txtCount", "1");
        map.put("chkHypens", "on");
        map.put("LocalTimestampValue", "Date().getTime()");
        map.put("btnGenerate", "Generate some GUIDs!");
        try {
            Document doc = Jsoup.connect("https://www.guidgenerator.com/online-guid-generator.aspx")
                    .data(map)
                    .post();
            String path = mProjectPath + File.separator + mModuleName + File.separator + "src" + File.separator +
                    "main" + File.separator + "res" + File.separator + "values" + File.separator + "strings.xml";
            changeContext(path, FileOperate.ThemeModuleId, doc.getElementById("txtResults").text());
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showMessageDialog("get guid error!", "error", Messages.getErrorIcon());
        }
    }

    private void icons() {
        iconsHelper(moduleRootPath + File.separator + "drawable-xxhdpi");
        String root = "";
        if (drawablePath.contains("output")) {
            root = drawablePath.replace("output", "operation") + File.separator;
        } else if (drawablePath.contains("develop")) {
            root = drawablePath.replace("develop", "operation") + File.separator;
        }
        iconsHelper(root);
    }

    private void iconsHelper(String xxPath) {
//        String xxPath = moduleRootPath + File.separator + "drawable-xxhdpi";
//        debug(xxPath);
        try {
            String[] icons = {"icon.png", "icon_72.png", "icon_192.png"};
            for (int i = 0; i < 3; i++) {
                File file = new File(xxPath + File.separator + icons[i]);
                if (!file.exists()) {
                    continue;
                }
                switch (i) {
                    case 0:
//                        file.delete();
                        break;
                    case 1:
                        File targetFile = new File(moduleRootPath + File.separator + "drawable-xxhdpi" + File.separator + "theme_icon.png");
                        if (targetFile.exists()) {
//                        file.delete();
                            break;
                        }
                        FileUtils.copyFile(file, targetFile);
                        break;
                    case 2:
                        File targetFile1 = new File(moduleRootPath + File.separator + "drawable-xxxhdpi" + File.separator + "theme_icon.png");
                        if (targetFile1.exists()) {
//                        file.delete();
                            break;
                        }
                        FileUtils.copyFile(file, targetFile1);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prview() {
        try {
            String root = "";
            if (drawablePath.contains("output")) {
                root = drawablePath.replace("output", "operation") + File.separator;
            } else if (drawablePath.contains("develop")) {
                root = drawablePath.replace("develop", "operation") + File.separator;
            }
//            debug(root);
            boolean flag = true;
            File prview1 = new File(root + "preview_665x1182.jpg");
            if (!prview1.exists()) {
                prview1 = new File(root + "preview_665x1182.png");
                if (!prview1.exists()) {
                    messages.add("preview_665x1182");
                    flag = false;
                }
            }
            if (flag) {
                FileUtils.copyFile(prview1, new File(moduleRootPath + File.separator + "drawable-xxhdpi" + File.separator + "theme_preview_img.jpg"));
            }
            flag = true;
            File preview2 = new File(root + "store_Promotion_400x600.jpg");
            if (!preview2.exists()) {
                preview2 = new File(root + "store_Promotion_400x600.png");
                if (!preview2.exists()) {
                    messages.add("store_Promotion_400x600");
                    flag = false;
                }
            }
            if (flag) {
                FileUtils.copyFile(preview2, new File(moduleRootPath + File.separator + "drawable-xxhdpi" + File.separator + "keyboard_theme_preview.jpg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void debug(String str) {
        Messages.showMessageDialog(str, "debug", Messages.getErrorIcon());
    }

    private void logging(String value) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("log.txt")));
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line + "\n");
        }
        Calendar calendar = Calendar.getInstance();
        stringBuffer.append(calendar.getTime().toString() + ":" + value + "\n");
        reader.close();
        writeFile(new File("log.txt"), stringBuffer.toString());
    }
}
