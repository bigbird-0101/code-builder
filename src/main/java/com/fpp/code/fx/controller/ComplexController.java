package com.fpp.code.fx.controller;

import com.fpp.code.Main;
import com.fpp.code.common.AlertUtil;
import com.fpp.code.common.DbUtil;
import com.fpp.code.common.Utils;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.CoreConfig;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.domain.DataSourceConfig;
import com.fpp.code.core.domain.DefinedFunctionDomain;
import com.fpp.code.core.domain.ProjectTemplateInfoConfig;
import com.fpp.code.core.factory.DefaultListableTemplateFactory;
import com.fpp.code.core.filebuilder.DefaultFileBuilder;
import com.fpp.code.core.filebuilder.FileAppendSuffixCodeBuilderStrategy;
import com.fpp.code.core.filebuilder.FileBuilder;
import com.fpp.code.core.filebuilder.FileCodeBuilderStrategy;
import com.fpp.code.core.filebuilder.definedfunction.DefaultDefinedFunctionResolver;
import com.fpp.code.core.template.TemplateFilePrefixNameStrategy;
import com.fpp.code.fx.aware.TemplateContextProvider;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * @author fpp
 */
public class ComplexController extends TemplateContextProvider implements Initializable {
    private Logger logger = LogManager.getLogger(getClass());

    @FXML
    public ListView<Label> listViewTemplate;

    @FXML
    private Pane pane;
    @FXML
    private VBox content;
    @FXML
    private SplitPane splitPane;

