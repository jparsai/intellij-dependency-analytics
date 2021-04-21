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
        // Logic to be executed when SA action is triggered by user
        // Get the SA report using CLI
        JSONObject saReportJson = new JSONObject(getSaReport(
                event.getData(PlatformDataKeys.VIRTUAL_FILE).getPath()));
        // Show report
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
        // Execute CLI command for analysis
        cmd = new CliConfig().getCliBinaryPath() + File.separator + cmd;
        CommandExecutor cmdExecutor = new CommandExecutor();
        return cmdExecutor.execute(cmd);
    }

    private void openToolWindow(Project project, String url) {
        // Logic to open tool window and show the report
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        // Get the tool window ID if already opened by previous run
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Dependency Analytics");

        // If running for first time register a tool window in bottom left
        if (toolWindow == null) {
            RegisterToolWindowTask registerToolWindowTask = new RegisterToolWindowTask("Dependency Analytics",
                    ToolWindowAnchor.BOTTOM, null, false, true, false,
                    true, null, SdkIcons.Sdk_default_icon, null);
            toolWindow = toolWindowManager.registerToolWindow(registerToolWindowTask);
            toolWindow.setToHideOnEmptyContent(true);
        }

        ContentManager contentManager = toolWindow.getContentManager();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        // Open given url in JCEF browser
        WebView webView = new WebView(url);

        // Add the browser content into tool window
        Content content = contentFactory.createContent(webView.getContent(),
                "Dependency Analytics Report", false);
        content.setCloseable(true);

        // Close existing tabs from tool window
        contentManager.removeAllContents(true);

        // Add new tab in tool window
        contentManager.addContent(content);
        contentManager.setSelectedContent(content, true);
        toolWindow.show();
    }
}