package oj.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class OjFrame extends JFrame {
    private final JComboBox<String> problemBox;
    private final JComboBox<String> langBox;
    private final JTextArea codeArea;
    private final JButton submitBtn;
    private final JLabel resultLabel;
    private final DefaultTableModel historyModel;
    private final JTable historyTable;

    private List<Integer> problemIds = new ArrayList<>();

    public OjFrame() {
        super("Mini-OJ Desktop Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 560));
        setSize(900, 650);
        setLayout(new BorderLayout(6, 6));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        toolbar.add(new JLabel("Problem:"));

        problemBox = new JComboBox<>();
        problemBox.setPreferredSize(new Dimension(300, 28));
        toolbar.add(problemBox);

        toolbar.add(new JLabel("Language:"));
        langBox = new JComboBox<>(new String[]{"cpp", "python"});
        toolbar.add(langBox);

        submitBtn = new JButton("Submit");
        toolbar.add(submitBtn);

        resultLabel = new JLabel("Ready");
        resultLabel.setForeground(new Color(0x24, 0x5B, 0x78));
        toolbar.add(resultLabel);
        add(toolbar, BorderLayout.NORTH);

        codeArea = new JTextArea();
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        codeArea.setTabSize(4);
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createTitledBorder("Source Code"));
        add(codeScroll, BorderLayout.CENTER);

        String[] columns = {
            "Submission ID", "Problem ID", "Language",
            "Status", "Passed/Total", "Time (ms)"
        };
        historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyModel);
        historyTable.setFillsViewportHeight(true);
        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.setPreferredSize(new Dimension(0, 180));
        historyScroll.setBorder(BorderFactory.createTitledBorder("Submission History"));
        add(historyScroll, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    public void loadProblems(List<Integer> ids, List<String> titles) {
        problemIds = new ArrayList<>(ids);
        problemBox.removeAllItems();
        for (int i = 0; i < ids.size(); i++) {
            problemBox.addItem(ids.get(i) + " " + titles.get(i));
        }
    }

    public void appendHistory(
            int submissionId,
            int problemId,
            String language,
            String status,
            String passedTotal,
            long elapsedMs) {
        historyModel.addRow(new Object[]{
            submissionId, problemId, language, status, passedTotal, elapsedMs
        });
        int lastRow = historyModel.getRowCount() - 1;
        historyTable.scrollRectToVisible(historyTable.getCellRect(lastRow, 0, true));
    }

    public int getSelectedProblemId() {
        int index = problemBox.getSelectedIndex();
        if (index < 0 || index >= problemIds.size()) {
            return -1;
        }
        return problemIds.get(index);
    }

    public String getSelectedLang() {
        return (String) langBox.getSelectedItem();
    }

    public String getSourceCode() {
        return codeArea.getText();
    }

    public JButton getSubmitBtn() {
        return submitBtn;
    }

    public JLabel getResultLabel() {
        return resultLabel;
    }
}
