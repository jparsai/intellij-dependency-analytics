package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiFile;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class SaReportAction extends AnAction {
    private static final Logger log = Logger.getInstance(SaReportAction.class);

    /**
     * <p>Intellij Plugin Action implementation for triggering SA.</p>
     *
     * <p>Analysis will be performed on the file for which Action is triggered and Report will be shown in editor workspace.</p>
     *
     * @param event An instance of AnActionEvent.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Logic to be executed when SA action is triggered by user
        SaUtils saUtils = new SaUtils();
        try {
            // Get FileEditorManager instance
            FileEditorManager instance = FileEditorManager.getInstance(event.getProject());

            // Close custom editor if already opened in previous run
            saUtils.closeCustomEditor(instance);

            // Get SA report for given manifest file.
            JSONObject saReportJson = saUtils.getReport(event.getData(PlatformDataKeys.VIRTUAL_FILE).getPath());

            // Open custom editor window which will load SA Report in browser attached to it.
            saUtils.openCustomEditor(instance, saReportJson.getString("report_link"));
        } catch (Exception e) {
            log.error(e);
        }
    }


    /**
     * <p>Updates the state of the action, Action is show if this method returns true.</p>
     *
     * @param event An instance of AnActionEvent.
     */
    @Override
    public void update(AnActionEvent event) {
        // Set supported file extensions
        List<String> extensions = Arrays.asList("xml", "json", "txt");
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        // Check if file where context menu is opened is type of supported extension.
        // If yes then show the action for SA in menu
        // Also check Intellij version if it is lower than 2021.X then don't show SA Action.
        if (psiFile != null) {
            int intellijMajorVersion = Integer.parseInt(ApplicationInfo.getInstance().getMajorVersion());
            event.getPresentation().setEnabledAndVisible(extensions
                    .contains(psiFile.getFileType().getDefaultExtension()) && intellijMajorVersion>=2021);
        }
    }
}