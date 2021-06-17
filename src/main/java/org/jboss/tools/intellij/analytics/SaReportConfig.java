package org.jboss.tools.intellij.analytics;

import java.nio.file.Paths;

/**
 * <p>SA related configs.</p>
 */
public class SaReportConfig {
    // Always set CLI telemetry consent to False.
    public static final String CLI_TELEMETRY_CONSENT = Paths.get("binarypath",
            "crda config set consent_telemetry false").toString();

    // Auth command, to register a new CRDA user, replace binarypath with sandbox location while using.
    public static final String CLI_AUTH = Paths.get("yes \"\" | binarypath", "crda auth").toString();

    // CLI command to run SA, replace filepath with manifest file location while using.
    public static final String CLI_ANALYSE = "crda analyse filepath -j -c -m intellij";

    // CLI config file name and location
    public static final String CLI_CONFIG_FILE_PATH = Paths.get(System.getProperty("user.home"),
            ".crda", "config.yaml").toString();

    // SA Report file name and path
    public static final String REPORT_FILE_NAME = "Dependency Analytics Report";
    public static final String REPORT_FILE_PATH = Paths.get(Platform.pluginDirectory, REPORT_FILE_NAME).toString();
}
