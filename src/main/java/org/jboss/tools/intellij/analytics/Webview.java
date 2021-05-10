package org.jboss.tools.intellij.analytics;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.JSONObject;


public class Webview extends Application {

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dependency Analytics Report");
        WebView webView = new WebView();
        JSONObject saReportJson = new JSONObject(new CliConfig().getSaReport());
        webView.getEngine().load(saReportJson.getString("report_link"));
        VBox vBox = new VBox(webView);
        Scene scene = new Scene(vBox, 1260, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
