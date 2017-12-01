package com.nikitosh.spbau.ui;

import com.nikitosh.spbau.database.DatabaseHandler;
import com.nikitosh.spbau.database.PageAttributes;
import com.nikitosh.spbau.dataprocessor.TimeExtractor;
import com.nikitosh.spbau.queryprocessor.QueryProcessor;
import com.nikitosh.spbau.queryprocessor.QueryProcessorImpl;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NewsSearcherFrame extends JFrame {
    private static final String FRAME_NAME = "NewsSearcher";
    private static final int FRAME_WIDTH = 1080;
    private static final int FRAME_HEIGHT = 480;

    private QueryProcessor queryProcessor;

    public NewsSearcherFrame() {
        super(FRAME_NAME);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        queryProcessor = new QueryProcessorImpl();
        add(buildSearchPanel(this));
        pack();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null); //set JFrame to appear in center
    }

    private JPanel buildSearchPanel(JFrame frame) {
        JTextField textField = new JTextField("");
        JButton searchButton = new JButton("Search");
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    List<Integer> documentIds = queryProcessor.getDocumentsForQuery(textField.getText());
                    resultsPanel.removeAll();
                    for (int documentId : documentIds) {
                        System.out.println(documentId);
                        PageAttributes pageAttributes = DatabaseHandler.getInstance().getPageAttributesForId(documentId);
                        JPanel label = new JLabelLink(
                                pageAttributes.getTitle(),
                                DatabaseHandler.getInstance().getUrlForId(documentId),
                                Arrays.asList("Очень", "Важная", "Новость"),
                                TimeExtractor.formatDate(pageAttributes.getTime()),
                                false,
                                Arrays.asList("lenta.ru", "ria.ru"));
                        resultsPanel.add(label);
                    }
                    repaint();
                    frame.validate();
                    frame.repaint();
                } catch (IOException exception) {}
            }
        });
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(textField);
        horizontalBox.add(searchButton);
        panel.add(horizontalBox);
        panel.add(scrollPane);
        return panel;
    }
}
