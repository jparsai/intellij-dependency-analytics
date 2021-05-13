package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class StackAnalysisAction extends AnAction {
    private static final Logger log = Logger.getInstance(StackAnalysisAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Logic to Run SA
        // Get Manifest file absolute path
        String filePath = event.getData(PlatformDataKeys.VIRTUAL_FILE).getPath();

        // Set the path of directory having CLI binary into command
        String cmd = CliConfig.CLI_ANALYSE.replace("filepath", filePath);
        cmd = new CliConfig().getCliBinaryPath() + File.separator + cmd;
        String saReportString = new CommandExecutor().execute(cmd);
        JSONObject saReportJson = new JSONObject(saReportString);
        log.warn("saReportString == "+saReportString);
        new Webview().webview(saReportJson.getString("report_link"));
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
}