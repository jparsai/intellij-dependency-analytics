package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class StackAnalysisAction extends AnAction {
    private static final Logger log = Logger.getInstance(StackAnalysisAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Logic to be executed when SA action is triggered by user
        try {
            // Get the SA report using CLI
            JSONObject saReportJson = new JSONObject(new CliProcessExecutor().performStackAnalysis(
                    event.getData(PlatformDataKeys.VIRTUAL_FILE).getPath()));
            // Show report
            WebView webView = new WebView(saReportJson.getString("report_link"));
            webView.openToolWindow(event.getProject());
        } catch (IOException | InterruptedException e) {
            log.error(e);
        }
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