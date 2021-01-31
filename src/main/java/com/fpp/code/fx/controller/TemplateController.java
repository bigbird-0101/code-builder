package com.fpp.code.fx.controller;

import com.fpp.code.common.AlertUtil;
import com.fpp.code.common.Utils;
import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.core.factory.DefaultListableTemplateFactory;
import com.fpp.code.core.factory.GenericTemplateDefinition;
import com.fpp.code.fx.aware.TemplateContextProvider;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * @author Administrator
 */
public class TemplateController extends TemplateContextProvider implements Initializable {
    @FXML
    public TextArea projectUrl;
    @FXML
    public TextField moduleName;
    @FXML
    public TextField srcPackageName;
    @FXML
    public TextField templateName;
    @FXML
    public TextField fileSuffixName;
    @FXML
    public RadioButton trueHandleRadio;
    @FXML
    public RadioButton falseHandleRadio;
    @FXML
    public FlowPane filePrefixNameStrategy;
    @FXML
    public TextField sourcesRootName;
    @FXML
    public Label fileName;
    @FXML
    public VBox vBox;
    @FXML
    private Stage primaryStage;

    private ToggleGroup toggleGroup;

    private ToggleGroup filePrefixNameStrategyToggleGroup;

    private Boolean isHandleFunction;

    private Integer filePrefixNameStrategyValue;

    private File file;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toggleGroup=new ToggleGroup();
        trueHandleRadio.setToggleGroup(toggleGroup);
        falseHandleRadio.setToggleGroup(toggleGroup);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton radioButton= (RadioButton) newValue;
            this.isHandleFunction=radioButton==trueHandleRadio;
        });
        filePrefixNameStrategyToggleGroup=new ToggleGroup();
        filePrefixNameStrategy.getChildren().forEach(node -> {
            RadioButton radioButton= (RadioButton) node;
            radioButton.setToggleGroup(filePrefixNameStrategyToggleGroup);
        });
        filePrefixNameStrategyToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton radioButton= (RadioButton) newValue;
            this.filePrefixNameStrategyValue=Integer.valueOf(radioButton.getText());
        });
    }

    @FXML
    public void selectTemplateFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Template files (*.template)", "*.template");
        fileChooser.getExtensionFilters().add(extFilter);
        this.file= fileChooser.showOpenDialog(vBox.getScene().getWindow());
        fileName.setText(this.file.getName());
    }

    @FXML
    public void createTemplate() {
        if(!check()){
            return;
        }
        try {
            GenericTemplateContext templateContext = (GenericTemplateContext) getTemplateContext();
            GenericTemplateDefinition genericTemplateDefinition = new GenericTemplateDefinition();
            genericTemplateDefinition.setFilePrefixNameStrategy(Utils.setIfNull(filePrefixNameStrategyValue, 1));
            genericTemplateDefinition.setFileSuffixName(fileSuffixName.getText());
            genericTemplateDefinition.setHandleFunction(Utils.setIfNull(isHandleFunction, true));
            genericTemplateDefinition.setModule(moduleName.getText());
            genericTemplateDefinition.setProjectUrl(projectUrl.getText());
            genericTemplateDefinition.setSourcesRoot(sourcesRootName.getText());
            genericTemplateDefinition.setSrcPackage(srcPackageName.getText());
            String newFileName = getTemplateContext().getEnvironment().getProperty(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH) + "/" + file.getName();
            File newFile = new File(newFileName);
            FileUtils.copyFile(this.file,newFile);
            genericTemplateDefinition.setTemplateFile(newFile);
            AbstractEnvironment.putTemplateContent(newFile.getAbsolutePath(), IOUtils.toString(new FileInputStream(newFile), StandardCharsets.UTF_8));
            templateContext.registerTemplateDefinition(templateName.getText(), genericTemplateDefinition);
            DefaultListableTemplateFactory defaultListableTemplateFactory = templateContext.getTemplateFactory();
            defaultListableTemplateFactory.preInstantiateTemplates();
            defaultListableTemplateFactory.refreshTemplate(templateContext.getTemplate(templateName.getText()));
            AlertUtil.showInfo("Success!");
        }catch (Exception e){
            AlertUtil.showError("failed:"+e.getMessage());
        }
    }



    public boolean check(){
        if(Utils.isEmpty(templateName.getText())){
            AlertUtil.showError("请输入模板名");
            return false;
        }
        if(null==isHandleFunction){
            AlertUtil.showError("请选择是否控制方法");
            return false;
        }
        if(null==file){
            AlertUtil.showError("请输入模板文件");
            return false;
        }
        if(getTemplateContext().getTemplateNames().contains(templateName.getText())){
            AlertUtil.showError("该模板名已存在,请填写其他模板名!");
            return false;
        }
        return true;
    }

}
