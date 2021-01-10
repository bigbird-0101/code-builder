package com.fpp.code.fx.controller;

import com.fpp.code.core.domain.DataSourceConfig;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author Administrator
 */
public class ConfigController {
    @FXML
    private TextArea url;
    @FXML
    private TextField userName;
    @FXML
    private TextField password;
    @FXML
    private TextField srcPrefixPackageUrl;
    @FXML
    private TextField srcPackageUrl;
    @FXML
    private TextField resourceUrl;
    @FXML
    private TextArea projectCompleteUrl;


    /**
     * 获取数据源配置
     * @return
     */
    public DataSourceConfig getDataSourceConfig(){
        String newUrl=url.getText();
        String quDongName = newUrl.indexOf("mysql") > 0 ? "com.mysql.jdbc.Driver" : newUrl.indexOf("oracle") > 0 ? "" : "";
        return new DataSourceConfig(quDongName,userName.getText(),newUrl,password.getText());
    }


    /**
     * 获取数据源配置
     * @return
     */
    public DataSourceConfig getFileTemplateConfig(){
        String newUrl=url.getText();
        String quDongName = newUrl.indexOf("mysql") > 0 ? "com.mysql.jdbc.Driver" : newUrl.indexOf("oracle") > 0 ? "" : "";
        return new DataSourceConfig(quDongName,userName.getText(),newUrl,password.getText());
    }

}
