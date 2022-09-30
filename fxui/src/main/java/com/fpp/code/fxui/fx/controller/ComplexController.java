package com.fpp.code.fxui.fx.controller;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.hutool.system.UserInfo;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fpp.code.core.common.CollectionUtils;
import com.fpp.code.core.common.DbUtil;
import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.config.CoreConfig;
import com.fpp.code.core.context.AbstractTemplateContext;
import com.fpp.code.core.context.TemplateContext;
import com.fpp.code.core.context.aware.TemplateContextProvider;
import com.fpp.code.core.domain.DataSourceConfig;
import com.fpp.code.core.domain.DefinedFunctionDomain;
import com.fpp.code.core.domain.ProjectTemplateInfoConfig;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.factory.DefaultListableTemplateFactory;
import com.fpp.code.core.factory.GenericMultipleTemplateDefinition;
import com.fpp.code.core.factory.RootTemplateDefinition;
import com.fpp.code.core.factory.config.TemplateDefinition;
import com.fpp.code.core.filebuilder.*;
import com.fpp.code.core.filebuilder.definedfunction.DefaultDefinedFunctionResolver;
import com.fpp.code.core.template.HaveDependTemplate;
import com.fpp.code.core.template.MultipleTemplate;
import com.fpp.code.core.template.Template;
import com.fpp.code.core.template.targetfile.PatternTargetFilePrefixNameStrategy;
import com.fpp.code.core.template.targetfile.TargetFilePrefixNameStrategy;
import com.fpp.code.core.template.variable.resource.ConfigFileTemplateVariableResource;
import com.fpp.code.core.template.variable.resource.DataSourceTemplateVariableResource;
import com.fpp.code.core.template.variable.resource.TemplateVariableResource;
import com.fpp.code.fxui.common.AlertUtil;
import com.fpp.code.fxui.event.DoGetTemplateAfterEvent;
import com.fpp.code.fxui.fx.bean.PageInputSnapshot;
import com.fpp.code.fxui.fx.component.FxAlerts;
import com.fpp.code.fxui.fx.component.FxProgressDialog;
import com.fpp.code.fxui.fx.component.ProgressTask;
import com.fpp.code.util.Utils;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.fpp.code.core.template.variable.resource.TemplateVariableResource.DEFAULT_SRC_RESOURCE_KEY;
import static com.fpp.code.core.template.variable.resource.TemplateVariableResource.DEFAULT_SRC_RESOURCE_VALUE;
import static com.fpp.code.fxui.CodeBuilderApplication.USER_OPERATE_CACHE;
import static java.util.stream.Collectors.toList;

/**
 * @author fpp
 */
