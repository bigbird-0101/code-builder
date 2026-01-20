package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.github.bigbird0101.code.core.config.FileUrlResource;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.GenericMultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.RootTemplateDefinition;
import io.github.bigbird0101.code.core.share.MultipleTemplateDefinitionWrapper;
import io.github.bigbird0101.code.core.share.ShareClient;
import io.github.bigbird0101.code.core.share.TemplateDefinitionWrapper;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import io.github.bigbird0101.code.fxui.fx.component.FxAlerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH;
import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_TEMPLATE_FILE_SUFFIX;

/**
 * @author fpp
 * @version 1.0
 * @since 2021/1/12 19:29
 */
public class ImportShareTemplateController extends AbstractTemplateContextProvider implements Initializable {

    // ==================== 常量定义 ====================

    /**
     * 提示信息：是否导入依赖模板
     */
    private static final String MSG_IMPORT_DEPENDENT = "当前导入的模版包含依赖模版，是否同步导入依赖模版？";

    /**
     * 按钮文本：替换
     */
    private static final String BUTTON_REPLACE = "替换";

    /**
     * ZIP目录：模板文件
     */
    private static final String ZIP_DIR_TEMPLATES = "templates/";

    /**
     * ZIP目录：模板定义
     */
    private static final String ZIP_DIR_DEFINITIONS = "definitions/";

    /**
     * ZIP目录：组合模板配置
     */
    private static final String ZIP_DIR_MULTIPLE_TEMPLATES = "multiple_templates/";

    /**
     * ZIP文件：元数据
     */
    private static final String ZIP_FILE_METADATA = "metadata.json";

    /**
     * 模板文件后缀
     */
    private static final String TEMPLATE_FILE_SUFFIX = ".ftl";

    /**
     * 定义文件后缀
     */
    private static final String DEFINITION_FILE_SUFFIX = ".json";

    /**
     * 错误信息：地址不能为空
     */
    private static final String ERROR_URL_EMPTY = "地址不能为空";

    /**
     * 错误信息：请选择ZIP文件
     */
    private static final String ERROR_ZIP_NOT_SELECTED = "请选择ZIP文件";

    /**
     * 错误信息：ZIP文件不存在
     */
    private static final String ERROR_ZIP_NOT_EXISTS = "ZIP文件不存在";

    /**
     * 错误信息：操作失败
     */
    private static final String ERROR_OPERATION_FAILED = "操作失败 错误信息:";

    /**
     * 错误信息：无法导入组合模版
     */
    private static final String ERROR_CANNOT_IMPORT_MULTIPLE = "无法导入组合模版";

    /**
     * 错误信息：无法解析的地址
     */
    private static final String ERROR_INVALID_URL = "无法解析的地址";

    /**
     * 提示信息：是否覆盖
     */
    private static final String MSG_OVERWRITE_CONFIRM = "已存在相同的组合模板: %s\n是否覆盖？";

    /**
     * 成功信息：导入成功
     */
    private static final String MSG_IMPORT_SUCCESS = "导入成功";

    /**
     * 成功信息：替换成功
     */
    private static final String MSG_REPLACE_SUCCESS = "替换成功";

    // ==================== UI组件 ====================

    @FXML
    public TextArea url;

    @FXML
    public Button importTemplate;

    @FXML
    public TextField zipFilePath;

    @FXML
    public ComboBox<String> multipleTemplateSelector;

    @FXML
    public CheckBox includeDependentTemplates;

    // ==================== 业务对象 ====================

    private final ShareClient shareClient = new ShareClient();
    private MainController mainController;
    private String oldTemplateName;
    private String multipleTemplateName;
    private Stage stage;

    /**
     * ZIP文件中的模板元数据
     */
    private List<String> zipTemplateNames = new ArrayList<>();
    private List<String> zipMultipleTemplateNames = new ArrayList<>();

    public void setReplaceButtonText() {
        this.importTemplate.setText(BUTTON_REPLACE);
    }

    public void setOldTemplateName(String oldTemplateName) {
        this.oldTemplateName = oldTemplateName;
    }

