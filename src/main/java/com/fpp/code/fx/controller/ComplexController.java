package com.fpp.code.fx.controller;
import com.fpp.code.Main;
import com.fpp.code.common.AlertUtil;
import com.fpp.code.common.DbUtil;
import com.fpp.code.common.Utils;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.CoreConfig;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.context.TemplateContext;
import com.fpp.code.core.domain.DataSourceConfig;
import com.fpp.code.core.domain.DefinedFunctionDomain;
import com.fpp.code.core.domain.ProjectTemplateInfoConfig;
import com.fpp.code.core.factory.DefaultListableTemplateFactory;
import com.fpp.code.core.filebuilder.*;
import com.fpp.code.core.filebuilder.definedfunction.DefaultDefinedFunctionResolver;
import com.fpp.code.core.template.*;
import com.fpp.code.fx.aware.TemplateContextProvider;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
/**
 * @author fpp
 */
public class ComplexController extends TemplateContextProvider implements Initializable {
    private Logger logger = LogManager.getLogger(getClass());
    @FXML
    public TreeView<Label> listViewTemplate;
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
    private Parent templatesOperateNode;
    private FXMLLoader templatesOperateFxmlLoader;
    private static ExecutorService executorService=Executors.newSingleThreadExecutor();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        splitPane.setDividerPosition(0, 0.15);
        splitPane.setDividerPosition(1, 1.0);
        //宽度绑定为Pane宽度
        listViewTemplate.prefWidthProperty().bind(pane.widthProperty());
        //高度绑定为Pane高度
        listViewTemplate.prefHeightProperty().bind(pane.heightProperty());
        TreeItem<Label> root = new TreeItem<>(new Label("根节点"));
        listViewTemplate.setRoot(root);
        listViewTemplate.setShowRoot(false);
        initMultipleTemplateViews(root);
        listViewTemplate.getSelectionModel().select(0);
        listViewTemplate.requestFocus();
        Main.USER_OPERATE_CACHE.setTemplateNameSelected(listViewTemplate.getSelectionModel().getSelectedItem().getValue().getText());
        doSelectMultiple();
        listViewTemplate.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (null != newValue && !newValue.isLeaf()) {
                Main.USER_OPERATE_CACHE.setTemplateNameSelected(newValue.getValue().getText());
                if (logger.isInfoEnabled()) {
                    logger.info("select template name {}", newValue.getValue().getText());
                }
                doSelectMultiple();
            }
        });
    }

    /**
     * 初始化所有的组合模板视图
     * @param root
     */
    public void initMultipleTemplateViews(TreeItem<Label> root){
        Set<String> multipleTemplateNames = getTemplateContext().getMultipleTemplateNames();
        multipleTemplateNames.forEach(multipleTemplateName -> {
            initMultipleTemplateView(multipleTemplateName, root);
        });
    }

    /**
     * 初始化组合模板视图
     *
     * @param multipleTemplateName 组合模板名
     * @param root                 根树节点
     */
    public void initMultipleTemplateView(String multipleTemplateName, TreeItem<Label> root) {
        Label label = new Label(multipleTemplateName);
        label.prefWidthProperty().bind(listViewTemplate.widthProperty());
        TreeItem<Label> item = new TreeItem<>(label);
        item.setExpanded(true);
        try {
            List<TreeItem<Label>> collect = getTemplateContext().getMultipleTemplate(multipleTemplateName).getTemplates().stream().map(template -> getAndInitTemplateView(template, multipleTemplateName, item)).collect(Collectors.toList());
            item.getChildren().addAll(collect);
        } catch (CodeConfigException | IOException e) {
            e.printStackTrace();
        }
        ContextMenu contextMenu = new ContextMenu();
        MenuItem register = new MenuItem("删除");
        MenuItem edit = new MenuItem("编辑");
        register.setOnAction(event -> {
            if (ButtonType.OK.getButtonData() == AlertUtil.showConfirm("您确定删除该组合模板吗").getButtonData()) {
                String text = listViewTemplate.getSelectionModel().getSelectedItem().getValue().getText();
                DefaultListableTemplateFactory defaultListableTemplateFactory = (DefaultListableTemplateFactory) getTemplateContext().getTemplateFactory();
                try {
                    defaultListableTemplateFactory.removeMultipleTemplate(text);
                    listViewTemplate.getRoot().getChildren().remove(item);
                } catch (CodeConfigException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        edit.setOnAction(event -> {
            try {
                toNewMultipleTemplateView(multipleTemplateName);
            } catch (IOException | CodeConfigException e) {
                e.printStackTrace();
            }
        });
        contextMenu.getItems().addAll(register, edit);
        label.setContextMenu(contextMenu);
        root.getChildren().add(item);
    }
    /**
     * 初始化模板视图
     *
     * @param template
     * @param item
     */
    private TreeItem<Label> getAndInitTemplateView(Template template, String multipleTemplateName, TreeItem<Label> item) {
        Label label = new Label(template.getTemplateName());
        label.prefWidthProperty().bind(listViewTemplate.widthProperty());
        ContextMenu contextMenu = new ContextMenu();
        MenuItem register = new MenuItem("删除");
        MenuItem edit = new MenuItem("编辑");

        TreeItem<Label> labelTreeItem = new TreeItem<>(label);
        register.setOnAction(event -> {
            if (ButtonType.OK.getButtonData() == AlertUtil.showConfirm("您确定删除" + multipleTemplateName + "中的" + template.getTemplateName() + "模板吗").getButtonData()) {
                //删除组合模板中的模板
                TemplateContext templateContext = getTemplateContext();
                DefaultListableTemplateFactory defaultListableTemplateFactory = (DefaultListableTemplateFactory) templateContext.getTemplateFactory();
                try {
                    MultipleTemplate multipleTemplate = defaultListableTemplateFactory.getMultipleTemplate(multipleTemplateName);
                    multipleTemplate.getTemplates().remove(template);
                    defaultListableTemplateFactory.refreshMultipleTemplate(multipleTemplate);
                    doSelectMultiple();
                    item.getChildren().remove(labelTreeItem);
                } catch (CodeConfigException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        edit.setOnAction(event -> {
            try {
                toNewTemplateView(template, multipleTemplateName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        contextMenu.getItems().addAll(register, edit);
        label.setContextMenu(contextMenu);
        return labelTreeItem;
    }
    public void doSelectMultiple() {
        try {
            templatesOperateFxmlLoader = new FXMLLoader(getClass().getResource("/views/templates_operate.fxml"));
            templatesOperateNode = templatesOperateFxmlLoader.load();
            TemplatesOperateController templatesOperateController=templatesOperateFxmlLoader.getController();
            templatesOperateController.getCurrentTemplate().setText("当前组合模板:"+Main.USER_OPERATE_CACHE.getTemplateNameSelected());
            CheckBox checkBox = (CheckBox) templatesOperateNode.lookup("#isAllTable");
            checkBox.selectedProperty().addListener((o, old, newVal) -> {
                isSelectedAllTable = newVal;
            });
            selectedTable = (TextField) templatesOperateNode.lookup("#targetTable");
            content.getChildren().clear();
            content.getChildren().add(templatesOperateNode);
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
        toNewTemplateView(null, null);
    }

    public void toNewTemplateView(Template template, String multipleTemplateName) throws IOException {
        Stage secondWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/new_template.fxml"));
        Parent root = fxmlLoader.load();
        boolean isEdit = null != template;
        TemplateController templateController = fxmlLoader.getController();
        if (isEdit) {
            templateController.setMode(0);
            templateController.setSourceTemplateName(template.getTemplateName());
            templateController.setComplexController(this);
            templateController.button.setText("编辑");
            templateController.templateName.setText(template.getTemplateName());
            if (template instanceof AbstractHandleFunctionTemplate) {
                templateController.trueHandleRadio.setSelected(true);
            } else {
                templateController.falseHandleRadio.setSelected(true);
            }
            templateController.projectUrl.setText(template.getProjectUrl());
            templateController.moduleName.setText(template.getModule());
            templateController.sourcesRootName.setText(template.getSourcesRoot());
            templateController.srcPackageName.setText(template.getSrcPackage());
            templateController.setFile(template.getTemplateFile());
            templateController.fileName.setText(template.getTemplateFile().getName());
            templateController.fileSuffixName.setText(template.getTemplateFileSuffixName());
            TemplateFilePrefixNameStrategy templateFilePrefixNameStrategy = template.getTemplateFilePrefixNameStrategy();
            int typeValue = templateFilePrefixNameStrategy.getTypeValue();
            RadioButton node = (RadioButton) templateController.filePrefixNameStrategy.getChildren().stream().filter(radio -> ((RadioButton) radio).getText().equals(String.valueOf(typeValue))).findFirst().get();
            node.setSelected(true);
            if (typeValue == 3) {
                templateController.filePrefixNameStrategyPane.setVisible(true);
                PatternTemplateFilePrefixNameStrategy patternTemplateFilePrefixNameStrategy = (PatternTemplateFilePrefixNameStrategy) templateFilePrefixNameStrategy;
                templateController.filePrefixNameStrategyPattern.setText(patternTemplateFilePrefixNameStrategy.getPattern());
            }
        }
        Scene scene = new Scene(root);
        secondWindow.setTitle(isEdit ? "编辑模板" : "新建模板");
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
        Platform.runLater(()-> doBuild(new DefaultFileBuilder()));
    }

    @FXML
    public void doBuildCoreAfter() {
        Platform.runLater(()-> {
            FileBuilder fileBuilder = new DefaultFileBuilder();
            FileCodeBuilderStrategy fileCodeBuilderStrategy = new FileAppendSuffixCodeBuilderStrategy();
            fileCodeBuilderStrategy.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
            fileBuilder.setFileCodeBuilderStrategy(fileCodeBuilderStrategy);
            doBuild(fileBuilder);
        });
    }

    public void doBuild(FileBuilder fileBuilder) {
        try {
            TemplatesOperateController templatesOperateController = templatesOperateFxmlLoader.getController();
            if (isSelectedAllTable) {
                initTableAll();
                this.tableSelected = this.tableAll;
            } else {
                if (Utils.isNotEmpty(selectedTable.getText())) {
                    if (!this.tableSelected.contains(selectedTable.getText())) {
                        this.tableSelected.add(selectedTable.getText());
                    }
                }
            }
            if (this.tableSelected.isEmpty() && null == templatesOperateController.getFile()) {
                AlertUtil.showWarning("请输入一个表名或者选择一个变量文件");
                hideProgressBar();
                return;
            }
            Platform.runLater(()->{
                this.showProgressBar();
                templatesOperateController.getProgressBar().progressProperty().bind(service.progressProperty());
                service.restart();
            });
            ProjectTemplateInfoConfig projectTemplateInfoConfig = getProjectTemplateInfoConfig();
            CoreConfig coreConfig = new CoreConfig(getDataSourceConfig(getTemplateContext().getEnvironment()), projectTemplateInfoConfig);
            AbstractFileCodeBuilderStrategy fileCodeBuilderStrategy = (AbstractFileCodeBuilderStrategy) fileBuilder.getFileCodeBuilderStrategy();
            fileCodeBuilderStrategy.setCoreConfig(coreConfig);
            if (logger.isInfoEnabled()) {
                logger.info("选中的模板名 {}", templatesOperateController.getSelectTemplateGroup().keySet());
            }
            AtomicReference<Properties> propertiesVariable = new AtomicReference<>();
            executorService.submit(()->{
                if (null != templatesOperateController.getFile()) {
                    try (InputStreamReader fileInputStream = new InputStreamReader(new FileInputStream(templatesOperateController.getFile()), StandardCharsets.UTF_8)) {
                        propertiesVariable.set(new Properties());
                        propertiesVariable.get().load(fileInputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> templatesOperateController.getProgressBar().setProgress(0.1));
                }
            });

            for (String tableName : tableSelected) {
                for (String templateName : templatesOperateController.getSelectTemplateGroup().get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).keySet()) {
                    Template template=doGetTemplate(templateName,tableName,coreConfig, propertiesVariable.get(),templatesOperateController);
                    fileBuilder.build(template);
                }
            }
            System.out.println(templatesOperateController.getProgressBar().getProgress());
            System.out.println(templatesOperateController.getProgressBar().isManaged());
            System.out.println(templatesOperateController.getProgressBar().isVisible());
            Platform.runLater(this::hideProgressBar);
            AlertUtil.showInfo("生成成功!");
        } catch (Exception e) {
            Platform.runLater(this::hideProgressBar);
            this.tableSelected.clear();
            logger.error("build error {} ,{}", e.getMessage(), e);
            e.printStackTrace();
            AlertUtil.showError(e.getMessage());
        }
    }

    private Template doGetTemplate(String templateName, String tableName, CoreConfig coreConfig, Properties propertiesVariable, TemplatesOperateController templatesOperateController) throws SQLException, ClassNotFoundException, IOException, CodeConfigException {
        Template template = getTemplateContext().getTemplate(templateName);
        Future<Map<String, Object>> tempValue = executorService.submit(() -> {
            Map<String, Object> temp = new HashMap<>(10);
            TableInfo tableInfo = null;
            try {
                tableInfo = DbUtil.getTableInfo(coreConfig.getDataSourceConfig(), tableName);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            tableInfo.setSavePath(template.getSrcPackage().replaceAll("\\/", "."));
            temp.put("tableInfo", tableInfo);
            if (null != propertiesVariable) {
                propertiesVariable.forEach((k, v) -> temp.put((String) k, v));
            }
            return temp;
        });
        try {
            template.setTemplateVariables(tempValue.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Platform.runLater(()->{
            //找到对应的模板checkbox
            CheckBox checkBoxTarget = templatesOperateController.getTemplates().getChildren().stream().map(node -> (VBox) node).map(vBox -> (CheckBox) (((AnchorPane) vBox.getChildren().get(0)).getChildren().get(0))).filter(checkBox -> checkBox.getUserData().equals(template.getTemplateName())).findFirst().get();
            AnchorPane anchorPane = (AnchorPane)checkBoxTarget.getParent();
            //重新设置模板值但不持久化
            templatesOperateController.doSetTemplate(template,anchorPane);
        });
        return template;
    }

    private ProjectTemplateInfoConfig getProjectTemplateInfoConfig() {
        List<DefinedFunctionDomain> definedFunctionDomains = new ArrayList<>();
        Scene scene = templatesOperateNode.getScene();
        CheckBox isDefinedFunction = (CheckBox) scene.lookup("#isDefinedFunction");
        TextField fields = (TextField) scene.lookup("#fields");
        TextField representFactor = (TextField) scene.lookup("#representFactor");
        TemplatesOperateController templatesOperateController = templatesOperateFxmlLoader.getController();
        if (isDefinedFunction.isSelected()) {
            templatesOperateController.getSelectTemplateGroup().get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).forEach((k, v) -> {
                v.forEach(s->definedFunctionDomains.add(new DefinedFunctionDomain(fields.getText(), s, representFactor.getText())));
            });
        }
        return new ProjectTemplateInfoConfig(definedFunctionDomains, templatesOperateController.getSelectTemplateGroup().get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()));
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
    public void addMultipleTemplate() throws IOException, CodeConfigException {
        toNewMultipleTemplateView(null);
    }
    private void toNewMultipleTemplateView(String multipleTemplateName) throws IOException, CodeConfigException {
        Stage secondWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/new_multiple_template.fxml"));
        Parent root = fxmlLoader.load();
        MultipleTemplateController multipleTemplateController = fxmlLoader.getController();
        multipleTemplateController.setListViewTemplate(listViewTemplate);
        multipleTemplateController.setComplexController(this);
        boolean isEdit = null != multipleTemplateName;
        if (isEdit) {
            multipleTemplateController.setMode(0);
            multipleTemplateController.getButton().setText("修改");
            multipleTemplateController.getMultipleTemplateName().setText(multipleTemplateName);
            multipleTemplateController.setSourceMultipleTemplateName(multipleTemplateName);
            MultipleTemplate multipleTemplate = getTemplateContext().getMultipleTemplate(multipleTemplateName);
            Set<String> collect = multipleTemplate.getTemplates().stream().map(Template::getTemplateName).collect(Collectors.toSet());
            multipleTemplateController.getTemplates().getChildren().forEach(checkbox -> {
                CheckBox checkBox = (CheckBox) checkbox;
                if (collect.contains(checkBox.getText())) {
                    checkBox.setSelected(true);
                }
            });
        }
        Scene scene = new Scene(root);
        secondWindow.setTitle(isEdit ? "编辑组合模板" : "新建组合模板");
        secondWindow.setScene(scene);
        secondWindow.show();
    }

    void showProgressBar(){
        TemplatesOperateController templatesOperateController = templatesOperateFxmlLoader.getController();
        BorderPane progressBarParent = templatesOperateController.getProgressBarParent();
        progressBarParent.setVisible(true);
        progressBarParent.setManaged(true);
        ProgressBar progressBar = templatesOperateController.getProgressBar();
        progressBar.setVisible(true);
        progressBar.setManaged(true);
    }

    void hideProgressBar(){
        TemplatesOperateController templatesOperateController = templatesOperateFxmlLoader.getController();
        BorderPane progressBarParent = templatesOperateController.getProgressBarParent();
        progressBarParent.setVisible(false);
        progressBarParent.setManaged(false);
        ProgressBar progressBar = templatesOperateController.getProgressBar();
        progressBar.setVisible(false);
        progressBar.setManaged(false);
    }


    Service<Integer> service = new Service<Integer>() {

        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    int i = 0;
                    while (i++ < 100) {
                        updateProgress(i, 100);
                    }
                    return null;
                };
            };
        }
    };
}
