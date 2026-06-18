package oj.gui;

import javax.swing.*;
import java.awt.*;

public class OjFrame extends JFrame {
    public JComboBox<Integer> problemSelector;
    public JComboBox<String> langSelector;
    public JTextArea srcTextArea;
    public JButton submitButton;
    public JTextArea resultTextArea;

    public OjFrame() {
        super("Mini-OJ local frontend");
        initUI();
    }

    private void initUI() {
        this.setSize(700, 550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        topPanel.add(new JLabel("Select problem ID:"));
        problemSelector = new JComboBox<>();
        topPanel.add(problemSelector);

        topPanel.add(new JLabel("Language:"));
        langSelector = new JComboBox<>(new String[]{"cpp", "python", "java"});
        topPanel.add(langSelector);

        submitButton = new JButton("Submit");
        submitButton.setBackground(new Color(0, 123, 255));
        submitButton.setForeground(Color.WHITE);
        topPanel.add(submitButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        JPanel srcPanel = new JPanel(new BorderLayout(5, 5));
        srcPanel.add(new JLabel("Source Code: "), BorderLayout.NORTH);
        srcTextArea = new JTextArea();
        srcTextArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        srcPanel.add(new JScrollPane(srcTextArea), BorderLayout.CENTER);
        centerPanel.add(srcPanel);

        JPanel resPanel = new JPanel(new BorderLayout(5, 5));
        resPanel.add(new JLabel("Stdout JSON:"), BorderLayout.NORTH);
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setBackground(new Color(245, 245,245));
        resultTextArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);
        centerPanel.add(resPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        this.add(mainPanel);
    }
}
