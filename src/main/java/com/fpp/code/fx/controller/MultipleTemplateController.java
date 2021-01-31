package com.fpp.code.fx.controller;

import com.fpp.code.common.AlertUtil;
import com.fpp.code.common.Utils;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.core.factory.DefaultListableTemplateFactory;
import com.fpp.code.core.factory.GenericMultipleTemplateDefinition;
import com.fpp.code.fx.aware.TemplateContextProvider;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class MultipleTemplateController extends TemplateContextProvider implements Initializable {
    @FXML
    public FlowPane templates;

    private final Insets insets=new Insets(0,10,10,0);

    private final Set<String> selectTemplateNames=new HashSet<>();
    @FXML
    public TextField multipleTemplateName;
    @FXML
    public AnchorPane anchorPane;

    public ListView listViewTemplate;

    public ListView getListViewTemplate() {
        return listViewTemplate;
    }

    public void setListViewTemplate(ListView listViewTemplate) {
        this.listViewTemplate = listViewTemplate;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Set<String> templateNames = getTemplateContext().getTemplateNames();
        templateNames.forEach(templateName->{
            CheckBox checkBox = new CheckBox(templateName);
            checkBox.setPadding(insets);
            templates.getChildren().add(checkBox);
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue){
                    selectTemplateNames.add(templateName);
                }else{
                    selectTemplateNames.remove(templateName);
                }
            });
        });

    }

    @FXML
    public void createMultipleTemplate() throws CodeConfigException, IOException {
        if(Utils.isEmpty(multipleTemplateName.getText())){
            AlertUtil.showWarning("请填写组合模板名");
            return;
        }
        if(getTemplateContext().getMultipleTemplateNames().contains(multipleTemplateName.getText())){
            AlertUtil.showWarning("该模板名已有，请填写其他模板名");
            return;
        }
        if(selectTemplateNames.isEmpty()){
            AlertUtil.showWarning("请选择模板");
            return;
        }
        GenericMultipleTemplateDefinition genericMultipleTemplateDefinition=new GenericMultipleTemplateDefinition();
        genericMultipleTemplateDefinition.setTemplateNames(selectTemplateNames);
        GenericTemplateContext genericTemplateContext= (GenericTemplateContext) getTemplateContext();
        genericTemplateContext.registerMultipleTemplateDefinition(multipleTemplateName.getText(),genericMultipleTemplateDefinition);
        DefaultListableTemplateFactory defaultListableTemplateFactory = genericTemplateContext.getTemplateFactory();
        defaultListableTemplateFactory.preInstantiateTemplates();
        defaultListableTemplateFactory.refreshMultipleTemplate(genericTemplateContext.getMultipleTemplate(multipleTemplateName.getText()));
        //刷新组合模板ListView页面
        Label label = new Label(multipleTemplateName.getText());
        label.setTextAlignment(TextAlignment.CENTER);
        listViewTemplate.getItems().add(label);
        listViewTemplate.refresh();
        selectTemplateNames.clear();
        AlertUtil.showInfo("Success!");
        ((Stage)anchorPane.getScene().getWindow()).close();
    }
}
