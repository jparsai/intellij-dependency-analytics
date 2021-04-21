package org.jboss.tools.intellij.analytics;

public class CliConfig {
    public static final String CLI_AUTH = "yes \"\" | binarypath/crda auth";
    public static final String CLI_ANALYSE = "crda analyse filepath -j -c";
    public static final String CLI_CONFIG_FILE = System.getProperty("user.home")+".crda/config.yaml";

    public static String cliBinaryPath;
    public String getCliBinaryPath() {
        return cliBinaryPath;
    }
    public void setCliBinaryPath(String cliBinaryPath) {
        this.cliBinaryPath = cliBinaryPath;
    }
}
