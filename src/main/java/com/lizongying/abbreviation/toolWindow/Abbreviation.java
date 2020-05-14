package com.lizongying.abbreviation.toolWindow;

import com.intellij.openapi.wm.ToolWindow;
import com.lizongying.abbreviation.Notifier;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import java.io.IOException;


public class Abbreviation {
    private JButton search;
    private JTextArea result;
    private JTextField keyword;
    private JPanel abbreviationContent;

    private static final String url = "http://technologytransfer.cn/api/abbr/";

    public Abbreviation(ToolWindow toolWindow) {
        search.addActionListener(e -> {
            String k = keyword.getText().trim();
            if (!k.isEmpty()) {
                getData(k);
            }
        });
    }

    class MyResponseHandler implements ResponseHandler<String> {

        public String handleResponse(final HttpResponse response) throws IOException {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return "";
                } else {
                    return EntityUtils.toString(entity);
                }
            } else {
                return "";
            }
        }
    }

    public void getData(String k) {
        keyword.setText(k);
        result.setText("");
        Notifier notifier = new Notifier();
        notifier.notify("Begin");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        ResponseHandler<String> responseHandler = new MyResponseHandler();
        HttpGet httpGet = new HttpGet(url + k);
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectTimeout(1000)
                .setConnectionRequestTimeout(1000)
                .setSocketTimeout(1000)
                .build();
        httpGet.setConfig(requestConfig);
        String data;
        try {
            data = httpClient.execute(httpGet, responseHandler);
            if (data.isEmpty()) {
                notifier.notify("Error");
                return;
            }
            result.setText(data);
            notifier.notify("Success");
        } catch (IOException e) {
            notifier.notify("Error");
        }
    }

    public JPanel getContent() {
        return abbreviationContent;
    }
}
