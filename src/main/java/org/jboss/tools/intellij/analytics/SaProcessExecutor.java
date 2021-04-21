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

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.SystemInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


public class SaProcessExecutor {
	private static final Logger logger = Logger.getInstance(SaProcessExecutor.class);


	/**
	 * <p>Authenticate a CRDA CLI user.</p>
	 *
	 * If CLI config file is not present in host machine then user is considered to be new
	 * and needs to be authenticated in CRDA Platform.
	 *
	 * @throws IOException In case of process failure
	 * @throws InterruptedException In case of process failure
	 */
	public void authenticateUser() throws IOException, InterruptedException {
		// Check if crda config file is already present in system. if not create CLI config file.
		if(!Files.exists(Paths.get(SaConfig.CLI_CONFIG_FILE_PATH))) {
			logger.info("Authenticating user.");

			// Run CLI command to set user consent to False for CLI telemetry data collection.
			String cmd = SaConfig.CLI_TELEMETRY_CONSENT;
			if (!SystemInfo.isWindows) cmd = "./" + cmd;
			execute(cmd);

			// Run command to authenticate user in CRDA Platform.
			cmd = SaConfig.CLI_SET_USER;
			if (!SystemInfo.isWindows) cmd = "./" + cmd;
			execute(cmd.replace("userID", UUID.randomUUID().toString()));
		}
	}


	/**
	 * <p>Perform Stack Analysis on given file.</p>
	 *
	 * @param filePath Path to target manifest file.
	 *
	 * @return String object having analysis report.
	 *
	 * @throws IOException In case of process failure
	 * @throws InterruptedException In case of process failure
	 */
	public String performStackAnalysis(String filePath) throws IOException, InterruptedException {
		logger.info("Starting Stack Analysis.");
		// Authenticate user, in case file has been deleted after loading the plugin
		authenticateUser();

		String cmd = SaConfig.CLI_ANALYSE;
		// Handle the space in directory names.
		if (SystemInfo.isWindows) {
			if (filePath.contains(" ")) filePath = "\"" + filePath + "\"";
		} else {
			filePath = filePath.replaceAll(" ", "\\\\ ");
			cmd = "./" + cmd;
		}

		// Set the path of directory having CLI binary into command
		cmd = cmd.replace("filePath", filePath);

		// Execute CLI command for analysis
		return execute(cmd);
	}


	private String getPrompt() {
		if (SystemInfo.isWindows)
			return "cmd.exe";
		return "bash";
	}
	
	private String getCmd() {
		if (SystemInfo.isWindows)
			return "/c";
		return "-c";
	}


	/**
	 * <p>Execute CLI commands.</p>
	 *
	 * @param command Command to be executed.
	 *
	 * @return String object having result of command.
	 *
	 * @throws IOException In case of process failure
	 * @throws InterruptedException In case of process failure
	 */
	public String execute(String command) throws IOException, InterruptedException {
		// Logic to execute given CLI command and get the result.
		ProcessBuilder processBuilder = new ProcessBuilder();
		// Set CLI binary location as working directory for process.
		processBuilder.directory(new File(Platform.pluginDirectory));
		processBuilder.command(this.getPrompt(), this.getCmd(), command);
		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();
		StringBuilder output = new StringBuilder();

		try (InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8);
			 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)){

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				output.append(line).append("\n");
			}

			int exitVal = process.waitFor();

			// Return data according to exit code of command.
			if (exitVal == 0 || exitVal == 2) {
				return output.toString();
			} else {
				logger.info("Failed to execute command = "+command);
				throw new PlatformDetectionException(output.toString());
			}
		}
	}
}
