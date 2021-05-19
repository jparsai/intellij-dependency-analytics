package org.jboss.tools.intellij.analytics;

import com.intellij.ui.jcef.JBCefBrowser;
import javax.swing.*;


public class WebView {
    private final JBCefBrowser browser;

    public WebView(String url) {
        browser = new JBCefBrowser();
        browser.loadURL(url);
    }

    public JComponent getContent() {
        return browser.getComponent();
    }
}
