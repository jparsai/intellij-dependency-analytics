package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;

import java.io.File;
import java.io.IOException;

public final class PreloadCliTool extends PreloadingActivity {
  private static final Logger log = Logger.getInstance(PreloadCliTool.class);
  private final ICookie cookies = ServiceManager.getService(Settings.class);

  @Override
  public void preload(ProgressIndicator indicator) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      return;
    }
    log.warn("cli preload is called");
    try {
      File cliBundle;
      log.warn(CLI.current.cliBundleName);
      final CliToolReleaseDownloader bundle = new CliToolReleaseDownloader(CLI.current.cliBundleName, cookies);
      bundle.download(indicator);
    } catch(IOException ex) {
      log.info("cli download fail");
    }
  }
}
