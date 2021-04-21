package org.jboss.tools.intellij.analytics;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHRelease;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GitHubRelease {
  private final GHRepository repo;
  private static Logger LOGGER = LoggerFactory.getLogger(CliToolReleaseDownloader.class);


  public GitHubRelease(final String repository) throws IOException {
    final GitHub github = GitHub.connectAnonymously();
    this.repo = github.getRepository(repository);
  }

  public String getLatestRelease() throws IOException {
    return this.repo.getLatestRelease().getTagName();
  }

  public String getDownloadUri(final String releaseLabel, final String fileLabel) throws IOException {
    LOGGER.warn("inside GitHubRelease:getDownloadUri");
    LOGGER.warn("releaseLabel = "+releaseLabel);
    LOGGER.warn("fileLabel = "+fileLabel);

    final GHRelease release = this.repo.getReleaseByTagName(releaseLabel);

    final GHAsset asset = release.listAssets()
                                 .toList()
                                 .stream()
                                 .filter(a -> a.getName().equals(fileLabel))
                                 .findFirst()
                                 .orElseThrow(() -> new IOException(fileLabel + ": unable to download"));
    LOGGER.warn("asset.getBrowserDownloadUrl() = "+asset.getBrowserDownloadUrl());
    return asset.getBrowserDownloadUrl();
  }
}
