package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.config.MultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import io.github.bigbird0101.code.fxui.fx.component.FxAlerts;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 组合模板导出控制器
 *
 * @author fpp
 */
public class ExportMultipleTemplateController extends AbstractTemplateContextProvider implements Initializable {

    @FXML
    public CheckBox includeDependentTemplates;

    @FXML
    public TextField exportPath;

    @FXML
    public ComboBox<String> multipleTemplateSelector;

    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 加载所有组合模板到下拉框
        List<String> multipleTemplateNames = new ArrayList<>(getTemplateContext().getMultipleTemplateNames());
        multipleTemplateSelector.getItems().addAll(multipleTemplateNames);

        // 如果只有一个组合模板，自动选中
        if (multipleTemplateNames.size() == 1) {
            multipleTemplateSelector.getSelectionModel().select(0);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setSelectedTemplate(String templateName) {
        multipleTemplateSelector.getSelectionModel().select(templateName);
    }

    /**
     * 选择导出路径
     */
    @FXML
    public void selectExportPath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择导出位置");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("ZIP文件", "*.zip")
        );
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            exportPath.setText(file.getAbsolutePath());
        }
    }

    /**
     * 导出组合模板为ZIP
     */
    @FXML
    public void exportMultipleTemplate() {
        String selectedTemplate = multipleTemplateSelector.getSelectionModel().getSelectedItem();
        if (StrUtil.isBlank(selectedTemplate)) {
            AlertUtil.showWarning("请选择要导出的组合模板");
            return;
        }

        String path = exportPath.getText();
        if (StrUtil.isBlank(path)) {
            AlertUtil.showWarning("请选择导出路径");
            return;
        }

        try {
            Set<String> templateNames = new java.util.HashSet<>();
            templateNames.add(selectedTemplate);

            File zipFile = new File(path);
            File parentDir = zipFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            exportTemplatesToZip(zipFile, templateNames, includeDependentTemplates.isSelected());

            AlertUtil.showInfo("导出成功!");
            stage.close();
        } catch (Exception e) {
            FxAlerts.error(stage.getScene().getWindow(), "导出失败", e);
        }
    }

    /**
     * 取消导出
     */
    @FXML
    public void cancelExport() {
        stage.close();
    }

    /**
     * 导出所有组合模板到ZIP文件
     *
     * @param zipFile               ZIP文件
     * @param multipleTemplateNames 组合模板名称列表
     * @param includeDependent      是否包含依赖模板
     */
    private void exportTemplatesToZip(File zipFile, Set<String> multipleTemplateNames, boolean includeDependent) throws IOException {
        List<String> exportedTemplateNames = new ArrayList<>();

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile), StandardCharsets.UTF_8)) {
            DefaultListableTemplateFactory factory = (DefaultListableTemplateFactory) getTemplateContext().getTemplateFactory();

            for (String multipleTemplateName : multipleTemplateNames) {
                MultipleTemplate multipleTemplate = getTemplateContext().getMultipleTemplate(multipleTemplateName);
                if (multipleTemplate == null) {
                    continue;
                }

                // 导出组合模板配置
                MultipleTemplateDefinition multipleTemplateDefinition = factory.getMultipleTemplateDefinition(multipleTemplateName);
                if (multipleTemplateDefinition != null) {
                    String configContent = buildMultipleTemplateConfig(multipleTemplateDefinition, multipleTemplate);
                    ZipEntry configEntry = new ZipEntry("multiple_templates/" + multipleTemplateName + ".json");
                    zos.putNextEntry(configEntry);
                    zos.write(configContent.getBytes(StandardCharsets.UTF_8));
                    zos.closeEntry();
                }

                // 导出包含的模板
                for (Template template : multipleTemplate.getTemplates()) {
                    String templateName = template.getTemplateName();

                    // 如果已经导出过，跳过（避免重复导出）
                    if (exportedTemplateNames.contains(templateName)) {
                        continue;
                    }

                    // 导出模板文件
                    File templateFile = template.getTemplateResource().getFile();
                    if (templateFile.exists()) {
                        ZipEntry templateEntry = new ZipEntry("templates/" + templateName + ".ftl");
                        zos.putNextEntry(templateEntry);
                        try (FileInputStream fis = new FileInputStream(templateFile)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }
                        }
                        zos.closeEntry();
                    }

                    // 导出模板定义
                    TemplateDefinition templateDefinition = factory.getTemplateDefinition(templateName);
                    if (templateDefinition != null) {
                        String definitionContent = buildTemplateDefinitionContent(templateDefinition, templateName);
                        ZipEntry definitionEntry = new ZipEntry("definitions/" + templateName + ".json");
                        zos.putNextEntry(definitionEntry);
                        zos.write(definitionContent.getBytes(StandardCharsets.UTF_8));
                        zos.closeEntry();
                    }

                    exportedTemplateNames.add(templateName);

                    // 如果需要包含依赖模板，递归导出依赖
                    if (includeDependent) {
                        exportDependentTemplates(zos, templateDefinition, exportedTemplateNames);
                    }
                }
            }

            // 写入导出元数据
            String metadata = buildExportMetadata(exportedTemplateNames, multipleTemplateNames);
            ZipEntry metadataEntry = new ZipEntry("metadata.json");
            zos.putNextEntry(metadataEntry);
            zos.write(metadata.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();
        }
    }

    /**
     * 导出依赖模板
     */
    private void exportDependentTemplates(ZipOutputStream zos, TemplateDefinition templateDefinition,
                                          List<String> exportedTemplateNames) throws IOException {
        if (templateDefinition == null) {
            return;
        }

        Set<String> dependentTemplateNames = templateDefinition.getDependTemplates();
        if (CollUtil.isEmpty(dependentTemplateNames)) {
            return;
        }
        for (String depTemplateName : dependentTemplateNames) {
            if (exportedTemplateNames.contains(depTemplateName)) {
                continue;
            }

            Template depTemplate = getTemplateContext().getTemplate(depTemplateName);
            if (depTemplate == null) {
                continue;
            }

            // 导出依赖模板文件
            File templateFile = depTemplate.getTemplateResource().getFile();
            if (templateFile.exists()) {
                ZipEntry templateEntry = new ZipEntry("templates/" + depTemplateName + ".ftl");
                zos.putNextEntry(templateEntry);
                try (FileInputStream fis = new FileInputStream(templateFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
                zos.closeEntry();
            }

            // 导出依赖模板定义
            TemplateDefinition depDefinition = ((DefaultListableTemplateFactory) getTemplateContext()
                    .getTemplateFactory()).getTemplateDefinition(depTemplateName);
            if (depDefinition != null) {
                String definitionContent = buildTemplateDefinitionContent(depDefinition, depTemplateName);
                ZipEntry definitionEntry = new ZipEntry("definitions/" + depTemplateName + ".json");
                zos.putNextEntry(definitionEntry);
                zos.write(definitionContent.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }

            exportedTemplateNames.add(depTemplateName);

            // 递归导出间接依赖
            exportDependentTemplates(zos, depDefinition, exportedTemplateNames);
        }
    }

    /**
     * 构建组合模板配置内容
     */
    private String buildMultipleTemplateConfig(MultipleTemplateDefinition definition, MultipleTemplate multipleTemplate) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"templateName\": \"").append(multipleTemplate.getTemplateName()).append("\",\n");
        sb.append("  \"description\": \"组合模板导出\",\n");
        sb.append("  \"templateNames\": [");
        List<String> templateNames = multipleTemplate.getTemplates().stream()
                .map(Template::getTemplateName)
                .toList();
        sb.append(templateNames.stream()
                .map(name -> "\"" + name + "\"")
                .reduce((a, b) -> a + ", " + b)
                .orElse(""));
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * 构建模板定义内容
     */
    private String buildTemplateDefinitionContent(TemplateDefinition definition, String templateName) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"templateName\": \"").append(templateName).append("\",\n");
        sb.append("  \"description\": \"模板定义导出\",\n");
        sb.append("  \"dependTemplateDefinitionList\": [");
        Set<String> depTemplates = definition.getDependTemplates();
        if (CollUtil.isNotEmpty(depTemplates)) {
            sb.append(depTemplates.stream()
                    .map(name -> "\"" + name + "\"")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse(""));
        }
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * 构建导出元数据
     */
    private String buildExportMetadata(List<String> templateNames, Set<String> multipleTemplateNames) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\": \"1.0\",\n");
        sb.append("  \"exportTime\": \"").append(new java.util.Date()).append("\",\n");
        sb.append("  \"templateCount\": ").append(templateNames.size()).append(",\n");
        sb.append("  \"multipleTemplateCount\": ").append(multipleTemplateNames.size()).append(",\n");
        sb.append("  \"templateNames\": [");
        sb.append(templateNames.stream()
                .map(name -> "\"" + name + "\"")
                .reduce((a, b) -> a + ", " + b)
                .orElse(""));
        sb.append("],\n");
        sb.append("  \"multipleTemplateNames\": [");
        sb.append(multipleTemplateNames.stream()
                .map(name -> "\"" + name + "\"")
                .reduce((a, b) -> a + ", " + b)
                .orElse(""));
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }
}
