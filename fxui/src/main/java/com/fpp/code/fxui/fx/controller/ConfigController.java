package com.fpp.code.fxui.fx.controller;

import com.fpp.code.core.config.StringPropertySource;
import com.fpp.code.core.context.aware.TemplateContextProvider;
import com.fpp.code.fxui.common.AlertUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * @author Administrator
 */
public class ConfigController extends TemplateContextProvider{
    @FXML
    public AnchorPane anchorPane;
    @FXML
    private TextArea url;
    @FXML
    private TextField userName;
    @FXML
    private TextField password;
    @FXML
    public void save() {
        getTemplateContext().getEnvironment().refreshPropertySourceSerialize(
                new StringPropertySource("code.datasource.url",url.getText()),
                new StringPropertySource("code.datasource.username",userName.getText()),
                new StringPropertySource("code.datasource.password",password.getText()));
        AlertUtil.showInfo("Success!");
        ((Stage)anchorPane.getScene().getWindow()).close();
    }
}
