package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.util.SystemInfo;

public class Cli {
  public String cliBinaryName;
  public String cliTarBallName;

  private static final Cli WINDOWS = new Cli("crda_version_Windows_64bit.tar.gz","crda.exe");
  private static final Cli LINUX = new Cli("crda_version_Linux_64bit.tar.gz","crda");
  private static final Cli MACOS = new Cli("crda_version_macOS_64bit.tar.gz","crda");

  private Cli(String cliTarBallName, String cliBinaryName) {
    this.cliBinaryName = cliBinaryName;
    this.cliTarBallName = cliTarBallName;
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
