package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


public class StackAnalysisAction extends AnAction {
    private static final Logger log = Logger.getInstance(StackAnalysisAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        log.warn("Inside PopupDialogAction:actionPerformed ");
        log.warn("Manifest File Location= "+ event.getData(PlatformDataKeys.VIRTUAL_FILE).getPath());
        log.warn("Action Location= "+ event.getPlace());
    }

    @Override
    public void update(AnActionEvent event) {
        log.warn("Inside PopupDialogAction:update");
        List<String> extensions = Arrays.asList("xml", "json", "txt");
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        event.getPresentation().setEnabledAndVisible(extensions
                .contains(psiFile.getFileType().getDefaultExtension()));
    }
}