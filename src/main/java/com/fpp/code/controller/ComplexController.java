package com.fpp.code.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ComplexController  implements Initializable {
    @FXML
    private ListView listViewTemplate;

    @FXML
    private Pane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listViewTemplate.prefWidthProperty().bind(pane.widthProperty());//宽度绑定为Pane宽度
        listViewTemplate.prefHeightProperty().bind(pane.heightProperty());//高度绑定为Pane高度
    }

    @FXML
    public void addTemplate() throws IOException {
        Stage secondWindow=new Stage();
        Parent root = new FXMLLoader(getClass().getResource("/views/new_template.fxml")).load();
        Scene scene=new Scene(root);
        secondWindow.setTitle("核心配置");
        secondWindow.setScene(scene);
        secondWindow.show();
    }
}
