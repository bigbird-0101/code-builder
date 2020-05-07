package main.java;

import main.java.org.CodeMainJFrame;

import java.awt.*;

public class StartMain {
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                CodeMainJFrame frame = new CodeMainJFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
