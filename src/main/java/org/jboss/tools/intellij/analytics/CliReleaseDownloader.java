package org.jboss.tools.intellij.analytics;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.zip.GZIPInputStream;


public final class CliReleaseDownloader extends GitHubReleaseDownloader {
  // Extends GitHubReleaseDownloader for CLI
  private static final Logger log = LoggerFactory.getLogger(CliReleaseDownloader.class);

  public CliReleaseDownloader(final String fileName, final ICookie cookies, final String repoName, final boolean forCli) throws IOException {
    super(fileName, cookies, repoName, forCli);
  }

  public void unTarBundle(final String cliTarBallName, final String cliBinaryName) throws IOException {
    // Logic to extract downloaded file into a directory

    // Get plugin directory to store extracted data
    String sandBox = Platform.pluginDirectory;

    // CLI Binary file
    final File cliBinaryDest = new File(sandBox, cliBinaryName);

    try (FileInputStream fileInputStream = new FileInputStream(sandBox + File.separator + cliTarBallName);
         GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));
         TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream)) {

      TarArchiveEntry tarEntry;
      while ((tarEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
        File outputFile = new File(sandBox + File.separator + tarEntry.getName());
        outputFile.getParentFile().mkdirs();
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
          IOUtils.copy(tarArchiveInputStream, fileOutputStream);
        }catch(IOException e) {
          log.error("Failed to untar", e);
          throw e;
        }
      }
      cliBinaryDest.setExecutable(true);
    }
  }
}
