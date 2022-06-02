package com.fpp.code.fxui.fx.controller;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.hutool.system.UserInfo;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fpp.code.core.common.CollectionUtils;
import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.config.CoreConfig;
import com.fpp.code.core.config.Environment;
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
import com.fpp.code.core.template.*;
import com.fpp.code.fxui.Main;
import com.fpp.code.fxui.common.AlertUtil;
import com.fpp.code.fxui.common.DbUtil;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author fpp
 */
public class ComplexController extends TemplateContextProvider implements Initializable {
    @FXML
    public VBox mainBox;
    @FXML
    public StackPane contentParent;
    @FXML
    public Menu showLog;
    private Logger logger = LogManager.getLogger(getClass());
    @FXML
    public TreeView<Label> listViewTemplate;
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
    private static ExecutorService executorService=Executors.newFixedThreadPool(4);
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
            final TreeItem<Label> labelTreeItem = root.getChildren()
                    .filtered(s -> s.getValue().getText().equals(defaultMultipleTemplate))
                    .get(0);
            listViewTemplate.getSelectionModel().select(labelTreeItem);
        }else{
            listViewTemplate.getSelectionModel().select(0);
        }
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
            List<TreeItem<Label>> collect = templateContext.getMultipleTemplate(multipleTemplateName).getTemplates().stream().map(template -> getAndInitTemplateView(template, multipleTemplateName, item)).collect(Collectors.toList());
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
                Main.USER_OPERATE_CACHE.setTemplateNameSelected(defaultListableTemplateFactory.getMultipleTemplateNames().stream().findFirst().orElse(""));
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
        List<TreeItem<Label>> collect = defaultListableTemplateFactory.getMultipleTemplate(copyMultipleTemplateName).getTemplates().stream().map(template -> getAndInitTemplateView(template, copyMultipleTemplateName, copyItem)).collect(Collectors.toList());
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
            templateController.fileSuffixName.setText(template.getTemplateFileSuffixName());
            if(template instanceof HaveDependTemplate) {
                HaveDependTemplate haveDepend= (HaveDependTemplate) template;
                if(!CollectionUtils.isEmpty(haveDepend.getDependTemplates())) {
                    templateController.depends.setText(String.join(",", haveDepend.getDependTemplates()));
                }
            }
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
        secondWindow.initModality(Modality.WINDOW_MODAL);
        secondWindow.show();
        secondWindow.centerOnScreen();
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
            if(isDefinedFunction.isSelected()&&(StrUtil.isBlank(representFactor.getText())||StrUtil.isBlank(fields.getText()))){
                FxAlerts.warn("告警","请输入字段名或者代表因子");
                return;
            }
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
                return;
            }
            ProjectTemplateInfoConfig projectTemplateInfoConfig = getProjectTemplateInfoConfig();
            CoreConfig coreConfig = new CoreConfig(getDataSourceConfig(getTemplateContext().getEnvironment()), projectTemplateInfoConfig);
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
                        logger.error("error propertiesVariable",e);
                    }
                }
            });

            final long l = System.currentTimeMillis();
            ProgressTask progressTask = new ProgressTask() {
                @Override
                protected void execute() {
                    ComplexController.this.concurrentDoBuild(
                            fileBuilderEnum, templatesOperateController, coreConfig, propertiesVariable, (total, current) -> updateProgress(current, total)
                    );
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

    public void concurrentDoBuild(FileBuilderEnum fileBuilderEnum, TemplatesOperateController templatesOperateController, CoreConfig coreConfig, AtomicReference<Properties> propertiesVariable, BiConsumer<Integer, Integer> onProgressUpdate) {
        final Set<String> strings = templatesOperateController.getSelectTemplateGroup().get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).keySet();
        int all=strings.size()*tableSelected.size();
        AtomicInteger i= new AtomicInteger(1);
        List<CompletableFuture<Void>> task=new ArrayList<>();
        for (String tableName : tableSelected) {
            for (String templateName : strings) {
                final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
                    final long l = System.currentTimeMillis();
                    final FileBuilder fileBuilder = FileBuilderFactory.getFileBuilder(fileBuilderEnum);
                    AbstractFileCodeBuilderStrategy fileCodeBuilderStrategy = (AbstractFileCodeBuilderStrategy) fileBuilder.getFileCodeBuilderStrategy();
                    fileCodeBuilderStrategy.setCoreConfig(coreConfig);
                    Template template = doGetTemplate(templateName, tableName, coreConfig, propertiesVariable.get(), templatesOperateController);
                    TemplateTraceContext.setCurrentTemplate(template);
                    TemplateContextProvider.doPushEventTemplateContextAware();
                    fileBuilder.build(template);
                    final long e = System.currentTimeMillis();
                    StaticLog.debug("{} 耗时: {}", template.getTemplateName(), (e - l) / 1000);
                },DO_ANALYSIS_TEMPLATE).whenCompleteAsync((v, e) -> onProgressUpdate.accept(all,i.getAndIncrement()),DO_ANALYSIS_TEMPLATE);
                task.add(voidCompletableFuture);
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

    private Template doGetTemplate(String templateName, String tableName, CoreConfig coreConfig, Properties propertiesVariable, TemplatesOperateController templatesOperateController){
        Template template = getTemplateContext().getTemplate(templateName);
        Future<Map<String, Object>> tempValue = executorService.submit(() -> {
            Map<String, Object> temp = new HashMap<>(10);
            TableInfo tableInfo;
            try {
                tableInfo = DbUtil.getTableInfo(coreConfig.getDataSourceConfig(), tableName);
            } catch (SQLException e) {
                logger.error("doGetTemplate DbUtil.getTableInfo error",e);
                throw e;
            }
            temp.put("tableInfo", tableInfo);
            temp.put("packageName",Utils.pathToPackage(template.getSrcPackage()));
            if (null != propertiesVariable) {
                propertiesVariable.forEach((k, v) -> temp.put((String) k, v));
            }
            return temp;
        });
        try {
            template.setTemplateVariables(tempValue.get());
            final String simpleClassName = template.getTemplateFilePrefixNameStrategy().prefixStrategy(template, tableName);
            template.getTemplateVariables().put("className",Utils.pathToPackage(template.getSrcPackage())+"."+simpleClassName);
            template.getTemplateVariables().put("simpleClassName",simpleClassName);
            if(template instanceof HaveDependTemplate){
                HaveDependTemplate haveDependTemplate= (HaveDependTemplate) template;
                if(!CollectionUtils.isEmpty(haveDependTemplate.getDependTemplates())) {
                    haveDependTemplate.getDependTemplates()
                            .forEach(s -> {
                                final Template templateDepend = getTemplateContext().getTemplate(s);
                                Map<String, Object> templateVariables = templateDepend.getTemplateVariables();
                                if (null == templateVariables) {
                                    templateVariables = new HashMap<>();
                                }
                                templateVariables.put("tableInfo", template.getTemplateVariables().get("tableInfo"));
                                templateDepend.setTemplateVariables(templateVariables);
                            });
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            StaticLog.error(e);
            throw new TemplateResolveException(e);
        }

        Platform.runLater(()->{
            //找到对应的模板checkbox
            Set<Node> nodes = templatesOperateController.getTemplates().lookupAll("AnchorPane");
            List<CheckBox> collect = nodes.stream().map(node -> (AnchorPane) node)
                    .map(anchorPane -> anchorPane.lookup("CheckBox"))
                    .map(node -> (CheckBox) node)
                    .filter(checkBox -> checkBox.getUserData().equals(template.getTemplateName()))
                    .collect(Collectors.toList());
            CheckBox checkBoxTarget = collect.stream().findFirst().orElse(null);
            if(null!=checkBoxTarget) {
                AnchorPane anchorPane = (AnchorPane) checkBoxTarget.getParent().getParent();
                //重新设置模板值但不持久化
                templatesOperateController.doSetTemplate(template, anchorPane);
            }else{
                logger.warn("checkBoxTarget not get {}",template.getTemplateName());
            }
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
}
