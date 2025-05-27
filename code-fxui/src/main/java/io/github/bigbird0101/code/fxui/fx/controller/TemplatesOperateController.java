package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.github.bigbird0101.code.core.cache.CachePool;
import io.github.bigbird0101.code.core.common.DbUtil;
import io.github.bigbird0101.code.core.config.StringPropertySource;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.domain.DataSourceConfig;
import io.github.bigbird0101.code.core.event.TemplateListener;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.OperateTemplateBeanFactory;
import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.exception.TemplateResolveException;
import io.github.bigbird0101.code.fxui.CodeBuilderApplication;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import io.github.bigbird0101.code.fxui.common.TooltipUtil;
import io.github.bigbird0101.code.fxui.event.DatasourceConfigUpdateEvent;
import io.github.bigbird0101.code.fxui.fx.bean.PageInputSnapshot;
import io.github.bigbird0101.code.fxui.fx.component.FxAlerts;
import io.github.bigbird0101.code.util.Utils;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static cn.hutool.core.comparator.CompareUtil.compare;

/**
 * @author fpp
 * @version 1.0
 * @since 2021/1/7 22:55
 */
public class TemplatesOperateController extends AbstractTemplateContextProvider implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(TemplatesOperateController.class);

    private static final String DEFAULT_SOURCES_ROOT = "src/main/java";

    private static final String DEFAULT_USER_SAVE_TEMPLATE_CONFIG = "code.user.save.config";

    @FXML
    Label fileName;
    @FXML
    Label currentTemplate;
    @FXML
    CheckComboBox<String> targetTable;
    @FXML
    CheckBox isDefinedFunction;
    @FXML
    TextField representFactor;
    @FXML
    TextField fields;
    @FXML
    CheckBox isAllTable;
    @FXML
    private VBox box;

    @FXML
    private TilePane templates;

    public CheckBox getIsAllTable() {
        return isAllTable;
    }

    public CheckComboBox<String> getTargetTable() {
        return targetTable;
    }

    CheckBox getIsDefinedFunction() {
        return isDefinedFunction;
    }

    public TextField getRepresentFactor() {
        return representFactor;
    }

    public TextField getFields() {
        return fields;
    }

    public TilePane getTemplates() {
        return templates;
    }

    /**
     * 变量文件
     */
    private File file;
    private Map<String, Map<String, List<String>>> selectTemplateGroup = new ConcurrentHashMap<>();
    private final URL resource = getClass().getResource("/views/template_info.fxml");

    private Set<String> allTables = new HashSet<>();

    public File getFile() {
        return file;
    }

    public Map<String, Map<String, List<String>>> getSelectTemplateGroup() {
        return selectTemplateGroup;
    }

    public Label getCurrentTemplate() {
        return currentTemplate;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            templates.getChildren().clear();
            templates.autosize();
            getTemplateContext().addListener(new TemplatesOperateController.DatasourceConfigUpdateEventListener());
            Platform.runLater(this::initData);
        } catch (CodeConfigException e) {
            LOGGER.info("Template Operate init error", e);
        }
    }

    @FXML
    public void selectTable(ActionEvent actionEvent) throws IOException {
        Stage secondWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/select_table.fxml"));
        Parent root = fxmlLoader.load();
        SelectTableController selectTableController = fxmlLoader.getController();
        selectTableController.initTableCheckBox(new HashSet<>(allTables));
        Scene scene = new Scene(root);
        secondWindow.setTitle("选表");
        secondWindow.setScene(scene);
        secondWindow.initOwner(box.getScene().getWindow());
        secondWindow.show();
    }

    private class DatasourceConfigUpdateEventListener extends TemplateListener<DatasourceConfigUpdateEvent> {
        @Override
        protected void onTemplateEvent(DatasourceConfigUpdateEvent doGetTemplateAfterEvent) {
            TemplatesOperateController.this.initData();
        }
    }

    private void initData() {
        Platform.runLater(() -> {
            Task<List<String>> task = new Task<List<String>>() {
                @Override
                protected List<String> call() {
                    // 后台线程执行耗时操作
                    DataSourceConfig dataSourceConfig = DataSourceConfig.getDataSourceConfig(getTemplateContext()
                            .getEnvironment());
                    List<String> allTableName = DbUtil.getAllTableName(dataSourceConfig);
                    allTables.addAll(allTableName);
                    return allTableName;
                }
            };
            task.setOnSucceeded(event -> {
                // 回到 JavaFX 主线程更新 UI
                List<String> allTableName = task.getValue();
                targetTable.getItems().clear();
                targetTable.getItems().addAll(allTableName);
            });
            task.setOnFailed(event -> StaticLog.error("initData 异步加载失败", task.getException()));
            new Thread(task).start();
        });
    }

    protected void doInitView() {
        initTemplateConfig();
        String templateNameSelected = CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected();
        if (StrUtil.isBlank(templateNameSelected)) {
            return;
        }
        MultipleTemplate multipleTemplate = getTemplateContext().getMultipleTemplate(templateNameSelected);
        if (null != multipleTemplate) {
            for (Template template : multipleTemplate.getTemplates().stream()
                    .sorted((o1, o2) -> compare(o1.hashCode(), o2.hashCode()))
                    .collect(Collectors.toList())) {
                VBox vBox;
                try {
                    vBox = initTemplateInfo(template);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                templates.getChildren().add(vBox);
            }
        }
    }

    protected void initTemplateConfig() {
        try {
            String property = getTemplateContext().getEnvironment().getProperty(DEFAULT_USER_SAVE_TEMPLATE_CONFIG);
            if (selectTemplateGroup.isEmpty()) {
                if (Utils.isNotEmpty(property)) {
                    final PageInputSnapshot pageInputSnapshot = JSONObject.parseObject(property, new TypeReference<PageInputSnapshot>() {
                    });
                    selectTemplateGroup = Optional.ofNullable(pageInputSnapshot.getSelectTemplateGroup()).orElse(new HashMap<>());
                    doSetView(pageInputSnapshot);
                }
                if (!selectTemplateGroup.isEmpty()) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("user save config {}", property);
                    }
                }
                selectTemplateGroup.putIfAbsent(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected(), new HashMap<>());
                selectTemplateGroup.forEach((k, v) -> {
                    try {
                        if (k == null || v == null) {
                            LOGGER.error("user save config error {}", selectTemplateGroup);
                            return;
                        }
                        final MultipleTemplate multipleTemplate = getTemplateContext().getMultipleTemplate(k);
                        final Set<String> templateNames = multipleTemplate.getTemplates()
                                .stream().map(Template::getTemplateName).collect(Collectors.toSet());
                        final Set<String> strings = v.keySet();
                        final Collection<String> subtract = CollUtil.subtract(strings, templateNames);
                        for (String templateName : subtract) {
                            v.remove(templateName);
                        }
                    } catch (Exception e) {
                        LOGGER.error("初始化用户配置异常: {} {} {} {} {} {}", e, DEFAULT_USER_SAVE_TEMPLATE_CONFIG, ",", e.getClass().getName(), ":", e.getMessage());
                    }
                });
            } else {
                if (Utils.isNotEmpty(property)) {
                    final PageInputSnapshot pageInputSnapshot = JSONObject.parseObject(property, new TypeReference<PageInputSnapshot>() {
                    });
                    doSetView(pageInputSnapshot);
                }
            }
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("初始化用户配置异常: {} {} {} {} {} {}", e, DEFAULT_USER_SAVE_TEMPLATE_CONFIG, ",", e.getClass().getName(), ":", e.getMessage());
            }
        }
    }

    private void doSetView(PageInputSnapshot pageInputSnapshot) {
        fields.setText(Optional.ofNullable(pageInputSnapshot.getFields()).orElse(StrUtil.EMPTY));
        isDefinedFunction.setSelected(Optional.ofNullable(pageInputSnapshot.getDefinedFunction()).orElse(false));
        representFactor.setText(Optional.ofNullable(pageInputSnapshot.getRepresentFactor()).orElse(StrUtil.EMPTY));
        IndexedCheckModel<String> checkModel = targetTable.getCheckModel();
        targetTable.getItems().addListener((ListChangeListener<String>) c -> {
            if (c.next()) {
                String tableNames = pageInputSnapshot.getTableNames();
                LOGGER.info("tableNames {}", tableNames);
                Optional.ofNullable(tableNames)
                        .map(s -> s.split(","))
                        .ifPresent(s -> {
                            checkModel.clearChecks();
                            if (!targetTable.getItems().isEmpty()) {
                                LOGGER.info("targetTable item {}", targetTable.getItems());
                                for (String s1 : s) {
                                    LOGGER.debug("all select {} targetTable item select {}", s, s1);
                                    if (targetTable.getItems().contains(s1)) {
                                        checkModel.check(s1);
                                    }
                                }
                            } else {
                                LOGGER.warn("targetTable item is empty");
                            }
                        });
            }
        });
        isAllTable.setSelected(Optional.ofNullable(pageInputSnapshot.getSelectTableAll()).orElse(false));
    }

    public VBox initTemplateInfo(Template template) throws IOException {
        VBox root = FXMLLoader.load(resource);
        try {
            initTemplateInfo(root, template);
        } catch (Exception e) {
            StaticLog.error(e);
            FxAlerts.warn("初始化模板异常", e.getMessage());
        }
        return root;
    }

    public void initTemplateInfo(VBox root, Template template) {
        String templateName = template.getTemplateName();
        FlowPane flowPane = (FlowPane) root.lookup("#functionPane");
        doSetTemplateNameLabel(root, templateName);
        CheckBox templateNameCheckBox = (CheckBox) root.lookup("#templateName");
        Map<String, List<String>> stringListMap = selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected());
        if (template instanceof AbstractHandleFunctionTemplate) {
            AbstractHandleFunctionTemplate handlerTemplate = (AbstractHandleFunctionTemplate) template;
            Set<String> templateFunctionNameS = handlerTemplate.getTemplateFunctionNameS();
            templateFunctionNameS.forEach(templateFunction -> {
                CheckBox checkBox = new CheckBox(templateFunction);
                if (null != stringListMap) {
                    List<String> functionNames = stringListMap.get(templateName);
                    if (null != functionNames && functionNames.contains(templateFunction)) {
                        checkBox.setSelected(true);
                    }
                }
                addCheckBoxListen(checkBox, templateNameCheckBox, templateName, templateFunction);
                flowPane.getChildren().add(checkBox);
            });
        } else {
            Label label = new Label(template.getTemplateName());
            flowPane.getChildren().add(label);
        }
        if (null != stringListMap && stringListMap.containsKey(templateName)) {
            templateNameCheckBox.setSelected(true);
        }

        templateNameCheckBox.setUserData(templateName);
        templateNameCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Map<String, List<String>> stringListMap2 = selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected());
            if (newValue) {
                List<String> strings = stringListMap2.get(templateName);
                if (null == strings || strings.isEmpty()) {
                    if (template instanceof AbstractHandleFunctionTemplate) {
                        flowPane.getChildren().forEach(node -> {
                            CheckBox checkBox = (CheckBox) node;
                            checkBox.setSelected(true);
                        });
                    }
                }
                stringListMap2.computeIfAbsent(templateName, k -> new ArrayList<>());
            } else {
                if (template instanceof AbstractHandleFunctionTemplate) {
                    flowPane.getChildren().forEach(node -> {
                        CheckBox checkBox = (CheckBox) node;
                        checkBox.setSelected(false);
                    });
                }
                if (null != stringListMap2) {
                    stringListMap2.remove(templateName);
                }
            }
        });
        initProjectUrlViewData(template, root);
    }

    private static void initProjectUrlViewData(Template template, Node parentNode) {
        TextArea projectUrlTextArea = (TextArea) parentNode.lookup("#projectUrl");
        projectUrlTextArea.setText(Utils.convertTruePathIfNotNull(template.getProjectUrl()));

        TextField moduleNameTextField = (TextField) parentNode.lookup("#moduleName");
        moduleNameTextField.setText(Utils.isEmpty(template.getModule()) ? "/" : Utils.convertTruePathIfNotNull(template.getModule()));

        TextField sourcesRootTextField = (TextField) parentNode.lookup("#sourcesRoot");
        sourcesRootTextField.setText(Utils.convertTruePathIfNotNull(ObjectUtil.defaultIfNull(template.getSourcesRoot(), DEFAULT_SOURCES_ROOT)));

        TextField srcPackageTextField = (TextField) parentNode.lookup("#srcPackage");
        srcPackageTextField.setText(Utils.convertTruePathIfNotNull(template.getSrcPackage()));

        ImageView editTemplate = (ImageView) parentNode.lookup("#editTemplate");
        editTemplate.setOnMouseClicked(event -> {
            final File templateFile;
            try {
                templateFile = template.getTemplateResource().getFile();
                Desktop desktop = Desktop.getDesktop();
                if (templateFile.exists()) {
                    try {
                        desktop.open(templateFile);
                    } catch (IOException ignored) {
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ImageView openTargetFile = (ImageView) parentNode.lookup("#openTargetFile");
        openTargetFile.setOnMouseClicked(event -> {
            final File templateFile;
            final Path path = Paths.get(template.getProjectUrl(), template.getModule(), template.getSourcesRoot(),
                    template.getSrcPackage());
            templateFile = path.toFile();
            Desktop desktop = Desktop.getDesktop();
            if (templateFile.exists()) {
                try {
                    desktop.open(templateFile);
                } catch (IOException ignored) {
                }
            } else {
                TooltipUtil.showToast("文件不存在");
            }
        });
        ImageView copyFileInfo = (ImageView) parentNode.lookup("#copyFileInfo");
        copyFileInfo.setOnMouseClicked(event -> {
            final File templateFile;
            try {
                final Path path = Paths.get(template.getProjectUrl(), template.getModule(), template.getSourcesRoot(),
                        template.getSrcPackage());
                templateFile = path.toFile();
                if (templateFile.exists()) {
                    Clipboard clipboard = Clipboard.getSystemClipboard();
                    ClipboardContent content = new ClipboardContent();//创建剪贴板内容
                    content.putString(IoUtil.readUtf8(Files.newInputStream(templateFile.toPath())));//剪贴板内容对象中添加上文定义的图片
                    clipboard.setContent(content);
                    TooltipUtil.showToast("生成的内容已复制");
                } else {
                    TooltipUtil.showToast("文件不存在");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void doSetTemplateNameLabel(Node scene, String templateName) {
        Label templateNameLabel = (Label) scene.lookup("#templateNameLabel");
        templateNameLabel.setText("模板名:" + templateName);
        templateNameLabel.setOnMouseClicked(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();//创建剪贴板内容
            content.putString(templateName);//剪贴板内容对象中添加上文定义的图片
            clipboard.setContent(content);
            TooltipUtil.showToast("模板名已复制");
        });
    }

    private void addCheckBoxListen(CheckBox checkBox, CheckBox templateNameCheckBox, String templateName, String templateFunction) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                LOGGER.info("selectTemplateGroup newValue {}", newValue);
                if (!selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected()).containsKey(templateName)) {
                    List<String> functionNames = new ArrayList<>();
                    functionNames.add(templateFunction);
                    Map<String, List<String>> stringListMap = selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected());
                    if (null == stringListMap) {
                        stringListMap = new HashMap<>();
                        stringListMap.put(templateName, functionNames);
                        selectTemplateGroup.put(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected(), stringListMap);
                    } else {
                        stringListMap.put(templateName, functionNames);
                    }
                } else {
                    List<String> functionNames = selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected()).get(templateName);
                    if (null == functionNames) {
                        functionNames = new ArrayList<>();
                    }
                    if (!functionNames.contains(templateFunction)) {
                        functionNames.add(templateFunction);
                    }
                    selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected()).put(templateName, functionNames);
                }
                if (!templateNameCheckBox.isSelected()) {
                    templateNameCheckBox.setSelected(true);
                }
                LOGGER.info("selectTemplateGroup {}", selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected()).get(templateName));
            } else {
                LOGGER.info("selectTemplateGroup oldValue{}", oldValue);
                List<String> functionNames = selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected()).get(templateName);
                if (null != functionNames) {
                    functionNames.remove(templateFunction);
                    selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected()).put(templateName, functionNames);
                }
                if (null == functionNames || functionNames.isEmpty()) {
                    if (templateNameCheckBox.isSelected()) {
                        templateNameCheckBox.setSelected(false);
                    }
                    selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected()).remove(templateName);
                }
                LOGGER.info("selectTemplateGroup {}", selectTemplateGroup.get(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected()).get(templateName));
            }
        });
    }

    @FXML
    public void saveConfig() {
        try {
            for (Node node : templates.getChildren()) {
                VBox box = (VBox) node;
                AnchorPane anchorPane = (AnchorPane) box.getChildren().get(0);
                BorderPane borderPane = (BorderPane) anchorPane.getChildren().get(0);
                final HBox hBox = (HBox) borderPane.getCenter();
                final CheckBox checkBox2 = (CheckBox) hBox.lookup("#templateName");
                if (checkBox2.isSelected()) {
                    String templateName = (String) checkBox2.getUserData();
                    Template template = getTemplateContext().getTemplate(templateName);
                    doSetTemplate(template, anchorPane);
                    DefaultListableTemplateFactory operateTemplateBeanFactory = (DefaultListableTemplateFactory) getTemplateContext().getTemplateFactory();
                    operateTemplateBeanFactory.refreshTemplate(template);
                }
            }
            if (!selectTemplateGroup.isEmpty() || getTemplateContext().getMultipleTemplateNames().isEmpty()) {
                Map<String, Map<String, List<String>>> templateGroup = selectTemplateGroup.entrySet()
                        .stream()
                        .filter(s -> StrUtil.isNotBlank(s.getKey())
                                && CollUtil.isNotEmpty(s.getValue())
                                && getTemplateContext().getMultipleTemplateNames().contains(s.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                ObservableList<String> checkedItems = targetTable.getCheckModel().getCheckedItems();
                final PageInputSnapshot build = PageInputSnapshot.Builder
                        .builder()
                        .withCurrentMultipleTemplate(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected())
                        .withFields(fields.getText())
                        .withRepresentFactor(representFactor.getText())
                        .withSelectTemplateGroup(templateGroup)
                        .withTableNames(String.join(",", checkedItems))
                        .withDefinedFunction(isDefinedFunction.isSelected())
                        .withSelectTableAll(isAllTable.isSelected())
                        .build();
                getTemplateContext().getEnvironment().refreshPropertySourceSerialize(new StringPropertySource(DEFAULT_USER_SAVE_TEMPLATE_CONFIG, JSON.toJSONString(build)));
            }
            TooltipUtil.showToast("保存成功");
        } catch (CodeConfigException e) {
            AlertUtil.showError("save config error :" + e.getMessage());
        }
    }

    public void doSetTemplate(Template template, AnchorPane anchorPane) {
        TextArea textArea = (TextArea) anchorPane.getChildren().get(2);
        String projectUrl = textArea.getText();
        TextField textField = (TextField) anchorPane.getChildren().get(4);
        String moduleName = textField.getText();
        TextField textField2 = (TextField) anchorPane.getChildren().get(6);
        String sourcesRoot = textField2.getText();
        TextField textField3 = (TextField) anchorPane.getChildren().get(8);
        String srcPackage = textField3.getText();
        template.setProjectUrl(Utils.convertTruePathIfNotNull(projectUrl));
        template.setModule(Utils.convertTruePathIfNotNull(moduleName));
        template.setSourcesRoot(Utils.convertTruePathIfNotNull(sourcesRoot));
        template.setSrcPackage(Utils.convertTruePathIfNotNull(srcPackage));
    }

    @FXML
    public void refreshTemplate() {
        try {
            OperateTemplateBeanFactory operateTemplateBeanFactory = (OperateTemplateBeanFactory) getTemplateContext().getTemplateFactory();
            String templateNameSelected = CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected();
            if (StrUtil.isNotBlank(templateNameSelected)) {
                MultipleTemplate multipleTemplate = getTemplateContext().getMultipleTemplate(templateNameSelected);
                operateTemplateBeanFactory.refreshMultipleTemplate(multipleTemplate.getTemplateName());
                initialize(null, null);
                doInitView();
                templates.requestLayout();
                CachePool.clearAll();
                DbUtil.CONNECTION_LFU_CACHE.clear();
            }
            AlertUtil.showInfo("刷新成功");
        } catch (CodeConfigException | TemplateResolveException e) {
            AlertUtil.showError("refresh error :" + e.getMessage());
        }
    }

    @FXML
    public void selectVariableFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Template files (*.properties)", "*.properties");
        fileChooser.getExtensionFilters().add(extFilter);
        this.file = fileChooser.showOpenDialog(box.getScene().getWindow());
        if (null != this.file) {
            fileName.setText(this.file.getName());
        }
    }

    @FXML
    private TextField searchTable;

    @FXML
    public void searchTableNames() {
        String searchText = searchTable.getText().toLowerCase();
        ObservableList<String> originalItems = targetTable.getItems();
        IndexedCheckModel<String> checkModel = targetTable.getCheckModel();

        // 保存原始数据
        if (searchText.isEmpty()) {
            targetTable.getItems().setAll(originalItems);
            checkModel.clearChecks();
            return;
        }

        // 动态过滤
        List<String> filteredItems = originalItems.stream()
                .filter(item -> item.toLowerCase().contains(searchText))
                .collect(Collectors.toList());

        // 更新表格内容并选中过滤后的表名
        targetTable.getItems().setAll(filteredItems);
        filteredItems.forEach(checkModel::check);
    }
}