package com.fpp.code.fx.controller;

import com.fpp.code.common.AlertUtil;
import com.fpp.code.core.config.GeneratePropertySource;
import com.fpp.code.fx.aware.TemplateContextProvider;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author Administrator
 */
public class ConfigController extends TemplateContextProvider{
    @FXML
    private TextArea url;
    @FXML
    private TextField userName;
    @FXML
    private TextField password;

    @FXML
    public void save() {
        getTemplateContext().getEnvironment().refreshPropertySourceSerialize(new GeneratePropertySource<>("code.datasource.url",url.getText()),new GeneratePropertySource<>("code.datasource.username",userName.getText()),new GeneratePropertySource<>("code.datasource.password",password.getText()));
        AlertUtil.showInfo("Success!");
    }
}
