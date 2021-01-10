package com.fpp.code.fx.controller;

import com.fpp.code.Main;
import com.fpp.code.fx.aware.TemplateContextProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author fpp
 */
public class ComplexController extends TemplateContextProvider implements Initializable {
    private Logger logger= LogManager.getLogger(getClass());

    @FXML
    private ListView listViewTemplate;

    @FXML
    private Pane pane;

    @FXML
    private VBox content;

    @FXML
    private SplitPane splitPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        splitPane.setDividerPosition(0,0.15);
        splitPane.setDividerPosition(1,1.0);
        //宽度绑定为Pane宽度
        listViewTemplate.prefWidthProperty().bind(pane.widthProperty());
        //高度绑定为Pane高度
        listViewTemplate.prefHeightProperty().bind(pane.heightProperty());

        ObservableList<Label> apiList= FXCollections.observableArrayList();
        Set<String> multipleTemplateNames = getTemplateContext().getMultipleTemplateNames();
        multipleTemplateNames.forEach(multipleTemplateName->{
            Label label = new Label(multipleTemplateName);
            label.setTextAlignment(TextAlignment.CENTER);
            apiList.add(label);
        });
        apiList.add(new Label("dfd123"));
        listViewTemplate.setItems(apiList);
        listViewTemplate.getSelectionModel().select(0);
        listViewTemplate.requestFocus();
        Main.userOperateCache.setTemplateNameSelected(((Label)listViewTemplate.getSelectionModel().getSelectedItem()).getText());
        try {
            Parent root = new FXMLLoader(getClass().getResource("/views/templates_operate.fxml")).load();
            content.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void addTemplate() throws IOException {
        Stage secondWindow=new Stage();
        Parent root = new FXMLLoader(getClass().getResource("/views/new_template.fxml")).load();
        Scene scene=new Scene(root);
        secondWindow.setTitle("新建模板");
        secondWindow.setScene(scene);
        secondWindow.show();
    }
}