package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class PreloadCli extends PreloadingActivity {
  private static final Logger log = Logger.getInstance(PreloadCli.class);
  private final ICookie cookies = ServiceManager.getService(Settings.class);


  private void authenticateCliUser(String cliPath){
    // Check if crda config file is present in system
    // if not then generate one
    if(!Files.exists(Paths.get(CliConfig.CLI_CONFIG_FILE))) {
      // Set the path of directory having CLI binary into command
      String cmd = CliConfig.CLI_AUTH.replace("binarypath", cliPath);
      new CommandExecutor().execute(cmd);
    }
  }


  @Override
  public void preload(ProgressIndicator indicator) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      return;
    }
    log.warn("cli preload is called");

    try {
      String cliPath = System.getenv("CLI_FILE_PATH");

      //CLI Binary file will be used to run system process.
      if (cliPath == null) {
        final CliReleaseDownloader bundle = new CliReleaseDownloader(
                Cli.current.cliTarBallName,
                Cli.current.cliBinaryName,
                Cli.current.cliReleaseTag,
                cookies, "fabric8-analytics/cli-tools");
        cliPath = bundle.download(indicator);
      }
      // Set path of directory containing CLI binary
      // for being used in SA Action later
      new CliConfig().setCliBinaryPath(cliPath);

      // Check if crda config file is present in system
      // if not then generate one and set User Key
      authenticateCliUser(cliPath);
    } catch(Exception ex) {
      log.info("cli download fail");
    }
  }
}
