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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author Administrator
 */
public class MultipleTemplateController extends TemplateContextProvider implements Initializable {
    @FXML
    public FlowPane templates;
    private final Insets insets=new Insets(0,10,10,0);
    private final Set<String> selectTemplateNames=new HashSet<>();
    @FXML
    public TextField multipleTemplateName;
    @FXML
    public AnchorPane anchorPane;
    public TreeView<Label> listViewTemplate;
    @FXML
    public Button button;

    private String sourceMultipleTemplateName;

    /**
     * 0-修改模式 1-添加模式
     */
    private int mode=1;

    public void setListViewTemplate(TreeView<Label> listViewTemplate) {
        this.listViewTemplate = listViewTemplate;
    }

    public void setSourceMultipleTemplateName(String sourceMultipleTemplateName) {
        this.sourceMultipleTemplateName = sourceMultipleTemplateName;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private ComplexController complexController;

    public TextField getMultipleTemplateName() {
        return multipleTemplateName;
    }

    public Button getButton() {
        return button;
    }

    public FlowPane getTemplates() {
        return templates;
    }

    public void setComplexController(ComplexController complexController) {
        this.complexController = complexController;
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
        if(mode==1&&getTemplateContext().getMultipleTemplateNames().contains(multipleTemplateName.getText())){
            AlertUtil.showWarning("该模板名已有，请填写其他模板名");
            return;
        }
        if(selectTemplateNames.isEmpty()){
            AlertUtil.showWarning("请选择模板");
            return;
        }
        GenericTemplateContext genericTemplateContext = (GenericTemplateContext) getTemplateContext();
        DefaultListableTemplateFactory defaultListableTemplateFactory = genericTemplateContext.getTemplateFactory();
        if (mode != 1) {
            defaultListableTemplateFactory.removeMultipleTemplateDefinition(sourceMultipleTemplateName);
            defaultListableTemplateFactory.removeMultipleTemplate(sourceMultipleTemplateName);
            TreeItem<Label> labelTreeItemLabel = listViewTemplate.getRoot().getChildren().stream().filter(labelTreeItem -> labelTreeItem.getValue().getText().equals(sourceMultipleTemplateName)).findFirst().get();
            listViewTemplate.getRoot().getChildren().remove(labelTreeItemLabel);
        }
        buildNewMultipleTemplate(genericTemplateContext);
        //刷新组合模板ListView页面
        complexController.initMultipleTemplateView(multipleTemplateName.getText(),listViewTemplate.getRoot());
        AlertUtil.showInfo("Success!");
        ((Stage)anchorPane.getScene().getWindow()).close();
    }

    /**
     * 创建新的组合模板
     * @param genericTemplateContext 模板容器
     * @throws IOException
     * @throws CodeConfigException
     */
    private void buildNewMultipleTemplate(GenericTemplateContext genericTemplateContext) throws IOException, CodeConfigException {
        DefaultListableTemplateFactory defaultListableTemplateFactory = genericTemplateContext.getTemplateFactory();
        GenericMultipleTemplateDefinition genericMultipleTemplateDefinition = new GenericMultipleTemplateDefinition();
        genericMultipleTemplateDefinition.setTemplateNames(selectTemplateNames);
        genericTemplateContext.registerMultipleTemplateDefinition(multipleTemplateName.getText(), genericMultipleTemplateDefinition);
        defaultListableTemplateFactory.preInstantiateTemplates();
        defaultListableTemplateFactory.refreshMultipleTemplate(genericTemplateContext.getMultipleTemplate(multipleTemplateName.getText()));
    }
}