package com.fpp.code.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    public void coreConfig() throws IOException {
        Stage secondWindow=new Stage();
        Parent root = new FXMLLoader(MainController.class.getResource("/views/config.fxml")).load();
        Scene scene=new Scene(root);
        secondWindow.setTitle("核心配置");
        secondWindow.setScene(scene);
        secondWindow.show();
    }
}
