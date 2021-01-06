package com.fpp.code;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("ComplexApplication_css.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("spring-code");
        primaryStage.setScene(scene);
//        ListView listView= (ListView) scene.lookup("#listViewTemplate");
//        ObservableList<String> objects = FXCollections.observableArrayList();
//        objects.add("增加模板");
//        listViewTemplate.setItems(objects);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
