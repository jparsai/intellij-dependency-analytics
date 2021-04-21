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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;

import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;


public final class CliReleaseDownloader {
  private static Logger LOGGER = LoggerFactory.getLogger(CliReleaseDownloader.class);
  private final IdeaPluginDescriptor descriptor = PluginManager
          .getPlugin(PluginId.getId("org.jboss.tools.intellij.analytics"));
  private String cliTarBallName;
  private final String cliBinaryName;
  private final String cliReleaseTag;
  private final ICookie cookies;
  private final GitHubRelease cliRelease;

  public CliReleaseDownloader(final String cliTarBallName, final String cliBinaryName,
                              final String cliReleaseTag, final ICookie cookies, final String repoName) throws IOException {
    this.cliTarBallName = cliTarBallName;
    this.cliBinaryName = cliBinaryName;
    this.cliReleaseTag = cliReleaseTag;
    this.cookies = cookies;
    this.cliRelease = new GitHubRelease(repoName);
  }


  public String download(final ProgressIndicator indicator) throws IOException {
    // Download Tarball and extract it
    // and return tha path of directory containing CLI binary
    File sandBox = descriptor.getPath();
    final File cliTarBallDest = new File(sandBox, cliTarBallName);
    final File cliBinaryDest = new File(sandBox, cliBinaryName);

    // If tarball is present in system then assuming its already been extracted
    // and using existing binary
    if (cliTarBallDest.exists()) {
      return sandBox.getAbsolutePath();
    }

    final ActionMessage telemetry = TelemetryService.instance()
            .action("cli:download").property("cliVersion", cliReleaseTag);
    try {
      // Download the given tarball from Github repo
      final String url = this.cliRelease.getDownloadUri(cliReleaseTag, cliTarBallName);
      HttpRequests
        .request(url)
        .productNameAsUserAgent()
        .saveToFile(cliTarBallDest, indicator);

      // Extract the tarball
      unTarBundle(sandBox);

      cliBinaryDest.setExecutable(true);
      cookies.setValue(ICookie.Name.CLIVersion, cliReleaseTag);
      telemetry.send();

      // Return directory location having CLI binary
      return sandBox.getAbsolutePath();
    } catch (IOException e) {
      telemetry.error(e).send();
      LOGGER.warn(e.getLocalizedMessage(), e);
      throw e;
    }
  }


  private void unTarBundle(File sandBox) throws IOException {
    TarArchiveInputStream tarArchiveInputStream = null;
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    GZIPInputStream gzipInputStream = null;

    try {
      fileInputStream = new FileInputStream(sandBox.getAbsolutePath() + File.separator + cliTarBallName);
      gzipInputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));
      tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream);
      TarArchiveEntry tarEntry = null;

      while ((tarEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
        File outputFile = new File(sandBox + File.separator + tarEntry.getName());
        outputFile.getParentFile().mkdirs();
        fileOutputStream = new FileOutputStream(outputFile);
        IOUtils.copy(tarArchiveInputStream, fileOutputStream);
      }
    }catch(IOException e) {
      throw e;
    }finally {
      if(tarArchiveInputStream != null) {
          tarArchiveInputStream.close();
      }
      if(fileInputStream != null) {
        fileInputStream.close();
      }
      if(fileOutputStream != null) {
        fileOutputStream.close();
      }
      if(gzipInputStream != null) {
        gzipInputStream.close();
      }
    }
  }
}