    public void setMultipleTemplateName(String multipleTemplateName) {
        this.multipleTemplateName = multipleTemplateName;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        importTemplate.setOnAction(event -> {
            try {
                String urlText = url.getText();
                if (StrUtil.isBlank(urlText)) {
                    AlertUtil.showWarning(ERROR_URL_EMPTY);
                } else {
                    if (this.importTemplate.getText().equals(BUTTON_REPLACE)) {
                        String templateContent = shareClient.templateContent(urlText);
                        Template template = getTemplateContext().getTemplate(oldTemplateName);
                        try {
                            File file = template.getTemplateResource().getFile();
                            FileWriter.create(file).write(templateContent);
                            mainController.refreshTemplate();
                            AlertUtil.showInfo(MSG_REPLACE_SUCCESS);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (shareClient.isTemplateShareUrl(urlText)) {
                        if (StrUtil.isNotBlank(multipleTemplateName)) {
                            TemplateDefinitionWrapper templateDefinitionWrapper = shareClient.template(urlText);
                            if (!templateDefinitionWrapper.getDependTemplateDefinitionList().isEmpty() &&
                                    ButtonType.OK.getButtonData() == AlertUtil.showConfirm(MSG_IMPORT_DEPENDENT).getButtonData()) {
                                templateDefinitionWrapper.registerAndRefreshTemplate(multipleTemplateName);
                            } else {
                                templateDefinitionWrapper.setDependTemplateDefinitionList(Collections.emptyList());
                                templateDefinitionWrapper.registerAndRefreshTemplate(multipleTemplateName);
                            }
                        } else {
                            TemplateDefinitionWrapper templateDefinitionWrapper = shareClient.template(urlText);
                            if (!templateDefinitionWrapper.getDependTemplateDefinitionList().isEmpty() &&
                                    ButtonType.OK.getButtonData() == AlertUtil.showConfirm(MSG_IMPORT_DEPENDENT).getButtonData()) {
                                templateDefinitionWrapper.registerAndRefreshTemplate();
                            } else {
                                templateDefinitionWrapper.setDependTemplateDefinitionList(Collections.emptyList());
                                templateDefinitionWrapper.registerAndRefreshTemplate();
                            }
                        }
                        mainController.initMultipleTemplateViews();
                        AlertUtil.showInfo(MSG_IMPORT_SUCCESS);
                        Node source = (Node) event.getSource();
                        Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                    } else if (StrUtil.isNotBlank(multipleTemplateName) && shareClient.isMultipleTemplateShareUrl(urlText)) {
                        AlertUtil.showWarning(ERROR_CANNOT_IMPORT_MULTIPLE);
                    } else if (shareClient.isMultipleTemplateShareUrl(urlText)) {
                        MultipleTemplateDefinitionWrapper multipleTemplateDefinitionWrapper = shareClient.multipleTemplate(urlText);
                        multipleTemplateDefinitionWrapper.registerAndRefreshMultipleTemplate();
                        mainController.initMultipleTemplateViews();
                        AlertUtil.showInfo(MSG_IMPORT_SUCCESS);
                        Node source = (Node) event.getSource();
                        Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                    } else {
                        AlertUtil.showWarning(ERROR_INVALID_URL);
                    }
                }
            } catch (Exception exception) {
                AlertUtil.showError(ERROR_OPERATION_FAILED + exception.getMessage());
            }
        });
    }

    public void setComplexController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * 选择ZIP文件
     */
    @FXML
    public void selectZipFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择ZIP文件");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ZIP文件", "*.zip")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            zipFilePath.setText(file.getAbsolutePath());
            // 解析ZIP文件获取元数据
            parseZipMetadata(new File(zipFilePath.getText()));
        }
    }

    /**
     * 取消导入
     */
    @FXML
    public void cancelImport() {
        stage.close();
    }

    /**
     * 从ZIP文件导入组合模板
     */
    @FXML
    public void importFromZip() {
        String zipPath = zipFilePath.getText();
        if (StrUtil.isBlank(zipPath)) {
            AlertUtil.showWarning(ERROR_ZIP_NOT_SELECTED);
            return;
        }

        File zipFile = new File(zipPath);
        if (!zipFile.exists()) {
            AlertUtil.showWarning(ERROR_ZIP_NOT_EXISTS);
            return;
        }

        try {

            // 检查是否有可导入的组合模板
            if (zipMultipleTemplateNames.isEmpty()) {
                AlertUtil.showWarning("ZIP文件中没有组合模板");
                return;
            }

            // 获取用户选择的组合模板
            String selectedTemplate = multipleTemplateSelector.getSelectionModel().getSelectedItem();
            if (StrUtil.isBlank(selectedTemplate)) {
                AlertUtil.showWarning("请选择要导入的组合模板");
                return;
            }

            // 导入选中的模板
            importSelectedTemplateFromZip(zipFile, selectedTemplate);

            mainController.initMultipleTemplateViews();
            AlertUtil.showInfo(MSG_IMPORT_SUCCESS);
            stage.close();
        } catch (Exception e) {
            FxAlerts.error(stage.getScene().getWindow(), "导入失败", e);
        }
    }

    /**
     * 解析ZIP文件获取元数据
     */
    private void parseZipMetadata(File zipFile) throws IOException {
        zipTemplateNames.clear();
        zipMultipleTemplateNames.clear();

        try (ZipFile zip = new ZipFile(zipFile, StandardCharsets.UTF_8)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                // 读取元数据
                if (entry.getName().equals(ZIP_FILE_METADATA)) {
                    String metadata = readEntryContent(zip, entry);
                    JSONObject metadataObj = JSONObject.parseObject(metadata);
                    JSONArray templateNames = metadataObj.getJSONArray("templateNames");
                    JSONArray multipleTemplateNames = metadataObj.getJSONArray("multipleTemplateNames");
                    if (templateNames != null) {
                        templateNames.forEach(name -> zipTemplateNames.add((String) name));
                    }
                    if (multipleTemplateNames != null) {
                        multipleTemplateNames.forEach(name -> zipMultipleTemplateNames.add((String) name));
                    }
                    break;
                }
            }

            // 如果没有元数据，扫描ZIP文件获取组合模板
            if (zipMultipleTemplateNames.isEmpty()) {
                entries = zip.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(ZIP_DIR_MULTIPLE_TEMPLATES) &&
                            entry.getName().endsWith(DEFINITION_FILE_SUFFIX)) {
                        String templateName = entry.getName().substring(
                                ZIP_DIR_MULTIPLE_TEMPLATES.length(),
                                entry.getName().length() - DEFINITION_FILE_SUFFIX.length()
                        );
                        zipMultipleTemplateNames.add(templateName);
                    }
                }
            }

            // 更新下拉框选项
            multipleTemplateSelector.getItems().clear();
            multipleTemplateSelector.getItems().addAll(zipMultipleTemplateNames);
            if (!zipMultipleTemplateNames.isEmpty()) {
                multipleTemplateSelector.getSelectionModel().select(0);
            }
        }
    }

    /**
     * 从ZIP文件导入选中的组合模板（单个导入）
     */
    private void importSelectedTemplateFromZip(File zipFile, String selectedTemplateName)
            throws IOException, CodeConfigException {

        GenericTemplateContext templateContext = (GenericTemplateContext) getTemplateContext();
        DefaultListableTemplateFactory factory = templateContext.getTemplateFactory();

        // 检查是否存在相同的组合模板，如果存在询问是否覆盖
        if (templateContext.getMultipleTemplateNames().contains(selectedTemplateName)) {
            String message = String.format(MSG_OVERWRITE_CONFIRM, selectedTemplateName);
            if (ButtonType.OK.getButtonData() != AlertUtil.showConfirm(message).getButtonData()) {
                return; // 用户选择不覆盖，直接返回
            }
            // 删除已存在的组合模板
            factory.removeMultipleTemplate(selectedTemplateName);
            factory.removeMultipleTemplateDefinition(selectedTemplateName);
        }

        // 读取选中的组合模板配置，获取包含的模板列表
        List<String> requiredTemplateNames = new ArrayList<>();
        try (ZipFile zip = new ZipFile(zipFile, StandardCharsets.UTF_8)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().equals(ZIP_DIR_MULTIPLE_TEMPLATES + selectedTemplateName + DEFINITION_FILE_SUFFIX)) {
                    String content = readEntryContent(zip, entry);
                    JSONObject configObj = JSONObject.parseObject(content);
                    JSONArray nameArray = configObj.getJSONArray("templateNames");
                    if (nameArray != null) {
                        for (int i = 0; i < nameArray.size(); i++) {
                            requiredTemplateNames.add(nameArray.getString(i));
                        }
                    }
                    break;
                }
            }
        }

        // 询问是否导入依赖模板
        boolean importDependents = false;
        if (!requiredTemplateNames.isEmpty() && ButtonType.OK.getButtonData() ==
                AlertUtil.showConfirm(MSG_IMPORT_DEPENDENT).getButtonData()) {
            importDependents = true;
        }

        // 导入所需的模板和组合模板
        try (ZipFile zip = new ZipFile(zipFile, StandardCharsets.UTF_8)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                // 导入模板文件
                if (entry.getName().startsWith(ZIP_DIR_TEMPLATES) && entry.getName().endsWith(TEMPLATE_FILE_SUFFIX)) {
                    String templateName = entry.getName().substring(ZIP_DIR_TEMPLATES.length(),
                            entry.getName().length() - TEMPLATE_FILE_SUFFIX.length());
                    if (!importDependents && !requiredTemplateNames.contains(templateName)) {
                        continue;
                    }
                    importTemplateFile(zip, entry, templateName);
                }

                // 导入模板定义
                if (entry.getName().startsWith(ZIP_DIR_DEFINITIONS) && entry.getName().endsWith(DEFINITION_FILE_SUFFIX)) {
                    String templateName = entry.getName().substring(ZIP_DIR_DEFINITIONS.length(),
                            entry.getName().length() - DEFINITION_FILE_SUFFIX.length());
                    if (!importDependents && !requiredTemplateNames.contains(templateName)) {
                        continue;
                    }
                    importTemplateDefinition(zip, entry, templateName);
                }

                // 导入选中的组合模板配置
                if (entry.getName().equals(ZIP_DIR_MULTIPLE_TEMPLATES + selectedTemplateName + DEFINITION_FILE_SUFFIX)) {
                    importMultipleTemplateDefinition(zip, entry, selectedTemplateName);
                }
            }

            // 重新实例化模板
            factory.preInstantiateTemplates();

            // 刷新导入的组合模板
            io.github.bigbird0101.code.core.template.MultipleTemplate multipleTemplate =
                    templateContext.getMultipleTemplate(selectedTemplateName);
            if (multipleTemplate != null) {
                factory.refreshMultipleTemplate(multipleTemplate);
            }
        }
    }

    /**
     * 导入模板文件
     */
    private void importTemplateFile(ZipFile zip, ZipEntry entry, String templateName) throws IOException {
        String templatePath = getTemplateContext().getEnvironment()
                .getProperty(DEFAULT_CORE_TEMPLATE_FILES_PATH) + File.separator + templateName + DEFAULT_TEMPLATE_FILE_SUFFIX;
        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            templateFile.getParentFile().mkdirs();
            templateFile.createNewFile();
        }

        try (InputStream is = zip.getInputStream(entry);
             FileOutputStream fos = new FileOutputStream(templateFile)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
    }

    /**
     * 导入模板定义
     */
    private void importTemplateDefinition(ZipFile zip, ZipEntry entry, String templateName) throws IOException, CodeConfigException {
        String content = readEntryContent(zip, entry);
        JSONObject definitionObj = JSONObject.parseObject(content);

        GenericTemplateContext templateContext = (GenericTemplateContext) getTemplateContext();
        DefaultListableTemplateFactory factory = templateContext.getTemplateFactory();

        // 如果模板定义已存在，先删除旧的
        if (null != factory.getTemplateDefinition(templateName)) {
            factory.removeTemplateDefinition(templateName);
        }

        RootTemplateDefinition templateDefinition = new RootTemplateDefinition();
        // 设置依赖模板
        LinkedHashSet<String> dependTemplates = new LinkedHashSet<>();
        JSONArray dependArray = definitionObj.getJSONArray("dependTemplateDefinitionList");
        if (dependArray != null) {
            for (int i = 0; i < dependArray.size(); i++) {
                dependTemplates.add(dependArray.getString(i));
            }
        }
        templateDefinition.setDependTemplates(dependTemplates);

        // 设置模板资源
        String templatePath = getTemplateContext().getEnvironment()
                .getProperty(DEFAULT_CORE_TEMPLATE_FILES_PATH) + File.separator + templateName + DEFAULT_TEMPLATE_FILE_SUFFIX;
        File templateFile = new File(templatePath);
        if (templateFile.exists()) {
            templateDefinition.setTemplateResource(new FileUrlResource(templateFile.getAbsolutePath()));
        }

        // 注册模板定义
        factory.registerTemplateDefinition(templateName, templateDefinition);
    }

    /**
     * 导入组合模板定义
     */
    private void importMultipleTemplateDefinition(ZipFile zip, ZipEntry entry, String multipleTemplateName)
            throws IOException, CodeConfigException {
        String content = readEntryContent(zip, entry);
        JSONObject configObj = JSONObject.parseObject(content);

        GenericTemplateContext templateContext = (GenericTemplateContext) getTemplateContext();
        DefaultListableTemplateFactory factory = templateContext.getTemplateFactory();

        // 如果组合模板已存在，先删除旧的（在主流程中已经处理，这里再次确保）
        if (templateContext.getMultipleTemplateNames().contains(multipleTemplateName)) {
            factory.removeMultipleTemplateDefinition(multipleTemplateName);
        }

        GenericMultipleTemplateDefinition multipleTemplateDefinition = new GenericMultipleTemplateDefinition();

        // 设置包含的模板名称
        Set<String> templateNames = new java.util.LinkedHashSet<>();
        JSONArray nameArray = configObj.getJSONArray("templateNames");
        if (nameArray != null) {
            for (int i = 0; i < nameArray.size(); i++) {
                templateNames.add(nameArray.getString(i));
            }
        }
        multipleTemplateDefinition.setTemplateNames(templateNames);

        // 注册组合模板定义
        templateContext.registerMultipleTemplateDefinition(multipleTemplateName, multipleTemplateDefinition);
    }

    /**
     * 读取ZIP条目内容
     */
    private String readEntryContent(ZipFile zip, ZipEntry entry) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    sb.append("\n");
                }
                sb.append(line);
                firstLine = false;
            }
        }
        return sb.toString();
    }
}
