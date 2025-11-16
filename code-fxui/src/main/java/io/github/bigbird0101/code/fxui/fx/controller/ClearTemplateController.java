package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.config.AbstractEnvironment;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bigbird-0101
 * @date 2025-07-28 23:00
 */
public class ClearTemplateController extends AbstractTemplateContextProvider implements Initializable {
    public TextField searchField;
    public HBox templateBox;
    public FlowPane templates;
    private final Insets insets = new Insets(0, 10, 10, 0);
    private final Set<String> selectTemplateNames = new HashSet<>();
    public AnchorPane anchorPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTemplateCheckBox();
        searchField.textProperty().addListener((_, _, newValue) -> {
            if (StrUtil.isNotBlank(newValue)) {
                Set<String> templateNames = getTemplateContext().getTemplateNames();
                Set<String> collect =
                        templateNames.stream()
                                .filter(s -> s.contains(newValue))
                                .collect(Collectors.toSet());
                initTemplateCheckBox(collect);
            } else {
                initTemplateCheckBox();
            }
        });
    }

    private void initTemplateCheckBox() {
        initTemplateCheckBox(getTemplateContext().getTemplateNames());
    }

    private void initTemplateCheckBox(Set<String> templateNames) {
        templates.getChildren().clear();
        templateNames.forEach(templateName -> {
            CheckBox checkBox = new CheckBox(templateName);
            checkBox.setText(templateName);
            checkBox.setPadding(insets);
            templates.getChildren().add(checkBox);
            checkBox.selectedProperty().addListener((_, _, newValue) -> {
                if (newValue) {
                    selectTemplateNames.add(templateName);
                } else {
                    selectTemplateNames.remove(templateName);
                }
            });
        });
    }

    public void delete(ActionEvent actionEvent) {
        selectTemplateNames.forEach(this::delete);
        AlertUtil.showInfo("Success!");
        initTemplateCheckBox();
    }

    private void delete(String templateName) {
        DefaultListableTemplateFactory defaultListableTemplateFactory = (DefaultListableTemplateFactory) getTemplateContext().getTemplateFactory();
        defaultListableTemplateFactory.removeTemplateDefinition(templateName);
        defaultListableTemplateFactory.removeTemplate(templateName);
        if (getTemplateContext().getEnvironment() instanceof AbstractEnvironment abstractEnvironment) {
            File templateDirectory = abstractEnvironment.getTemplateDirectory();
            String path = templateDirectory.getPath() + File.separator + templateName + AbstractEnvironment.DEFAULT_TEMPLATE_FILE_SUFFIX;
            File templateFile = new File(path);
            if (templateFile.exists()) {
                templateFile.delete();
            }
        }
    }

    public void clearUnRefer(ActionEvent actionEvent) {
        Set<String> templateNames = getTemplateContext().getTemplateNames();
        Set<String> templateNamesUse = getTemplateContext().getMultipleTemplateNames()
                .stream()
                .flatMap(s -> getTemplateContext().getMultipleTemplate(s).getTemplates().stream().map(Template::getTemplateName))
                .collect(Collectors.toSet());
        Set<String> collect = templateNames.stream()
                .filter(templateName -> !templateNamesUse.contains(templateName))
                .collect(Collectors.toSet());
        collect.forEach(this::delete);
        AlertUtil.showInfo("Success!");
        initTemplateCheckBox();
    }
}
