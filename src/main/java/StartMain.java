package main.java;

import main.java.orgv2.MainJFramePage;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class StartMain {
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Map<String,String> map=new HashMap<>(1);
                Stream.of(args).forEach(s->{
                    String[] array=s.split("\\=");
                    String key=array[0].replaceAll("\\-\\-","");
                    String value=array[1];
                    map.put(key,value);
                });
                MainJFramePage frame = new MainJFramePage(map);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}