public class ComplexController extends TemplateContextProvider implements Initializable {
    @FXML
    VBox mainBox;
    @FXML
    StackPane contentParent;
    @FXML
    Menu showLog;
    private Logger logger = LogManager.getLogger(getClass());
    @FXML
    TreeView<Label> listViewTemplate;
    @FXML
    private Pane pane;
    @FXML
    private AnchorPane content;
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
    private VBox templatesOperateNode;
    private FXMLLoader templatesOperateFxmlLoader;
    final ThreadPoolExecutor DO_ANALYSIS_TEMPLATE = ExecutorBuilder.create()
            .setCorePoolSize(5)
            .setThreadFactory(ThreadFactoryBuilder.create().setNamePrefix("DO_ANALYSIS_TEMPLATE").build())
            .build();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        content.prefWidthProperty().bind(contentParent.widthProperty());
        splitPane.setDividerPosition(0, 0.15);
        splitPane.setDividerPosition(1, 1.0);
        //宽度绑定为Pane宽度
        listViewTemplate.prefWidthProperty().bind(pane.widthProperty());
        //高度绑定为Pane高度
        listViewTemplate.prefHeightProperty().bind(splitPane.heightProperty());
        TreeItem<Label> root = new TreeItem<>(new Label("根节点"));
        listViewTemplate.setRoot(root);
        listViewTemplate.setShowRoot(false);
        initMultipleTemplateViews(root);
        final String defaultMultipleTemplate = getDefaultMultipleTemplate();
        if(StrUtil.isNotBlank(defaultMultipleTemplate)){
            Optional.ofNullable(root.getChildren()
                    .filtered(s -> s.getValue().getText().equals(defaultMultipleTemplate)))
                    .ifPresent(s->{
                        final TreeItem<Label> labelTreeItem = s.get(0);
                        listViewTemplate.getSelectionModel().select(labelTreeItem);
                    });
        }else{
            listViewTemplate.getSelectionModel().select(0);
        }
        listViewTemplate.requestFocus();
        USER_OPERATE_CACHE.setTemplateNameSelected(listViewTemplate.getSelectionModel().getSelectedItem().getValue().getText());
        doSelectMultiple();
        listViewTemplate.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (null != newValue && !newValue.isLeaf()) {
                USER_OPERATE_CACHE.setTemplateNameSelected(newValue.getValue().getText());
                if (logger.isInfoEnabled()) {
                    logger.info("select template name {}", newValue.getValue().getText());
                }
                doSelectMultiple();
            }
        });
        init();
    }

    private void init(){
        initView();
        initData();
    }

    private void initData() {
        initTableAll();
    }

    private void initView() {
        initLogView();
    }

    private void initLogView() {
        Platform.runLater(()->{
            final String property = new UserInfo().getCurrentDir();
            logger.info("property {}",property);
            if(StrUtil.isNotBlank(property)){
                final String logPath = property + File.separator + "log";
                final File file = new File(logPath);
                logger.info("logPath {}",logPath);
                if(file.exists()){
                    FileFilterUtils.filterList(FileFileFilter.FILE, file.listFiles())
                            .stream().map(File::getName)
                            .map(MenuItem::new)
                            .forEach(s->{
                                logger.info("MenuItem {}",s.getText());
                                s.setOnAction(event -> {
                                    Desktop desktop = Desktop.getDesktop();
                                    File logFile=new File(logPath+File.separator+s.getText());
                                    if(logFile.exists()) {
                                        try {
                                            desktop.open(logFile);
                                        } catch (IOException ignored) {
                                        }
                                    }
                                });
                                showLog.getItems().add(s);
                            });
                }
            }
        });
    }

    public String getDefaultMultipleTemplate(){
        String property = getTemplateContext().getEnvironment().getProperty(AbstractEnvironment.DEFAULT_USER_SAVE_TEMPLATE_CONFIG);
        if (Utils.isNotEmpty(property)) {
            return JSONObject.parseObject(property, new TypeReference<PageInputSnapshot>() {}).getCurrentMultipleTemplate();
        }
        return null;
    }

    /**
     * 初始化所有的组合模板视图
     * @param root
     */
    public void initMultipleTemplateViews(TreeItem<Label> root){
        Set<String> multipleTemplateNames = getTemplateContext().getMultipleTemplateNames();
        for (String multipleTemplateName : multipleTemplateNames) {
            final TreeItem<Label> labelTreeItem = initMultipleTemplateView(multipleTemplateName, root);
            root.getChildren().add(labelTreeItem);
        }
    }

    /**
     * 初始化组合模板视图
     *
     * @param multipleTemplateName 组合模板名
     * @param root                 根树节点
     */
    public TreeItem<Label> initMultipleTemplateView(String multipleTemplateName, TreeItem<Label> root) {
        Label label = new Label(multipleTemplateName);
        label.prefWidthProperty().bind(listViewTemplate.widthProperty());
        TreeItem<Label> item = new TreeItem<>(label);
        item.setExpanded(true);
        TemplateContext templateContext = getTemplateContext();
        DefaultListableTemplateFactory defaultListableTemplateFactory = (DefaultListableTemplateFactory) templateContext.getTemplateFactory();
        try {
            List<TreeItem<Label>> collect = templateContext.getMultipleTemplate(multipleTemplateName).getTemplates().stream().map(template -> getAndInitTemplateView(template, multipleTemplateName, item)).collect(toList());
            item.getChildren().addAll(item.getChildren().size(),collect);
        } catch (CodeConfigException e) {
            e.printStackTrace();
        }
        ContextMenu contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("删除");
        MenuItem edit = new MenuItem("编辑");
        MenuItem copy = new MenuItem("复制");
        MenuItem deepCopy = new MenuItem("递归复制");

        delete.setOnAction(event -> {
            if (ButtonType.OK.getButtonData() == AlertUtil.showConfirm("您确定删除该组合模板吗").getButtonData()) {
                String text = listViewTemplate.getSelectionModel().getSelectedItem().getValue().getText();
                logger.info("delete multiple template {}",text);
                defaultListableTemplateFactory.removeMultipleTemplateDefinition(text);
                defaultListableTemplateFactory.removeMultipleTemplate(text);
                listViewTemplate.getRoot().getChildren().remove(listViewTemplate.getSelectionModel().getSelectedItem());
                USER_OPERATE_CACHE.setTemplateNameSelected(defaultListableTemplateFactory.getMultipleTemplateNames().stream().findFirst().orElse(""));
                doSelectMultiple();
            }
        });
        edit.setOnAction(event -> {
            String text = listViewTemplate.getSelectionModel().getSelectedItem().getValue().getText();
            try {
                toNewMultipleTemplateView(text);
            } catch (IOException | CodeConfigException e) {
                e.printStackTrace();
            }
        });
        copy.setOnAction(event -> {
            copyMultipleTemplate(root, defaultListableTemplateFactory, contextMenu);
            AlertUtil.showInfo("复制成功");
        });
        deepCopy.setOnAction(event -> {
            deepCopyMultipleTemplate(root, defaultListableTemplateFactory, contextMenu);
            AlertUtil.showInfo("复制成功");
        });
        contextMenu.getItems().addAll(delete, edit,copy,deepCopy);
        label.setContextMenu(contextMenu);
        return item;
    }

    private void copyMultipleTemplate(TreeItem<Label> root, DefaultListableTemplateFactory defaultListableTemplateFactory, ContextMenu contextMenu) {
        final GenericMultipleTemplateDefinition clone = getGenericMultipleTemplateDefinition(defaultListableTemplateFactory);
        doCopyMultipleTemplate(root, defaultListableTemplateFactory, contextMenu, clone);
    }

    private void deepCopyMultipleTemplate(TreeItem<Label> root, DefaultListableTemplateFactory defaultListableTemplateFactory, ContextMenu contextMenu) {
        final GenericMultipleTemplateDefinition clone = getGenericMultipleTemplateDefinition(defaultListableTemplateFactory);
        Set<String> oldTemplateNames = clone.getTemplateNames();
        Set<String> newTemplateNames=new LinkedHashSet<>();
        for(String templateName:oldTemplateNames){
            Template newTemplate = copyTemplate(defaultListableTemplateFactory.getTemplate(templateName), defaultListableTemplateFactory);
            newTemplateNames.add(newTemplate.getTemplateName());
        }
        oldTemplateNames.clear();
        clone.setTemplateNames(newTemplateNames);
        doCopyMultipleTemplate(root, defaultListableTemplateFactory, contextMenu, clone);
    }

    private GenericMultipleTemplateDefinition getGenericMultipleTemplateDefinition(DefaultListableTemplateFactory defaultListableTemplateFactory) {
        String text = listViewTemplate.getSelectionModel().getSelectedItem().getValue().getText();
        logger.info("multipleTemplateName {}", text);
        final GenericMultipleTemplateDefinition multipleTemplateDefinition = (GenericMultipleTemplateDefinition) defaultListableTemplateFactory.getMultipleTemplateDefinition(text);
        final GenericMultipleTemplateDefinition clone = (GenericMultipleTemplateDefinition) multipleTemplateDefinition.clone();
        final String copyMultipleTemplateName = text + "Copy";
        if (null != defaultListableTemplateFactory.getMultipleTemplateDefinition(copyMultipleTemplateName)) {
            defaultListableTemplateFactory.removeMultipleTemplateDefinition(copyMultipleTemplateName);
            defaultListableTemplateFactory.removeMultipleTemplate(copyMultipleTemplateName);
        }
        return clone;
    }

    private void doCopyMultipleTemplate(TreeItem<Label> root, DefaultListableTemplateFactory defaultListableTemplateFactory, ContextMenu contextMenu, GenericMultipleTemplateDefinition clone) {
        String text = listViewTemplate.getSelectionModel().getSelectedItem().getValue().getText();
        final String copyMultipleTemplateName = text + "Copy";
        defaultListableTemplateFactory.registerMultipleTemplateDefinition(copyMultipleTemplateName,clone);
        defaultListableTemplateFactory.preInstantiateTemplates();
        defaultListableTemplateFactory.refreshMultipleTemplate(defaultListableTemplateFactory.getMultipleTemplate(copyMultipleTemplateName));
        Label copyLabel = new Label(copyMultipleTemplateName);
        copyLabel.prefWidthProperty().bind(listViewTemplate.widthProperty());
        TreeItem<Label> copyItem = new TreeItem<>(copyLabel);
        copyItem.setExpanded(true);
        List<TreeItem<Label>> collect = defaultListableTemplateFactory.getMultipleTemplate(copyMultipleTemplateName).getTemplates().stream().map(template -> getAndInitTemplateView(template, copyMultipleTemplateName, copyItem)).collect(toList());
        copyItem.getChildren().addAll(copyItem.getChildren().size(), collect);
        final FilteredList<TreeItem<Label>> filtered = root.getChildren().filtered(s -> s.getValue().getText().equals(copyMultipleTemplateName));
        final int size = filtered.size();
        if(0==size) {
            root.getChildren().add(root.getChildren().size(), copyItem);
        }else if(1==size){
            root.getChildren().remove(filtered.get(0));
            root.getChildren().add(root.getChildren().size(), copyItem);
        }
        copyLabel.setContextMenu(contextMenu);
    }

    /**
     * 初始化模板视图
     *
     * @param template
     * @param item
     */
    public TreeItem<Label> getAndInitTemplateView(Template template, String multipleTemplateName, TreeItem<Label> item) {
        Label label = new Label(template.getTemplateName());
        label.prefWidthProperty().bind(listViewTemplate.widthProperty());
        ContextMenu contextMenu = new ContextMenu();
        MenuItem register = new MenuItem("删除");
        MenuItem edit = new MenuItem("编辑");
        MenuItem copy = new MenuItem("复制");
        TemplateContext templateContext = getTemplateContext();
        DefaultListableTemplateFactory defaultListableTemplateFactory = (DefaultListableTemplateFactory) templateContext.getTemplateFactory();
        TreeItem<Label> labelTreeItem = new TreeItem<>(label);
        register.setOnAction(event -> {
            if (ButtonType.OK.getButtonData() == AlertUtil.showConfirm("您确定删除" + multipleTemplateName + "中的" + template.getTemplateName() + "模板吗").getButtonData()) {
                //删除组合模板中的模板
                MultipleTemplate multipleTemplate = defaultListableTemplateFactory.getMultipleTemplate(multipleTemplateName);
                multipleTemplate.getTemplates().remove(template);
                defaultListableTemplateFactory.refreshMultipleTemplate(multipleTemplate);
                item.getChildren().remove(labelTreeItem);
            }
        });
        edit.setOnAction(event -> {
            try {
                toNewTemplateView(template);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        copy.setOnAction(event -> {
            copyTemplate(template, defaultListableTemplateFactory);
            AlertUtil.showInfo("复制成功");
        });

        contextMenu.getItems().addAll(register, edit,copy);
        label.setContextMenu(contextMenu);
        return labelTreeItem;
    }

    private Template copyTemplate(Template template, DefaultListableTemplateFactory defaultListableTemplateFactory) {
        final RootTemplateDefinition templateDefinition = (RootTemplateDefinition)defaultListableTemplateFactory.getTemplateDefinition(template.getTemplateName());
        TemplateDefinition clone = (TemplateDefinition) templateDefinition.clone();
        final String copyTemplateName = template.getTemplateName() + "Copy";
        final File templateFile = clone.getTemplateFile();
        try {
            String newFileName = getTemplateContext().getEnvironment().getProperty(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH) + File.separator + copyTemplateName+AbstractEnvironment.DEFAULT_TEMPLATE_FILE_SUFFIX;
            final File file = new File(newFileName);
            if(!file.exists()) {
                FileUtils.copyFile(templateFile, file);
                Thread.sleep(100);
            }
            RootTemplateDefinition rootTemplateDefinition= (RootTemplateDefinition) clone;
            rootTemplateDefinition.setTemplateFile(file);
        } catch (Exception e) {
            logger.error(e);
        }
        if(null!=defaultListableTemplateFactory.getTemplateDefinition(copyTemplateName)) {
            defaultListableTemplateFactory.removeTemplateDefinition(copyTemplateName);
            defaultListableTemplateFactory.removeTemplate(copyTemplateName);
        }
        defaultListableTemplateFactory.registerTemplateDefinition(copyTemplateName,clone);
        defaultListableTemplateFactory.preInstantiateTemplates();
        Template newTemplate = defaultListableTemplateFactory.getTemplate(copyTemplateName);
        defaultListableTemplateFactory.refreshTemplate(newTemplate);
        return newTemplate;
    }

    public void doSelectMultiple() {
        try {
            templatesOperateFxmlLoader = new FXMLLoader(getClass().getResource("/views/templates_operate.fxml"));
            templatesOperateNode = templatesOperateFxmlLoader.load();
            templatesOperateNode.prefHeightProperty().bind(contentParent.heightProperty());
            templatesOperateNode.prefWidthProperty().bind(contentParent.widthProperty());
            TemplatesOperateController templatesOperateController=templatesOperateFxmlLoader.getController();
            templatesOperateController.getCurrentTemplate().setText("当前组合模板:"+ USER_OPERATE_CACHE.getTemplateNameSelected());
            CheckBox checkBox = (CheckBox) templatesOperateNode.lookup("#isAllTable");
            checkBox.selectedProperty().addListener((o, old, newVal) -> {
                isSelectedAllTable = newVal;
            });
            selectedTable = (TextField) templatesOperateNode.lookup("#targetTable");
            content.getChildren().clear();
            content.getChildren().add(templatesOperateNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化所有表格名
     */
    private void initTableAll() {
        this.tableAll = DbUtil.getAllTableName(DataSourceConfig.getDataSourceConfig(getTemplateContext().getEnvironment()));
    }

    @FXML
    public void addTemplate() throws IOException {
        toNewTemplateView(null);
    }

    public void toNewTemplateView(Template template) throws IOException {
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
            templateController.selectTemplateClassName.getSelectionModel()
                    .select(template.getClass().getName());
            templateController.projectUrl.setText(Utils.convertTruePathIfNotNull(template.getProjectUrl()));
            templateController.moduleName.setText(Utils.convertTruePathIfNotNull(template.getModule()));
            templateController.sourcesRootName.setText(Utils.convertTruePathIfNotNull(template.getSourcesRoot()));
            templateController.srcPackageName.setText(Utils.convertTruePathIfNotNull(template.getSrcPackage()));
            templateController.setFile(template.getTemplateFile());
            templateController.fileName.setText(template.getTemplateFile().getName());
            templateController.fileSuffixName.setText(template.getTargetFileSuffixName());
            if(template instanceof HaveDependTemplate) {
                HaveDependTemplate haveDepend= (HaveDependTemplate) template;
                if(!CollectionUtils.isEmpty(haveDepend.getDependTemplates())) {
                    templateController.depends.setText(String.join(",", haveDepend.getDependTemplates()));
                }
            }
            TargetFilePrefixNameStrategy targetFilePrefixNameStrategy = template.getTemplateFilePrefixNameStrategy();
            int typeValue = targetFilePrefixNameStrategy.getTypeValue();
            RadioButton node = (RadioButton) templateController.filePrefixNameStrategy.getChildren().stream().filter(radio -> ((RadioButton) radio).getText().equals(String.valueOf(typeValue))).findFirst().get();
            node.setSelected(true);
            if (typeValue == 3) {
                templateController.filePrefixNameStrategyPane.setVisible(true);
                PatternTargetFilePrefixNameStrategy patternTemplateFilePrefixNameStrategy = (PatternTargetFilePrefixNameStrategy) targetFilePrefixNameStrategy;
                templateController.filePrefixNameStrategyPattern.setText(patternTemplateFilePrefixNameStrategy.getPattern());
            }
        }
        Scene scene = new Scene(root);
        secondWindow.setTitle(isEdit ? "编辑模板" : "新建模板");
        secondWindow.setScene(scene);
        secondWindow.initModality(Modality.WINDOW_MODAL);
        secondWindow.show();
        secondWindow.centerOnScreen();
    }

    @FXML
    public void coreConfig() throws IOException {
        Stage secondWindow = new Stage();
        Parent root = new FXMLLoader(this.getClass().getResource("/views/config.fxml")).load();
        Scene scene = new Scene(root);
        secondWindow.setTitle("核心配置");
        secondWindow.setScene(scene);
        secondWindow.show();
    }

    @FXML
    public void doBuildCore() {
         doBuild(FileBuilderEnum.NEW);
    }

    @FXML
    public void doBuildCoreAfter() {
        doBuild(FileBuilderEnum.SUFFIX);
    }

    public void doBuild(FileBuilderEnum fileBuilderEnum) {
        try {
            TemplatesOperateController templatesOperateController = templatesOperateFxmlLoader.getController();
            final CheckBox isDefinedFunction = templatesOperateController.getIsDefinedFunction();
            final TextField representFactor = templatesOperateController.getRepresentFactor();
            final TextField fields = templatesOperateController.getFields();
            if(isDefinedFunction.isSelected()&&(!StrUtil.isAllNotBlank(representFactor.getText(),fields.getText()))){
                FxAlerts.warn("告警","请输入字段名或者代表因子");
                return;
            }
            if (isSelectedAllTable) {
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
                return;
            }
            ProjectTemplateInfoConfig projectTemplateInfoConfig = getProjectTemplateInfoConfig();
            CoreConfig coreConfig = new CoreConfig(DataSourceConfig.getDataSourceConfig(getTemplateContext().getEnvironment()), projectTemplateInfoConfig);
            if (logger.isInfoEnabled()) {
                logger.info("选中的模板名 {}", templatesOperateController.getSelectTemplateGroup().keySet());
            }
            ConfigFileTemplateVariableResource configFileTemplateVariableResource=null;
            if (null != templatesOperateController.getFile()) {
                configFileTemplateVariableResource=new ConfigFileTemplateVariableResource(
                        Files.newInputStream(templatesOperateController.getFile().toPath()));
            }
            final FileBuilder fileBuilder = getFileBuilder(fileBuilderEnum, coreConfig);
            final long l = System.currentTimeMillis();
            ConfigFileTemplateVariableResource finalConfigFileTemplateVariableResource = configFileTemplateVariableResource;
            ProgressTask progressTask = new ProgressTask() {
                @Override
                protected void execute() {
                    ComplexController.this.concurrentDoBuild(fileBuilder,
                            finalConfigFileTemplateVariableResource, (total, current) -> updateProgress(current, total));
                }
            };
            Window controllerWindow = mainBox.getScene().getWindow();
            FxProgressDialog dialog = FxProgressDialog.create(controllerWindow, progressTask, "正在生成中...");
            progressTask.setOnCancelled(event -> {
                throw new IllegalArgumentException("生成被取消。");
            });
            progressTask.setOnFailed(event -> {
                Throwable e = event.getSource().getException();
                if (e != null) {
                    logger.error("生成失败", e);
                    FxAlerts.error(controllerWindow, "生成失败", e);
                } else {
                    FxAlerts.error(controllerWindow, "生成失败", event.getSource().getMessage());
                }
            });
            dialog.showAndWait();
            final long e = System.currentTimeMillis();
            StaticLog.info("done..... {}", (e - l) / 1000);
        } catch (Exception e) {
            this.tableSelected.clear();
            logger.error("build error",e);
            AlertUtil.showError(e.getMessage());
        }
    }

    public void concurrentDoBuild(FileBuilder fileBuilder,
                                  ConfigFileTemplateVariableResource propertiesVariable,
                                  BiConsumer<Integer, Integer> onProgressUpdate) {
        List<CompletableFuture<Void>> task=new ArrayList<>();
        if(!tableSelected.isEmpty()) {
            for (String tableName : tableSelected) {
                //数据库变量资源
                DataSourceTemplateVariableResource dataSourceTemplateVariableResource = new DataSourceTemplateVariableResource(
                        tableName,  getTemplateContext().getEnvironment());
                final Queue<Map<String, Object>> noShareVar = Optional.ofNullable(propertiesVariable)
                        .map(ConfigFileTemplateVariableResource::getNoShareVar).orElse(new LinkedList<>());
                doBuildTemplate(new DoBuildTemplateParam.Builder()
                        .fileBuilder(fileBuilder)
                        .templateVariableResources(Arrays.asList(propertiesVariable,dataSourceTemplateVariableResource))
                        .onProgressUpdate(onProgressUpdate)
                        .task(task)
                        .tableName(tableName)
                        .noShareVar(noShareVar)
                        .queueSize(0)
                        .build());
            }
        }else{
            final Queue<Map<String, Object>> noShareVar = propertiesVariable.getNoShareVar();
            int sizeAll=noShareVar.size();
            while(!noShareVar.isEmpty()) {
                doBuildTemplate(new DoBuildTemplateParam.Builder()
                        .fileBuilder(fileBuilder)
                        .templateVariableResources(Collections.singletonList(propertiesVariable))
                        .onProgressUpdate(onProgressUpdate)
                        .task(task)
                        .tableName( (String) propertiesVariable.getTemplateVariable().getOrDefault(
                                DEFAULT_SRC_RESOURCE_KEY, DEFAULT_SRC_RESOURCE_VALUE))
                        .noShareVar(noShareVar)
                        .queueSize(sizeAll)
                        .build());
            }
        }
        StaticLog.debug("do get task {}",task.size());
        CompletableFuture.allOf(task.toArray(new CompletableFuture[]{})).whenCompleteAsync((v,e)->{
            if(e==null){
                AlertUtil.showInfo("生成成功!");
            }
            this.tableSelected.clear();
        },DO_ANALYSIS_TEMPLATE).join();
    }

    private void doBuildTemplate(DoBuildTemplateParam doBuildTemplateParam) {
        final TemplateContext templateContext = getTemplateContext();
        final TemplatesOperateController controller = templatesOperateFxmlLoader.getController();
        final Set<String> templateNamesSelected = controller.getSelectTemplateGroup()
                .get(USER_OPERATE_CACHE.getTemplateNameSelected()).keySet();
        final int size = tableSelected.size();
        int all=0==size?templateNamesSelected.size()*doBuildTemplateParam.getQueueSize():templateNamesSelected.size()* size;
        AtomicInteger i= new AtomicInteger(1);
        for (String templateName : templateNamesSelected) {
            final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                final long l = System.currentTimeMillis();
                Template template = templateContext.getTemplate(templateName);
                template.getTemplateVariables().putAll(
                        doBuildTemplateParam.getTemplateVariableResources().stream()
                                .filter(Objects::nonNull)
                                .findFirst().orElseThrow(()->new CodeConfigException("not get eVariable resource config"))
                                .mergeTemplateVariable(doBuildTemplateParam.getTemplateVariableResources()));
                Optional.ofNullable(doBuildTemplateParam.getNoShareVar().poll()).ifPresent(s->template.getTemplateVariables().putAll(s));
                doSetDependTemplateVariablesMapping(template, doBuildTemplateParam.getTableName());
                if (templateContext instanceof AbstractTemplateContext) {
                    AbstractTemplateContext abstractTemplateContext = (AbstractTemplateContext) templateContext;
                    Platform.runLater(() -> abstractTemplateContext.publishEvent(
                            new DoGetTemplateAfterEvent(template, controller)));
                }
                doBuildTemplateParam.getFileBuilder().build(template);
                final long e = System.currentTimeMillis();
                StaticLog.debug("{} 耗时: {}", template.getTemplateName(), (e - l) / 1000);
            }, DO_ANALYSIS_TEMPLATE).whenCompleteAsync((v, e) -> doBuildTemplateParam.getOnProgressUpdate().accept(all, i.getAndIncrement()),
                    DO_ANALYSIS_TEMPLATE);
            doBuildTemplateParam.getTask().add(voidCompletableFuture);
        }
    }

    private static FileBuilder getFileBuilder(FileBuilderEnum fileBuilderEnum, CoreConfig coreConfig) {
        final FileBuilder fileBuilder = FileBuilderFactory.getFileBuilder(fileBuilderEnum);
        AbstractFileCodeBuilderStrategy fileCodeBuilderStrategy = (AbstractFileCodeBuilderStrategy)
                fileBuilder.getFileCodeBuilderStrategy();
        fileCodeBuilderStrategy.setCoreConfig(coreConfig);
        return fileBuilder;
    }

    private void doSetDependTemplateVariablesMapping(Template template, String tableName){
        final String simpleClassName = template.getTemplateFilePrefixNameStrategy().prefixStrategy(template, tableName);
        template.getTemplateVariables().put("className",Utils.pathToPackage(template.getSrcPackage())+"."+simpleClassName);
        template.getTemplateVariables().put("simpleClassName",simpleClassName);
        if(template instanceof HaveDependTemplate){
            HaveDependTemplate haveDependTemplate= (HaveDependTemplate) template;
            if(!CollectionUtils.isEmpty(haveDependTemplate.getDependTemplates())) {
                haveDependTemplate.getDependTemplates()
                        .forEach(s -> {
                            final Template templateDepend = getTemplateContext().getTemplate(s);
                            Map<String, Object> templateVariables =new HashMap<>();
                            templateVariables.put("tableInfo", template.getTemplateVariables().get("tableInfo"));
                            templateDepend.getTemplateVariables().putAll(templateVariables);
                        });
            }
        }
    }

    private ProjectTemplateInfoConfig getProjectTemplateInfoConfig() {
        List<DefinedFunctionDomain> definedFunctionDomains = new ArrayList<>();
        Scene scene = templatesOperateNode.getScene();
        CheckBox isDefinedFunction = (CheckBox) scene.lookup("#isDefinedFunction");
        TextField fields = (TextField) scene.lookup("#fields");
        TextField representFactor = (TextField) scene.lookup("#representFactor");
        TemplatesOperateController templatesOperateController = templatesOperateFxmlLoader.getController();
        if (isDefinedFunction.isSelected()) {
            templatesOperateController.getSelectTemplateGroup().get(USER_OPERATE_CACHE.getTemplateNameSelected()).forEach((k, v) -> {
                v.forEach(s->definedFunctionDomains.add(new DefinedFunctionDomain(fields.getText(), s, representFactor.getText())));
            });
        }
        return new ProjectTemplateInfoConfig(definedFunctionDomains, templatesOperateController.getSelectTemplateGroup().get(USER_OPERATE_CACHE.getTemplateNameSelected()));
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
        secondWindow.initOwner(mainBox.getScene().getWindow());
        secondWindow.show();
    }

    @FXML
    public void doBuildCoreOverride() {
        FileBuilder fileBuilder = new DefaultFileBuilder();
        FileCodeBuilderStrategy fileCodeBuilderStrategy = new OverrideFileCodeBuilderStrategy();
        fileCodeBuilderStrategy.setDefinedFunctionResolver(new DefaultDefinedFunctionResolver());
        fileBuilder.setFileCodeBuilderStrategy(fileCodeBuilderStrategy);
        doBuild(FileBuilderEnum.OVERRIDE);
    }

    public static final class DoBuildTemplateParam{
        private FileBuilder fileBuilder;
        private List<TemplateVariableResource> templateVariableResources;
        private BiConsumer<Integer,Integer> onProgressUpdate;
        private List<CompletableFuture<Void>> task;
        private String tableName;
        private Queue<Map<String,Object>> noShareVar;
        private int queueSize;

        private DoBuildTemplateParam(Builder builder) {
            setFileBuilder(builder.fileBuilder);
            setTemplateVariableResources(builder.templateVariableResources);
            setOnProgressUpdate(builder.onProgressUpdate);
            setTask(builder.task);
            setTableName(builder.tableName);
            setNoShareVar(builder.noShareVar);
            setQueueSize(builder.queueSize);
        }

        public FileBuilder getFileBuilder() {
            return fileBuilder;
        }

        public void setFileBuilder(FileBuilder fileBuilder) {
            this.fileBuilder = fileBuilder;
        }

        public List<TemplateVariableResource> getTemplateVariableResources() {
            return templateVariableResources;
        }

        public void setTemplateVariableResources(List<TemplateVariableResource> templateVariableResources) {
            this.templateVariableResources = templateVariableResources;
        }

        public BiConsumer<Integer, Integer> getOnProgressUpdate() {
            return onProgressUpdate;
        }

        public void setOnProgressUpdate(BiConsumer<Integer, Integer> onProgressUpdate) {
            this.onProgressUpdate = onProgressUpdate;
        }

        public List<CompletableFuture<Void>> getTask() {
            return task;
        }

        public void setTask(List<CompletableFuture<Void>> task) {
            this.task = task;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public Queue<Map<String, Object>> getNoShareVar() {
            return noShareVar;
        }

        public void setNoShareVar(Queue<Map<String, Object>> noShareVar) {
            this.noShareVar = noShareVar;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }


        public static final class Builder {
            private FileBuilder fileBuilder;
            private List<TemplateVariableResource> templateVariableResources;
            private BiConsumer<Integer, Integer> onProgressUpdate;
            private List<CompletableFuture<Void>> task;
            private String tableName;
            private Queue<Map<String, Object>> noShareVar;
            private int queueSize;

            public Builder() {
            }

            public Builder fileBuilder(FileBuilder val) {
                fileBuilder = val;
                return this;
            }

            public Builder templateVariableResources(List<TemplateVariableResource> val) {
                templateVariableResources = val;
                return this;
            }

            public Builder onProgressUpdate(BiConsumer<Integer, Integer> val) {
                onProgressUpdate = val;
                return this;
            }

            public Builder task(List<CompletableFuture<Void>> val) {
                task = val;
                return this;
            }

            public Builder tableName(String val) {
                tableName = val;
                return this;
            }

            public Builder noShareVar(Queue<Map<String, Object>> val) {
                noShareVar = val;
                return this;
            }

            public Builder queueSize(int val) {
                queueSize = val;
                return this;
            }

            public DoBuildTemplateParam build() {
                return new DoBuildTemplateParam(this);
            }
        }
    }
}
