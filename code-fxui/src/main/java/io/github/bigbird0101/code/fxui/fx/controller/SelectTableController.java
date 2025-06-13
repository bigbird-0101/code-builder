package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.core.common.DbUtil;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.domain.DataSourceConfig;
import io.github.bigbird0101.code.core.event.TemplateListener;
import io.github.bigbird0101.code.fxui.event.DatasourceConfigUpdateEvent;
import io.github.bigbird0101.code.fxui.fx.bean.PageInputSnapshot;
import io.github.bigbird0101.code.util.Utils;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_USER_SAVE_TEMPLATE_CONFIG;

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
    private final Set<String> allTables = new HashSet<>();
    private final Set<String> allTablesSelect = new HashSet<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchField.textProperty().addListener((observable, old, newValue) -> {
            if (StrUtil.isNotBlank(newValue)) {
                Set<String> collect = allTables.stream().filter(s -> s.contains(newValue))
                        .collect(Collectors.toSet());
                initTableCheckBox(collect);
            } else {
                initTableCheckBox(allTables);
            }
        });
        getTemplateContext().addListener(new SelectTableController.DatasourceConfigUpdateEventListener());
        initData(this::initView);
    }

    public void initViewBeforeSetData(Set<String> tableDataSet) {
        StaticLog.info("initViewBeforeSetData {}", tableDataSet);
        allTablesSelect.clear();
        allTablesSelect.addAll(tableDataSet);
    }

    private void initView() {
        if (allTablesSelect.isEmpty()) {
            getTemplateContext().getEnvironment().consumerPropertyIfPresent(DEFAULT_USER_SAVE_TEMPLATE_CONFIG, PageInputSnapshot.class, pageInputSnapshot -> {
                String tableNames = pageInputSnapshot.getTableNames();
                if (Utils.isNotEmpty(tableNames)) {
                    String[] split = tableNames.split(",");
                    for (String splitTableName : split) {
                        for (String tableName : allTables) {
                            if (tableName.equals(splitTableName)) {
                                CheckBox checkBox = (CheckBox) tables.lookup("#" + splitTableName);
                                if (null != checkBox) {
                                    checkBox.setSelected(true);
                                }
                            }
                        }
                    }
                }
            });
        } else {
            for (String tableName : allTablesSelect) {
                CheckBox checkBox = (CheckBox) tables.lookup("#" + tableName);
                if (null != checkBox) {
                    checkBox.setSelected(true);
                }
            }
        }
    }


    private class DatasourceConfigUpdateEventListener extends TemplateListener<DatasourceConfigUpdateEvent> {
        @Override
        protected void onTemplateEvent(DatasourceConfigUpdateEvent doGetTemplateAfterEvent) {
            SelectTableController.this.initData(SelectTableController.this::initView);
        }
    }

    private void initData(Runnable runnable) {
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
                initTableCheckBox(new HashSet<>(allTableName));
                runnable.run();
            });
            task.setOnFailed(event -> StaticLog.error("initData 异步加载失败", task.getException()));
            new Thread(task).start();
        });
    }

    // 优化表名过滤逻辑
    private void initTableCheckBox(Set<String> tableNames) {
        // 获取当前已有的复选框集合
        ObservableList<Node> currentChildren = tables.getChildren();
        Set<String> currentTableNames = currentChildren.stream()
                .filter(node -> node instanceof CheckBox)
                .map(Node::getId)
                .collect(Collectors.toSet());

        // 计算需要添加和移除的表名
        Set<String> toAdd = new HashSet<>(tableNames);
        toAdd.removeAll(currentTableNames);

        Set<String> toRemove = new HashSet<>(currentTableNames);
        toRemove.removeAll(tableNames);

        // 批量设置不需要的复选框为不可见且不管理
        currentChildren.forEach(node -> {
            if (toRemove.contains(node.getId())) {
                node.setVisible(false); // 设置为不可见
                node.setManaged(false); // 释放布局空间
            }
        });

        // 批量添加新的复选框
        toAdd.forEach(tableName -> {
            // 检查是否已存在相同ID的节点
            if (currentChildren.stream().noneMatch(node -> tableName.equals(node.getId()))) {
                CheckBox checkBox = new CheckBox();
                checkBox.setId(tableName);
                checkBox.setText("_" + tableName);
                checkBox.setPadding(insets);
                tables.getChildren().add(checkBox);
            }
        });

        // 批量设置需要显示的复选框为可见且管理
        currentChildren.forEach(node -> {
            if (tableNames.contains(node.getId())) {
                node.setVisible(true); // 设置为可见
                node.setManaged(true); // 恢复布局管理
            }
        });
    }

    @FXML
    public void selectAll() {
        allTablesSelect.addAll(allTables);
        // 延迟刷新UI
        Platform.runLater(() -> refreshCheckBoxSelection(true));
    }

    @FXML
    public void clearAll() {
        allTablesSelect.clear();
        // 延迟刷新UI
        Platform.runLater(() -> refreshCheckBoxSelection(false));
    }

    // 新增方法：刷新复选框选中状态
    private void refreshCheckBoxSelection(boolean isSelected) {
        ObservableList<Node> children = tables.getChildren();
        children.forEach(node -> {
            if (node instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) node;
                checkBox.setSelected(isSelected);
            }
        });
    }

    public Set<String> getSelectTableNames() {
        ObservableList<Node> children = tables.getChildren();
        Set<String> result = new HashSet<>();
        children.forEach(s -> {
            CheckBox checkBox = (CheckBox) s;
            if (checkBox.isSelected()) {
                result.add(checkBox.getId());
            }
        });
        return result;
    }
}
