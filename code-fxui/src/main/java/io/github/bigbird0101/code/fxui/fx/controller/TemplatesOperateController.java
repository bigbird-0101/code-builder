package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSON;
import io.github.bigbird0101.code.core.cache.CachePool;
import io.github.bigbird0101.code.core.common.DbUtil;
import io.github.bigbird0101.code.core.config.StringPropertySource;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
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
import io.github.bigbird0101.code.fxui.fx.bean.PageInputSnapshot;
import io.github.bigbird0101.code.fxui.fx.component.FxAlerts;
import io.github.bigbird0101.code.util.Utils;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import static cn.hutool.core.text.CharSequenceUtil.splitTrim;
import static cn.hutool.core.text.StrPool.COMMA;

/**
 * @author fpp
 * @version 1.0
 * @since 2021/1/7 22:55
 */
public class TemplatesOperateController extends AbstractTemplateContextProvider implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger(TemplatesOperateController.class);

    private static final String DEFAULT_SOURCES_ROOT = "src/main/java";

    private static final String DEFAULT_USER_SAVE_TEMPLATE_CONFIG = "code.user.save.config";
    public static final int MAX_SHOW_SELECT_NUMBER = 2;

    @FXML
    public Label selectTableName;
    @FXML
    Label fileName;
    @FXML
    Label currentTemplate;
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

    private SelectTableController selectTableController;

    public CheckBox getIsAllTable() {
        return isAllTable;
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

    private final Set<String> selectTableNameSet = new HashSet<>();

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
            setSelectTableView();
        } catch (CodeConfigException e) {
            LOGGER.info("Template Operate init error", e);
        }
    }

    private void setSelectTableView() {
        Set<String> selectTableNames = getSelectTableNames();
        if (selectTableNames.size() > MAX_SHOW_SELECT_NUMBER) {
            this.selectTableName.setText("选择的表:" + String.join(COMMA, CollUtil.sub(selectTableNames, 0, 2)) + "...");
        } else {
            this.selectTableName.setText("选择的表:" + String.join(COMMA, selectTableNames));
        }
    }

    @FXML
    public void selectTable() throws IOException {
        Stage secondWindow = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/select_table.fxml"));
        Parent root = fxmlLoader.load();
        this.selectTableController = fxmlLoader.getController();
        this.selectTableController.initViewBeforeSetData(selectTableNameSet);
        Scene scene = new Scene(root);
        secondWindow.setTitle("选表");
        secondWindow.setOnCloseRequest(event -> setSelectTableView());
        secondWindow.setScene(scene);
        secondWindow.initOwner(box.getScene().getWindow());
        secondWindow.show();
    }

    public Set<String> getSelectTableNames() {
        Set<String> strings = Optional.ofNullable(selectTableController).map(SelectTableController::getSelectTableNames)
                .orElse(getTemplateContext().getEnvironment()
                        .functionPropertyIfPresent(DEFAULT_USER_SAVE_TEMPLATE_CONFIG, PageInputSnapshot.class,
                                s -> new HashSet<>(splitTrim(s.getTableNames(), COMMA))));
        if (CollUtil.isNotEmpty(strings)) {
            selectTableNameSet.clear();
            selectTableNameSet.addAll(strings);
        }
        return selectTableNameSet;
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
                    .toList()) {
                VBox vBox;
                try {
                    vBox = initTemplateInfo(template);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (null != vBox) {
                    templates.getChildren().add(vBox);
                }
            }
        }
    }

    protected void initTemplateConfig() {
        try {
            if (selectTemplateGroup.isEmpty()) {
                getTemplateContext().getEnvironment()
                        .consumerPropertyIfPresent(DEFAULT_USER_SAVE_TEMPLATE_CONFIG, PageInputSnapshot.class, pageInputSnapshot -> {
                            selectTemplateGroup = Optional.ofNullable(pageInputSnapshot.getSelectTemplateGroup()).orElse(new HashMap<>(16));
                            doSetView(pageInputSnapshot);
                        });
                if (!selectTemplateGroup.isEmpty()) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("user save config {}", getTemplateContext().getEnvironment()
                                .getProperty(DEFAULT_USER_SAVE_TEMPLATE_CONFIG));
                    }
                }
                selectTemplateGroup.putIfAbsent(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected(), new HashMap<>(16));
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
                getTemplateContext().getEnvironment()
                        .consumerPropertyIfPresent(DEFAULT_USER_SAVE_TEMPLATE_CONFIG, PageInputSnapshot.class,
                                this::doSetView);
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
        isAllTable.setSelected(Optional.ofNullable(pageInputSnapshot.getSelectTableAll()).orElse(false));
    }

    public VBox initTemplateInfo(Template template) throws IOException {
        try {
            assert resource != null;
            VBox root = FXMLLoader.load(resource);
            initTemplateInfo(root, template);
            return root;
        } catch (Exception e) {
            StaticLog.error(e);
            FxAlerts.warn("初始化模板异常", e.getMessage());
        }
        return null;
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
                GridPane gridPane = (GridPane) box.getChildren().get(0);
                GridPane gridPaneForm = (GridPane) box.getChildren().get(1);
                final CheckBox checkBox2 = (CheckBox) gridPane.lookup("#templateName");
                if (checkBox2.isSelected()) {
                    String templateName = (String) checkBox2.getUserData();
                    Template template = getTemplateContext().getTemplate(templateName);
                    doSetTemplate(template, gridPaneForm);
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
                Set<String> checkTables = Optional.ofNullable(this.selectTableController)
                        .map(SelectTableController::getSelectTableNames)
                        .orElse(new HashSet<>());
                LOGGER.info("checked tables {}", checkTables);
                getTemplateContext().getEnvironment().consumerPropertyIfPresent(DEFAULT_USER_SAVE_TEMPLATE_CONFIG, PageInputSnapshot.class, pageInputSnapshot -> {
                    String tableNames = pageInputSnapshot.getTableNames();
                    final PageInputSnapshot build = PageInputSnapshot.Builder
                            .builder()
                            .withCurrentMultipleTemplate(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected())
                            .withUseMultipleTemplateSelected(CodeBuilderApplication.USER_OPERATE_CACHE.getUseMultipleTemplateSelected())
                            .withUnUseMultipleTemplateSelected(CodeBuilderApplication.USER_OPERATE_CACHE.getUnUseMultipleTemplateSelected())
                            .withUnUseMultipleTemplateTopicOne(CodeBuilderApplication.USER_OPERATE_CACHE.getUnUseMultipleTemplateTopicOne())
                            .withUseMultipleTemplateTopicOne(CodeBuilderApplication.USER_OPERATE_CACHE.getUseMultipleTemplateTopicOne())
                            .withFields(fields.getText())
                            .withRepresentFactor(representFactor.getText())
                            .withSelectTemplateGroup(templateGroup)
                            .withTableNames(String.join(",", CollUtil.isEmpty(checkTables) ? splitTrim(tableNames, ",") : checkTables))
                            .withDefinedFunction(isDefinedFunction.isSelected())
                            .withSelectTableAll(isAllTable.isSelected())
                            .build();
                    getTemplateContext().getEnvironment().refreshPropertySourceSerialize(new StringPropertySource(DEFAULT_USER_SAVE_TEMPLATE_CONFIG, JSON.toJSONString(build)));
                });

            }
            TooltipUtil.showToast("保存成功");
        } catch (CodeConfigException e) {
            AlertUtil.showError("save config error :" + e.getMessage());
        }
    }

    public void doSetTemplate(Template template, GridPane gridPane) {
        TextArea textArea = (TextArea) gridPane.lookup("#projectUrl");
        String projectUrl = textArea.getText();
        TextField textField = (TextField) gridPane.lookup("#moduleName");
        String moduleName = textField.getText();
        TextField textField2 = (TextField) gridPane.lookup("#sourcesRoot");
        String sourcesRoot = textField2.getText();
        TextField textField3 = (TextField) gridPane.lookup("#srcPackage");
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

}