package org.jboss.tools.intellij.analytics;

import com.intellij.ui.jcef.JBCefBrowser;
import javax.swing.*;


public class WebView {
    private final JBCefBrowser browser;

    public WebView(String url) {
        // Opne given url in JCEF browser
        browser = new JBCefBrowser();
        browser.loadURL(url);
    }

    public JComponent getContent() {
        // return content of web page to be added in tool window
        return browser.getComponent();
    }
}
