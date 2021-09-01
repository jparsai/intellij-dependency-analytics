/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.intellij.analytics;

import java.nio.file.Paths;

/**
 * <p>SA related configs.</p>
 */
public class SaConfig {
    // Always set CLI telemetry consent to False.
    public static final String CLI_TELEMETRY_CONSENT = "crda config set consent_telemetry false";

    // Auth command, to register a new CRDA user.
    public static final String CLI_SET_USER = "crda config set crda_key userID";

    // CLI command to run SA, replace filePath with manifest file location while using.
    public static final String CLI_ANALYSE = "crda analyse filePath -j -c -m intellij";

    // CLI config file name and location
    public static final String CLI_CONFIG_FILE_PATH = Paths.get(System.getProperty("user.home"),
            ".crda", "config.yaml").toString();

    // SA Report file name and path
    public static final String REPORT_FILE_NAME = "Dependency Analytics Report";
    public static final String REPORT_FILE_PATH = Paths.get(Platform.pluginDirectory, REPORT_FILE_NAME).toString();
}