    /**
     * 所有的表格
     */
    private List<String> tableAll = new ArrayList<>(10);
    /**
     * 选中的表格
     */
    private List<String> tableSelected = new ArrayList<>(10);
    private static boolean isSelectedAllTable = false;
    private TextField selectedTable;
    private final Insets insets = new Insets(0, 10, 10, 0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        splitPane.setDividerPosition(0, 0.15);
        splitPane.setDividerPosition(1, 1.0);
        //宽度绑定为Pane宽度
        listViewTemplate.prefWidthProperty().bind(pane.widthProperty());
        //高度绑定为Pane高度
        listViewTemplate.prefHeightProperty().bind(pane.heightProperty());
        ObservableList<Label> apiList = listViewTemplate.getItems();
        Set<String> multipleTemplateNames = getTemplateContext().getMultipleTemplateNames();
        multipleTemplateNames.forEach(multipleTemplateName -> {
            Label label = new Label(multipleTemplateName);
            label.setTextAlignment(TextAlignment.CENTER);
            apiList.add(label);
        });
        listViewTemplate.setItems(apiList);
        listViewTemplate.getSelectionModel().select(0);
        listViewTemplate.requestFocus();
        Main.userOperateCache.setTemplateNameSelected(listViewTemplate.getSelectionModel().getSelectedItem().getText());
        doSelectMultiple();
        listViewTemplate.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Main.userOperateCache.setTemplateNameSelected((newValue.getText()));
            if (logger.isInfoEnabled()) {
                logger.info("select template name {}", newValue.getText());
            }
            doSelectMultiple();
        });
        ContextMenu contextMenu = new ContextMenu();
        MenuItem register = new MenuItem("删除");
        register.setOnAction(event ->{
            if(ButtonType.OK.getButtonData()==AlertUtil.showConfirm("您确定删除该组合模板吗").getButtonData()){
                DefaultListableTemplateFactory defaultListableTemplateFactory= (DefaultListableTemplateFactory) getTemplateContext().getTemplateFactory();
                try {
                    defaultListableTemplateFactory.removeMultipleTemplate(listViewTemplate.getSelectionModel().getSelectedItem().getText());
                    listViewTemplate.getItems().remove(listViewTemplate.getSelectionModel().getSelectedIndex());
                } catch (CodeConfigException e) {
                    e.printStackTrace();
                }
            }
        });
        contextMenu.getItems().add(register);
        listViewTemplate.setContextMenu(contextMenu);
    }

    private void doSelectMultiple() {
        try {
            Parent root = new FXMLLoader(getClass().getResource("/views/templates_operate.fxml")).load();
            CheckBox checkBox = (CheckBox) root.lookup("#isAllTable");
            checkBox.selectedProperty().addListener((o, old, newVal) -> {
                isSelectedAllTable = newVal;
            });
            selectedTable = (TextField) root.lookup("#targetTable");
            content.getChildren().clear();
            content.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化所有表格名
     */
    private void initTableAll() throws SQLException, ClassNotFoundException {
        this.tableAll = DbUtil.getAllTableName(getDataSourceConfig(getTemplateContext().getEnvironment()));
    }

    @FXML
    public void addTemplate() throws IOException {
        Stage secondWindow = new Stage();
        Parent root = new FXMLLoader(getClass().getResource("/views/new_template.fxml")).load();
        Scene scene = new Scene(root);
        secondWindow.setTitle("新建模板");
        FlowPane flowPane = (FlowPane) scene.lookup("#filePrefixNameStrategy");
        ServiceLoader<TemplateFilePrefixNameStrategy> load = ServiceLoader.load(TemplateFilePrefixNameStrategy.class);
        for (TemplateFilePrefixNameStrategy next : load) {
            RadioButton radioButton = new RadioButton(String.valueOf(next.getTypeValue()));
            radioButton.setPadding(insets);
            flowPane.getChildren().add(radioButton);
        }
        secondWindow.setScene(scene);
        secondWindow.show();
    }

    @FXML
    public void coreConfig() throws IOException {
        Stage secondWindow = new Stage();
        Parent root = new FXMLLoader(this.getClass().getResource("/views/config.fxml")).load();
        Scene scene = new Scene(root);
        TextArea url = (TextArea) scene.lookup("#url");
        TextField userName = (TextField) scene.lookup("#userName");
        TextField password = (TextField) scene.lookup("#password");
        Environment environment = getTemplateContext().getEnvironment();
        url.setText(environment.getProperty("code.datasource.url"));
        userName.setText(environment.getProperty("code.datasource.username"));
        password.setText(environment.getProperty("code.datasource.password"));
        secondWindow.setTitle("核心配置");
        secondWindow.setScene(scene);
        secondWindow.show();
    }

    @FXML
    public void doBuildCore() {
        doBuild(new DefaultFileBuilder());
    }

    @FXML
    public void doBuildCoreAfter() {
        FileBuilder fileBuilder = new DefaultFileBuilder();
        FileCodeBuilderStrategy fileCodeBuilderStrategy = new FileAppendSuffixCodeBuilderStrategy();
        fileCodeBuilderStrategy.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
        fileBuilder.setFileCodeBuilderStrategy(fileCodeBuilderStrategy);
        doBuild(fileBuilder);
    }

    public void doBuild(FileBuilder fileBuilder) {
        try {
            if (isSelectedAllTable) {
                initTableAll();
                this.tableSelected = this.tableAll;
            } else {
                if (Utils.isEmpty(selectedTable.getText())) {
                    AlertUtil.showWarning("请输入目标表名");
                    return;
                }
                if (!this.tableSelected.contains(selectedTable.getText())) {
                    this.tableSelected.add(selectedTable.getText());
                }
            }
            ProjectTemplateInfoConfig projectTemplateInfoConfig = getProjectTemplateInfoConfig();
            CoreConfig coreConfig = new CoreConfig(getDataSourceConfig(getTemplateContext().getEnvironment()), projectTemplateInfoConfig);
            for (String tableName : tableSelected) {
                for (String templateName : TemplatesOperateController.selectTemplateGroup.keySet()) {
                    fileBuilder.build(coreConfig, tableName, getTemplateContext().getTemplate(templateName));
                }
            }
            AlertUtil.showInfo("生成成功!");
        } catch (Exception e) {
            logger.error("build error {} ,{}", e.getMessage(), e);
            e.printStackTrace();
            AlertUtil.showError(e.getMessage());
        }
    }

    private ProjectTemplateInfoConfig getProjectTemplateInfoConfig() {
        List<DefinedFunctionDomain> definedFunctionDomains = new ArrayList<>();
        Parent root;
        try {
            root = new FXMLLoader(this.getClass().getResource("/views/templates_operate.fxml")).load();
            Scene scene = new Scene(root);
            CheckBox isDefinedFunction = (CheckBox) scene.lookup("#isDefinedFunction");
            TextField fields = (TextField) scene.lookup("#fields");
            TextField representFactor = (TextField) scene.lookup("#representFactor");
            if (isDefinedFunction.isSelected()) {
                TemplatesOperateController.selectTemplateGroup.forEach((k, v) -> {
                    v.forEach(s -> {
                        definedFunctionDomains.add(new DefinedFunctionDomain(fields.getText(), s, representFactor.getText()));
                    });
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ProjectTemplateInfoConfig(definedFunctionDomains, TemplatesOperateController.selectTemplateGroup);
    }

    /**
     * 获取数据源配置
     *
     * @return
     */
    public DataSourceConfig getDataSourceConfig(Environment environment) {
        String url = environment.getProperty("code.datasource.url");
        String quDongName = url.indexOf("mysql") > 0 ? "com.mysql.jdbc.Driver" : url.indexOf("oracle") > 0 ? "" : "";
        return new DataSourceConfig(quDongName, environment.getProperty("code.datasource.username"), url, environment.getProperty("code.datasource.password"));
    }

    @FXML
    public void about() throws IOException {
        Stage secondWindow = new Stage();
        Parent root = new FXMLLoader(this.getClass().getClassLoader().getResource("about.fxml")).load();
        secondWindow.setTitle("关于");
        secondWindow.setScene(new Scene(root));
        secondWindow.show();
    }

    @FXML
    public void addMultipleTemplate() throws IOException {
        Stage secondWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/new_multiple_template.fxml"));
        Parent root = fxmlLoader.load();
        MultipleTemplateController multipleTemplateController = fxmlLoader.getController();
        multipleTemplateController.setListViewTemplate(listViewTemplate);
        Scene scene = new Scene(root);
        secondWindow.setTitle("新建组合模板");
        secondWindow.setScene(scene);
        secondWindow.show();
    }
}