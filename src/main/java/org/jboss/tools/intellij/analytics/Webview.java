package org.jboss.tools.intellij.analytics;

import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.impl.IdeFrameImpl;
import com.intellij.ui.jcef.JBCefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Webview {
    public void webview(String url) {
        try {
            Window activeFrame = IdeFrameImpl.getActiveFrame();
            if (activeFrame == null) return;
            JFrame frame = new IdeFrameImpl();
            frame.setTitle("Dependency Analytics Report");
            frame.setLayout(new BorderLayout());
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            JBCefBrowser browser = new JBCefBrowser(url);

            JTextField urlBar = new JTextField(url);
            urlBar.addActionListener(event -> browser.loadURL(urlBar.getText()));
            frame.add(urlBar, BorderLayout.NORTH);

            frame.add(browser.getComponent(), BorderLayout.CENTER);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    Disposer.dispose(browser);
                }
            });
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

