package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.util.SystemInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class CliProcessExecutor {
	private final String cliPath = Platform.pluginDirectory;

	public void authenticateUser() throws IOException, InterruptedException {
		// Check if crda config file is present in system
		// if not then generate one
		if(!Files.exists(Paths.get(CliConfig.CLI_CONFIG_FILE))) {
			// Set user consent to False, as we are not allowed to
			String cmd = CliConfig.CLI_TELEMETRY_CONSENT.replace("binarypath", cliPath);
			execute(cmd);

			// Set the path of directory having CLI binary into command
			cmd = CliConfig.CLI_AUTH.replace("binarypath", cliPath);
			execute(cmd);
		}
	}


	public String performStackAnalysis(final String filePath) throws IOException, InterruptedException {
		// Logic to run SA
		// Set the path of directory having CLI binary into command
		String cmd = CliConfig.CLI_ANALYSE.replace("filepath", filePath);
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
			if (exitVal == 0 || exitVal == 2) {
				return output.toString();
			} else {
				return String.valueOf(exitVal);
			}
		}
	}
}
