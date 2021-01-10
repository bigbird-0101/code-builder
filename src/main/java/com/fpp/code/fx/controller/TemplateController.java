package com.fpp.code.fx.controller;

import com.fpp.code.common.AlertUtil;
import com.fpp.code.core.domain.TemplateInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Administrator
 */
public class TemplateController implements Initializable {
    @FXML
    public TextArea projectUrl;
    @FXML
    public TextField moduleName;
    @FXML
    public TextField srcPackagePathName;
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
    private Stage primaryStage;

    private ToggleGroup toggleGroup;

    private Boolean isHandleFunction;

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
    }

    @FXML
    public void selectTemplateFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Template files (*.com.fpp.code.core.template)", "*.com.fpp.code.core.template");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        this.file=file;
    }

    @FXML
    public void createTemplate() {
        TemplateInfo templateInfo = getTemplate();
        System.out.println(templateInfo);
    }

    public TemplateInfo getTemplate(){
        AlertUtil.showError("请输入项目地址");
        return new TemplateInfo(projectUrl.getText(),moduleName.getText(),srcPackagePathName.getText(),srcPackageName.getText(),
                templateName.getText(),fileSuffixName.getText(),isHandleFunction,file);
    }
}
