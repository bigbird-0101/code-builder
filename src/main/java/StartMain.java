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
                String config="code.properties";
                if(args.length>0){
                    config=args[0];
                }
                CodeMainJFrame frame = new CodeMainJFrame(config);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
