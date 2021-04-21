package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.util.SystemInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class CommandExecutor {

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


	public String execute(String command) {
		// Logic to execute given CLI command and get the result.
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(this.getPrompt(), this.getCmd(), command);
		 try {
			 	processBuilder.redirectErrorStream(true);
			 	Process process = processBuilder.start();

		        StringBuilder output = new StringBuilder();
		        InputStreamReader isr = new InputStreamReader(process.getInputStream(), "UTF-8");
		        BufferedReader reader = new BufferedReader(isr);

		        String line;
		        while ((line = reader.readLine()) != null) {
		            output.append(line + "\n");
		        }

		        reader.close();
	            isr.close();

		        int exitVal = process.waitFor();
		        if (exitVal == 0 || exitVal == 2) {
		            return output.toString();
		        } else {
		        	return String.valueOf(exitVal);
		        }

		    } catch (IOException e) {
		    	e.printStackTrace();
		    } catch (InterruptedException e) {
		    	e.printStackTrace();
		    }
		 return "";
	}
}
