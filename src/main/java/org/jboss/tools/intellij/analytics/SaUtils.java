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

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.Writer;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.zip.GZIPInputStream;

public class SaUtils {

    /**
     * <p>Get Stack Analysis Report for given manifest file using CLI.</p>
     *
     * @param filePath Path to target manifest file
     *
     * @return A JSONObject having SA Report.
     *
     * @throws IOException In case of process failure
     * @throws InterruptedException In case of process failure
     */
    public JSONObject getReport(String filePath) throws IOException, InterruptedException {
        // Get the SA report using CLI and return JSON data
        return new JSONObject(new SaProcessExecutor().performStackAnalysis(filePath));
    }


    /**
     * <p>Open a custom editor window.</p>
     *
     * <p>The custom editor window will open a file which will have browser attached to it.</p>
     *
     * @param instance An instance of FileEditorManager.
     * @param url URL to be opened in attached browser.
     *
     * @throws IOException In case of process failure
     */
    public void openCustomEditor(FileEditorManager instance, String url) throws IOException {

        // Close custom editor if already opened in previous run
        closeCustomEditor(instance);

        // Create a temp file in which is registered with SaReportEditorProvider.
        File reportFile = File.createTempFile("CRDA-", ".sa");

        //Save the SA Report URL in file, which will be loaded in browser
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportFile)))) {
            writer.write(url);
        }

        // Create a virtual file from report file
        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(reportFile.getAbsolutePath());

        // Refresh the cached file from the physical file system.
        // if Virtual file is already opened in editor window from previous run then refresh file from physical file
        // else old web page will be shown in editor window.
        if (virtualFile == null){
            throw new PlatformDetectionException("Dependency Analytics Report file is not created.");
        }
        virtualFile.refresh(false, true);

        // Open the virtual file in editor window, which will show the SA report.
        instance.openFile(virtualFile, true, false);
    }


    /**
     * <p>Close a custom editor window if already opened.</p>
     *
     * @param instance An instance of FileEditorManager.
     */
    private void closeCustomEditor(FileEditorManager instance){
        // Check if IDE is having open files in editor worker.
        if (instance.hasOpenFiles()) {
            // get all open files from editor workspace
            VirtualFile[] openFiles = instance.getOpenFiles();

            // iterate  through all files and if Report file is open then close it
            for (VirtualFile openFile : openFiles){
                if (openFile.getExtension().equals("sa")){
                    // Close the Report file in workspace,
                    // dispose method of SaReportEditor will delete file from filesystem as well.
                    instance.closeFile(openFile);

                    // Refresh the project from physical filesystem.
                    openFile.refresh(false, true);
                    break;
                }
            }
        }
    }


    /**
     * <p>Extract given tar.gz file.</p>
     *
     * @param cliTarBallName Tar file to be extracted.
     * @param cliBinaryName File which need to be extracted from tar
     *
     * @throws IOException In case of process failure
     */
    public void unTarBundle(final String cliTarBallName, final String cliBinaryName) throws IOException {
        // Logic to extract downloaded file into a directory

        // Get plugin directory to store extracted data
        String sandBox = Platform.pluginDirectory;

        // CLI Binary file
        final File cliBinaryDest = new File(sandBox, cliBinaryName);

        try (FileInputStream fileInputStream = new FileInputStream(sandBox + File.separator + cliTarBallName);
             GZIPInputStream gzipInputStream = new GZIPInputStream(new BufferedInputStream(fileInputStream));
             TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipInputStream)) {

            TarArchiveEntry tarEntry;
            while ((tarEntry = tarArchiveInputStream.getNextTarEntry()) != null) {
                File outputFile = new File(sandBox + File.separator + tarEntry.getName());
                outputFile.getParentFile().mkdirs();
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                    IOUtils.copy(tarArchiveInputStream, fileOutputStream);
                }
            }
            cliBinaryDest.setExecutable(true);
        }
    }
}
