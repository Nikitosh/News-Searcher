package com.nikitosh.spbau.ui;

import com.nikitosh.spbau.parser.ParserHelper;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

public class JLabelLink extends JPanel {
    private static final int MAX_LENGTH = 60;
    private static final String ELLIPSIS = "...";

    private JLabel titleLabel;
    private JLabel contentLabel;
    private JLabel linksLabel;

    public JLabelLink(String title, String url, List<String> content, String time, boolean highlight, List<String> links) {
        if (title.length() > MAX_LENGTH) {
            title = title.substring(0, MAX_LENGTH - 3) + ELLIPSIS;
        }
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        titleLabel = new JLabel(
                "<html><p><font size=\"5\">" + getHref(url, title)
                        + "&nbsp;</font><span style=\"background-color: #D0D0D7\">" + time + "</span></p><br></html>"
        );
        contentLabel = new JLabel("<html> " + String.join("<br>", content) + "</html>");
        linksLabel = new JLabel("<html><font size=\"2\"><br>Список сославшихся:<br>"
                + String.join("<br>", links.stream().map(link -> {
                    try {
                        return getHref(link, ParserHelper.getDomainName(link));
                    } catch (URISyntaxException exception) {
                        return "";
                    }
                })
                .collect(Collectors.toList()))
                + "</font></html>");
        add(titleLabel);
        add(contentLabel);
        add(linksLabel);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED)));
        if (highlight) {
            setBackground(Color.decode("#90E09D"));
        } else {
            setBackground(Color.WHITE);
        }
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (URISyntaxException | IOException exception) {
                }
            }
        });
    }

    private static String getHref(String url, String content) {
        return "<a href=\"" + url + "\">" + content + "</a>";
    }
}
