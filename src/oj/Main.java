package oj;

import oj.gui.OjFrame;
import oj.gui.OjController;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Swing 线程安全推荐写法
        SwingUtilities.invokeLater(() -> {
            OjFrame frame = new OjFrame();
            new OjController(frame);
            frame.setVisible(true);
        });
    }
}
