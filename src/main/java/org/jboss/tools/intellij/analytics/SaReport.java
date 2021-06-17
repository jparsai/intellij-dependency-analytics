package org.jboss.tools.intellij.analytics;

import com.intellij.ui.jcef.JBCefBrowser;
import javax.swing.*;

public class SaReport {
    private final JBCefBrowser browser;

    /**
     * <p>Initialize JCEF Browser.</p>
     */
    public SaReport(String url) {
        // Open given url in JCEF browser
        browser = new JBCefBrowser();
        browser.loadURL(url);
    }


    /**
     * <p>Get JCEF Browser component.</p>
     *
     * <p>This method will be called within ReportFileEditor.</p>
     *
     * @return JComponent Object
     */
    public JComponent getContent() {
        // return content of browser to be attached in editor window
        return browser.getComponent();
    }
}
