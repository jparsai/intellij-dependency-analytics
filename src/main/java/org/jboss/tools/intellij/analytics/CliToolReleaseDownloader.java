package org.jboss.tools.intellij.analytics;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.io.HttpRequests;
import com.redhat.devtools.intellij.telemetry.core.service.TelemetryMessageBuilder.ActionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public final class CliToolReleaseDownloader {
  private static Logger LOGGER = LoggerFactory.getLogger(CliToolReleaseDownloader.class);
  private final IdeaPluginDescriptor descriptor = PluginManager.getPlugin(PluginId.getId("org.jboss.tools.intellij.analytics"));
  private final String fileName;
  private final ICookie cookies;
  private final GitHubRelease release;

  public CliToolReleaseDownloader(final String fileName, final ICookie cookies) throws IOException {
    this.fileName = fileName;
    this.cookies = cookies;
    this.release = new GitHubRelease("fabric8-analytics/cli-tools");
  }

  private boolean isNewRelease(final String releaseLabel) {
    LOGGER.warn("Inside CliToolReleaseDownloader:isNewRelease");
    final String currentVersion = cookies.getValue(ICookie.Name.CLIVersion);
    return !releaseLabel.equals(currentVersion);
  }

  public File download(final ProgressIndicator indicator) throws IOException {
    LOGGER.warn("Inside CliToolReleaseDownloader:download");
    final File dest = new File(descriptor.getPath(), fileName);
    final String latestReleaseTag = this.release.getLatestRelease();

    if (!isNewRelease(latestReleaseTag) && dest.exists()) {
      return dest;
    }

    final ActionMessage telemetry = TelemetryService.instance().action("cli:download").property("cliVersion", latestReleaseTag);
    try {
      final String url = this.release.getDownloadUri(latestReleaseTag, this.fileName);
      LOGGER.warn("url = "+url);
      HttpRequests
        .request(url)
        .productNameAsUserAgent()
        .saveToFile(dest, indicator);
      LOGGER.warn("downloaded CLI");

      dest.setExecutable(true);
      cookies.setValue(ICookie.Name.CLIVersion, latestReleaseTag);
      telemetry.send();
      return dest;
    } catch (IOException e) {
      LOGGER.warn(e.getMessage());
      telemetry.error(e).send();
      LOGGER.warn(e.getLocalizedMessage(), e);
      throw e;
    }
  }
}
