package org.jboss.tools.intellij.analytics;

import java.nio.file.Paths;

public class CliConfig {
    // Auth command, to register a CRDA user
    // Replace binarypath with sendbox location
    public static final String CLI_TELEMETRY_CONSENT = Paths.get("binarypath", "crda config set consent_telemetry false").toString();
    public static final String CLI_AUTH = Paths.get("yes \"\" | binarypath", "crda auth").toString();

    // Analyse command to run SA
    // Replace filepath with manifest file location
    public static final String CLI_ANALYSE = "crda analyse filepath -j -c -m intellij";

    // CLI config file name and location
    public static final String CLI_CONFIG_FILE = Paths.get(System.getProperty("user.home"), ".crda", "config.yaml").toString();
}
