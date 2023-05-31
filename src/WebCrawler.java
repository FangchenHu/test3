import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class WebCrawler extends JFrame {

    private static final long serialVersionUID = 1L;

    private JButton btnCrawl;
    private JTextField txtURL;
    private JTextArea txtResult;

    private HashSet<String> links;

    public WebCrawler() {
        super("Web Crawler");

        links = new HashSet<String>();

        JPanel pnlInput = new JPanel();
        txtURL = new JTextField("http://", 30);
        btnCrawl = new JButton("Crawl");
        btnCrawl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = txtURL.getText();
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    crawl(url);
                } else {
                    txtResult.setText("Error: Invalid URL");
                }
            }
        });
        pnlInput.add(txtURL);
        pnlInput.add(btnCrawl);

        txtResult = new JTextArea(15, 50);
        txtResult.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(txtResult);

        add(pnlInput, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void crawl(String url) {
        txtResult.setText("");
        links.clear();
        crawlPage(url);
        for (String link : links) {
            txtResult.append(link + "\n");
        }
    }

    private void crawlPage(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            Pattern pattern = Pattern.compile("<a\\s+href=\"(.*?)\"");
            Matcher matcher = pattern.matcher(content.toString());
            while (matcher.find()) {
                String link = matcher.group(1);
                if (link.startsWith("http://") || link.startsWith("https://")) {
                    links.add(link);
                } else if (link.startsWith("/") || link.startsWith("#")) {
                    URL baseUrl = new URL(url);
                    String absoluteLink = baseUrl.getProtocol() + "://" + baseUrl.getHost() + link;
                    links.add(absoluteLink);
                } else {
                    URL baseUrl = new URL(url);
                    String absoluteLink = baseUrl.getProtocol() + "://" + baseUrl.getHost() + "/" + link;
                    links.add(absoluteLink);
                }
            }
        } catch (IOException e) {
            txtResult.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new WebCrawler();
    }

}