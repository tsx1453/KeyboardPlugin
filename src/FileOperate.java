import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.regex.Pattern;

import static com.intellij.openapi.util.io.StreamUtil.closeStream;

public class FileOperate {

    private static String ModulePkgName = "com.amber.keyboard.skin.moudle";
    private final Project project;
    private final Module module;
    private String mProjectPath;
    private String mModuleName;
    private String moduleRootPath;
    private String drawablePath;
    private String appName;
    private String pkgName;

    public FileOperate(Project project, Module module, String mModuleName, String drawablePath, String appName, String pkgName) {
        this.project = project;
        this.module = module;
        this.mModuleName = mModuleName;
        this.drawablePath = drawablePath;
        this.appName = appName;
        this.pkgName = pkgName;
        mProjectPath = project.getBasePath();
        moduleRootPath = mProjectPath + File.separator + mModuleName + File.separator + "src" + File.separator +
                "main" + File.separator + "res";
        try {
            copyModuleFile();
            copyDrawableFile();
            deleteTest();
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
                    String regstr = "([a-z]|_)+([0-9]|[a-z]|_)*\\.jpg|png";
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
            changePkgName(gpath, pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changePkgName(String path, String targetStr) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            String line;
            while ((line = reader.readLine()) != null) {
//                debug(String.valueOf(index));
                if (line.contains(FileOperate.ModulePkgName)) {
                    line = line.replace(FileOperate.ModulePkgName, targetStr);
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
            changePkgName(path, pkgName);
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

    private void debug(String str) {
        Messages.showMessageDialog(str, "debug", Messages.getErrorIcon());
    }
// Todo builed.gradle  AndroidManifest.xml
}
