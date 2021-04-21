package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.util.SystemInfo;

public class CLI {
  public String cliBundleName;

  private static final CLI WINDOWS = new CLI("crda_0.1.2_Windows_64bit.tar.gz");
  private static final CLI LINUX = new CLI("crda_0.1.2_Linux_64bit.tar.gz");
  private static final CLI MACOS = new CLI("crda_0.1.2_macOS_64bit.tar.gz");

  private CLI(String cliBundleName) {
    this.cliBundleName = cliBundleName;
  }

  private static CLI detect() {
    if (SystemInfo.isLinux)
      return LINUX;
    if (SystemInfo.isWindows)
      return WINDOWS;
    if (SystemInfo.isMac)
      return MACOS;
    throw new PlatformDetectionException(SystemInfo.OS_NAME + " is not supported");
  }

  public static final CLI current = detect();
}
