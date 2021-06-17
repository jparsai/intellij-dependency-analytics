package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.util.SystemInfo;

public class Cli {
  public String cliBinaryName;
  public String cliReleaseTag;

  // Set name of CLI binary and release tag
  private static final Cli WINDOWS = new Cli("crda.exe", "v0.2.2");
  private static final Cli LINUX = new Cli("crda", "v0.2.2");
  private static final Cli MACOS = new Cli("crda", "v0.2.2");

  private Cli(String cliBinaryName, String cliReleaseTag) {
    this.cliBinaryName = cliBinaryName;
    this.cliReleaseTag = cliReleaseTag;
  }

  private static Cli detect() {
    if (SystemInfo.isLinux)
      return LINUX;
    if (SystemInfo.isWindows)
      return WINDOWS;
    if (SystemInfo.isMac)
      return MACOS;
    throw new PlatformDetectionException(SystemInfo.OS_NAME + " is not supported");
  }

  public static final Cli current = detect();
}
