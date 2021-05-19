package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import icons.SdkIcons;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class StackAnalysisAction extends AnAction {
    private static final Logger log = Logger.getInstance(StackAnalysisAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        JSONObject saReportJson = new JSONObject(getSaReport(
                event.getData(PlatformDataKeys.VIRTUAL_FILE).getPath()));
        openToolWindow(event.getProject(), saReportJson.getString("report_link"));
    }

    @Override
    public void update(AnActionEvent event) {
        // Set supported file extensions
        List<String> extensions = Arrays.asList("xml", "json", "txt");
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        // Check if file where context menu is opened is type of supported extension.
        // If yes then show the action for SA in menu
        if (psiFile != null) {
            event.getPresentation().setEnabledAndVisible(extensions
                    .contains(psiFile.getFileType().getDefaultExtension()));
        }
    }

    private String getSaReport(String filePath){
        // Logic to run SA
        // Set the path of directory having CLI binary into command
        String cmd = CliConfig.CLI_ANALYSE.replace("filepath", filePath);
        cmd = new CliConfig().getCliBinaryPath() + File.separator + cmd;
        CommandExecutor cmdExecutor = new CommandExecutor();
        return cmdExecutor.execute(cmd);
    }

    private void openToolWindow(Project project, String url) {
        // Logic to open tool window and show the report
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Dependency Analytics");

        if (toolWindow == null) {
            RegisterToolWindowTask registerToolWindowTask = new RegisterToolWindowTask("Dependency Analytics",
                    ToolWindowAnchor.BOTTOM, null, false, true, false,
                    true, null, SdkIcons.Sdk_default_icon, null);
            toolWindow = toolWindowManager.registerToolWindow(registerToolWindowTask);
            toolWindow.setToHideOnEmptyContent(true);
        }

        ContentManager contentManager = toolWindow.getContentManager();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        WebView webView = new WebView(url);
        Content content = contentFactory.createContent(webView.getContent(),
                "Dependency Analytics Report", false);
        content.setCloseable(true);
        contentManager.removeAllContents(true);
        contentManager.addContent(content);
        contentManager.setSelectedContent(content, true);
        toolWindow.show();
    }
}