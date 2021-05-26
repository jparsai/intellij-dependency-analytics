package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.jcef.JBCefBrowser;
import icons.SdkIcons;

import javax.swing.*;


public class WebView {
    private final JBCefBrowser browser;

    public WebView(String url) {
        // Open given url in JCEF browser
        browser = new JBCefBrowser();
        browser.loadURL(url);
    }

    public JComponent getContent() {
        // return content of web page to be added in tool window
        return browser.getComponent();
    }

    public void openToolWindow(Project project) {
        // Logic to open tool window and show the report
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        // Get the tool window ID if already opened by previous run
        ToolWindow toolWindow = toolWindowManager.getToolWindow("Dependency Analytics");

        // If running for first time register a tool window in bottom left
        if (toolWindow == null) {
            RegisterToolWindowTask registerToolWindowTask = new RegisterToolWindowTask("Dependency Analytics",
                    ToolWindowAnchor.BOTTOM, null, false, true, false,
                    true, null, SdkIcons.Sdk_default_icon, null);
            toolWindow = toolWindowManager.registerToolWindow(registerToolWindowTask);
            toolWindow.setToHideOnEmptyContent(true);
        }

        ContentManager contentManager = toolWindow.getContentManager();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        // Add the browser content into tool window
        Content content = contentFactory.createContent(getContent(),
                "Dependency Analytics Report", false);
        content.setCloseable(true);

        // Close existing tabs from tool window
        contentManager.removeAllContents(true);

        // Add new tab in tool window
        contentManager.addContent(content);
        contentManager.setSelectedContent(content, true);
        toolWindow.show();
    }
}
