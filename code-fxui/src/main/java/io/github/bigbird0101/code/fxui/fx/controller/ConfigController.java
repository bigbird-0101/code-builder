package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.github.bigbird0101.code.core.config.Environment;
import io.github.bigbird0101.code.core.config.StringPropertySource;
import io.github.bigbird0101.code.core.context.aware.TemplateContextProvider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Administrator
 */
public class ConfigController extends TemplateContextProvider implements Initializable {
    private static final Logger logger= LogManager.getLogger(ConfigController.class);

    @FXML
    AnchorPane anchorPane;
    @FXML
    TextField dataSourceName;
    @FXML
    ChoiceBox<String> dataSourceNames;
    @FXML
    private TextArea url;
    @FXML
    private TextField userName;
    @FXML
    private TextField password;

    @FXML
    public void add(ActionEvent actionEvent) throws IOException {
        Stage secondWindow = new Stage();
        final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/views/data_source_config_info.fxml"));
        Parent root = fxmlLoader.load();
        final AddEditConfigController controller = fxmlLoader.getController();
        controller.setEdit(false);
        controller.setConfigController(this);
        Scene scene = new Scene(root);
        secondWindow.setTitle("添加配置");
        secondWindow.setScene(scene);
        secondWindow.show();
    }

    @FXML
    public void edit(ActionEvent actionEvent) throws IOException {
        Stage secondWindow = new Stage();
        final FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/views/data_source_config_info.fxml"));
        Parent root = fxmlLoader.load();
        final AddEditConfigController controller = fxmlLoader.getController();
        controller.setEdit(true);
        controller.setEditDataSourceName(dataSourceNames.getSelectionModel().getSelectedItem());
        controller.setConfigController(this);
        controller.initialize(null,null);
        Scene scene = new Scene(root);
        secondWindow.setTitle("修改配置");
        secondWindow.setScene(scene);
        secondWindow.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final Environment environment = getTemplateContext().getEnvironment();
        final String dataSourceNamesConfig = Optional.ofNullable(environment.getProperty("code.datasource.names")).orElse("[]");
        final LinkedHashSet<String> dataSourceNamesSet = JSONObject.parseObject(dataSourceNamesConfig, new TypeReference<LinkedHashSet<String>>() {
        });
        dataSourceNames.getItems().clear();
        dataSourceNames.getItems().addAll(dataSourceNamesSet);
        final String defaultName = Optional.ofNullable(environment.getProperty("code.datasource")).orElse("default");
        logger.info("defaultName {}",defaultName);
        dataSourceNames.getSelectionModel().select(defaultName);
        dataSourceNames.getSelectionModel().selectedItemProperty().addListener((b,o,n)-> {
            logger.info("old {},new {}",o,n);
            if(o!=null&&null!=n&&!o.equals(n)) {
                getTemplateContext().getEnvironment().refreshPropertySourceSerialize(new StringPropertySource("code.datasource", n));
                setDataSource(environment, n);
            }
        });
        setDataSource(environment, defaultName);
    }

    private void setDataSource(Environment environment, String defaultName) {
        url.setText(environment.getProperty(StrUtil.format("code.datasource.{}.url", defaultName)));
        userName.setText(environment.getProperty(StrUtil.format("code.datasource.{}.username", defaultName)));
        password.setText(environment.getProperty(StrUtil.format("code.datasource.{}.password", defaultName)));
    }
}
