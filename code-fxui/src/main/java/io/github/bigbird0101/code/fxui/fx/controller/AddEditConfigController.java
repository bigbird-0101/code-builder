package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.config.StringPropertySource;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Administrator
 */
public class AddEditConfigController extends AbstractTemplateContextProvider implements Initializable {
    private static final Logger logger= LogManager.getLogger(AddEditConfigController.class);

    @FXML
    VBox main;
    @FXML
    TextField dataSourceName;
    @FXML
    Button button;
    @FXML
    private TextArea url;
    @FXML
    private TextField userName;
    @FXML
    private TextField password;

    private ConfigController configController;

    private boolean isEdit;

    private String editDataSourceName;

    public void setEdit(boolean edit) {
        this.isEdit = edit;
    }

    public void setEditDataSourceName(String editDataSourceName) {
        this.editDataSourceName = editDataSourceName;
    }

    public void setConfigController(ConfigController configController) {
        this.configController = configController;
    }

    @FXML
    public void operate() {
        final String dataSourceNameText = dataSourceName.getText();
        final String urlText = url.getText();
        final String userNameText = userName.getText();
        final String passwordText = password.getText();
        if(StrUtil.isBlank(dataSourceNameText)){
            AlertUtil.showWarning("请填写数据源名");
            return;
        }
        if(StrUtil.isBlank(urlText)){
            AlertUtil.showWarning("请填写数据源url");
            return;
        }
        if(StrUtil.isBlank(userNameText)){
            AlertUtil.showWarning("请填写数据源用户名");
            return;
        }
        if(StrUtil.isBlank(passwordText)){
            AlertUtil.showWarning("请填写数据源密码");
            return;
        }
        final String codeDataSources = Optional.ofNullable(getTemplateContext().getEnvironment().getProperty("code.datasource.names")).orElse("[]");
        final LinkedHashSet<String> codeDataSourcesSet = JSONObject.parseObject(codeDataSources, new TypeReference<LinkedHashSet<String>>() {
        });
        codeDataSourcesSet.add(dataSourceNameText);
        if(isEdit&&!editDataSourceName.equals(dataSourceNameText)){
            codeDataSourcesSet.remove(editDataSourceName);
            //删除老的配置
            getTemplateContext().getEnvironment().removePropertySourceSerialize(new StringPropertySource(StrUtil.format("code.datasource.{}.url",dataSourceNameText), urlText),
                    new StringPropertySource(StrUtil.format("code.datasource.{}.username",editDataSourceName), userNameText),
                    new StringPropertySource(StrUtil.format("code.datasource.{}.password",editDataSourceName), passwordText));
            final String property = getTemplateContext().getEnvironment().getProperty("code.datasource");
            logger.info("property {},editDataSourceName {}",property,editDataSourceName);
            if(property.equals(editDataSourceName)){
                getTemplateContext().getEnvironment().refreshPropertySourceSerialize(
                        new StringPropertySource("code.datasource", dataSourceNameText));
            }
        }
        getTemplateContext().getEnvironment().refreshPropertySourceSerialize(
                new StringPropertySource("code.datasource.names", JSONObject.toJSONString(codeDataSourcesSet)),
                new StringPropertySource(StrUtil.format("code.datasource.{}.url",dataSourceNameText), urlText),
                new StringPropertySource(StrUtil.format("code.datasource.{}.username",dataSourceNameText), userNameText),
                new StringPropertySource(StrUtil.format("code.datasource.{}.password",dataSourceNameText), passwordText));
        AlertUtil.showInfo("Success!");
        configController.initialize(null,null);
        ((Stage) main.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final Environment environment = getTemplateContext().getEnvironment();
        if(isEdit) {
            dataSourceName.setText(editDataSourceName);
            setDataSource(environment, editDataSourceName);
            button.setText("修改");
        }else {
            button.setText("增加");
        }
    }

    private void setDataSource(Environment environment, String defaultName) {
        url.setText(environment.getProperty(StrUtil.format("code.datasource.{}.url", defaultName)));
        userName.setText(environment.getProperty(StrUtil.format("code.datasource.{}.username", defaultName)));
        password.setText(environment.getProperty(StrUtil.format("code.datasource.{}.password", defaultName)));
    }
}
