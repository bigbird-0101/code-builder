package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bigbird-0101
 * @date 2025-05-25 22:28
 */
public class SelectTableController extends AbstractTemplateContextProvider implements Initializable {
    @FXML
    public FlowPane tables;
    @FXML
    public HBox tableBox;
    @FXML
    public TextField searchField;
    private final Insets insets = new Insets(0, 10, 10, 0);
    private final Set<String> tableNames = new HashSet<>();
    private final Set<String> selectTableNames = new HashSet<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchField.textProperty().addListener((observable, old, newValue) -> {
            if (StrUtil.isNotBlank(newValue)) {
                Set<String> templateNames = getTemplateContext().getTemplateNames();
                Set<String> collect =
                        templateNames.stream()
                                .filter(s -> s.contains(newValue))
                                .collect(Collectors.toSet());
                initTableCheckBox(collect);
            } else {
                initTemplateCheckBox();
            }
        });
    }

    public void setTables(Set<String> tableNames) {
        tableNames.addAll(tableNames);
    }

    public void initTableCheckBox(Set<String> tableNames) {
        tables.getChildren().clear();
        tableNames.forEach(templateName -> {
            CheckBox checkBox = new CheckBox(templateName);
            checkBox.setText(templateName);
            checkBox.setPadding(insets);
            tables.getChildren().add(checkBox);
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectTableNames.add(templateName);
                } else {
                    selectTableNames.remove(templateName);
                }
            });
        });
    }

    @FXML
    public void selectAll() {
        tables.getChildren().forEach(checkbox -> {
            CheckBox checkBox = (CheckBox) checkbox;
            checkBox.setSelected(true);
        });
    }

    @FXML
    public void clearAll() {
        tables.getChildren().forEach(checkbox -> {
            CheckBox checkBox = (CheckBox) checkbox;
            checkBox.setSelected(false);
        });
    }
}
