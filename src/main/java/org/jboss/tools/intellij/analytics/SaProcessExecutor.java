package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.util.SystemInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class SaProcessExecutor {
	private final String cliPath = Platform.pluginDirectory;

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
		// Check if crda config file is present in system
		if(!Files.exists(Paths.get(SaReportConfig.CLI_CONFIG_FILE_PATH))) {
			// Run CLI command to set user consent to False for CLI telemetry data collection.
			execute(SaReportConfig.CLI_TELEMETRY_CONSENT.replace("binarypath", cliPath));

			// Run command to authenticate user in CRDA Platform, it will create a CLI config file.
			execute(SaReportConfig.CLI_AUTH.replace("binarypath", cliPath));
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
	public String performStackAnalysis(final String filePath) throws IOException, InterruptedException {
		// Set the path of directory having CLI binary into command
		String cmd = SaReportConfig.CLI_ANALYSE.replace("filepath", filePath);

		// Execute CLI command for analysis
		cmd = cliPath + File.separator+ cmd;
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
				return String.valueOf(exitVal);
			}
		}
	}
}
