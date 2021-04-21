package org.jboss.tools.intellij.analytics;

public class CliConfig {
    // Auth command, to register a CRDA user
    // Replace binarypath with sendbox location
    public static final String CLI_AUTH = "yes \"\" | binarypath/crda auth";

    // Analyse command to run SA
    // Replace filepath with manifest file location
    public static final String CLI_ANALYSE = "crda analyse filepath -j -c";

    // CLI config file name and location
    public static final String CLI_CONFIG_FILE = System.getProperty("user.home")+".crda/config.yaml";

    public static String cliBinaryPath;
    public String getCliBinaryPath() {
        return cliBinaryPath;
    }
    public void setCliBinaryPath(String cliBinaryPath) {
        this.cliBinaryPath = cliBinaryPath;
    }
}
