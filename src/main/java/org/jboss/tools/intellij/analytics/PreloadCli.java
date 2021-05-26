package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;


public final class PreloadCli extends PreloadingActivity {
  private static final Logger log = Logger.getInstance(PreloadCli.class);
  private final ICookie cookies = ServiceManager.getService(Settings.class);


  @Override
  public void preload(ProgressIndicator indicator) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      return;
    }
    log.warn("cli preload is called");

    try {
      final String cliPath = System.getenv("CLI_FILE_PATH");

      //CLI Binary file will be used to run system process.
      if (cliPath == null) {
        final CliReleaseDownloader bundle = new CliReleaseDownloader(
                Platform.current.cliTarBallName,
                cookies,
                "fabric8-analytics/cli-tools",
                true);
        // Download the tarball
        bundle.download(indicator);

        // Untar file to get CLI Binary
        bundle.unTarBundle(Platform.current.cliTarBallName, Cli.current.cliBinaryName);
      }

      // Check if crda config file is present in system
      // if not then generate one and set User Key
      new CliProcessExecutor().authenticateUser();
    } catch(Exception ex) {
      log.error("cli preload failed ", ex);
    }
  }
}
