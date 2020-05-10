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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Abbreviation {
    private JButton search;
    private JTextArea result;
    private JTextField keyword;
    private JPanel abbreviationContent;

    private static final String url = "https://www.abbreviations.com/abbreviation/";

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
        } catch (IOException e) {
            notifier.notify("Error");
            return;
        }
        Document doc = Jsoup.parse(data);
        Elements items = doc.select("table.no-margin tr");
        Map<String, Integer> map = new TreeMap<>();
        List<String> listShow = new ArrayList<>();
        if (!items.isEmpty()) {
            items.forEach((e) -> {
                Element i = e.select("td.tm>a").first();
                Elements i3 = e.select("td.rt>span#abbr-rate>span.sf");
                map.put(i.text(), i3.size());
            });
            List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
            list.sort((Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) ->
                    o2.getValue().compareTo(o1.getValue())
            );
            for (Map.Entry<String, Integer> e : list) {
                listShow.add(e.getKey());
            }
            result.setText(String.join("\n", listShow));
            notifier.notify("Success");
        } else {
            notifier.notify("Empty");
        }
    }

    public JPanel getContent() {
        return abbreviationContent;
    }
}
