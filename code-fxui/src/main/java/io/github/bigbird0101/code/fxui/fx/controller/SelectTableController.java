package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;

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
    public TilePane tables;
    @FXML
    public HBox tableBox;
    @FXML
    public TextField searchField;
    private final Insets insets = new Insets(0, 10, 10, 0);
    private final Set<String> selectTableNames = new HashSet<>();
    private final Set<String> allTables = new HashSet<>();

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
        initData();
        initView();
    }

    private void initView() {
        String property = getTemplateContext().getEnvironment().getProperty(DEFAULT_USER_SAVE_TEMPLATE_CONFIG);
        if (Utils.isNotEmpty(property)) {
            final PageInputSnapshot pageInputSnapshot = JSONObject.parseObject(property, new TypeReference<PageInputSnapshot>() {
            });
            String tableNames = pageInputSnapshot.getTableNames();
            if (Utils.isNotEmpty(tableNames)) {
                String[] split = tableNames.split(",");
                for (String splitTableName : split) {
                    for (String tableName : allTables) {
                        if (tableName.equals(splitTableName)) {
                            CheckBox checkBox = (CheckBox) tables.lookup("#" + splitTableName);
                            checkBox.setSelected(true);
                        }
                    }
                }
            }
        }
    }


    private class DatasourceConfigUpdateEventListener extends TemplateListener<DatasourceConfigUpdateEvent> {
        @Override
        protected void onTemplateEvent(DatasourceConfigUpdateEvent doGetTemplateAfterEvent) {
            SelectTableController.this.initData();
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
                initTableCheckBox(new HashSet<>(allTableName));
            });
            task.setOnFailed(event -> StaticLog.error("initData 异步加载失败", task.getException()));
            new Thread(task).start();
        });
    }

    public void initTableCheckBox(Set<String> tableNames) {
        tables.getChildren().clear();
        tableNames.forEach(templateName -> {
            CheckBox checkBox = new CheckBox(templateName);
            checkBox.setText(templateName);
            checkBox.setPadding(insets);
            tables.getChildren().add(checkBox);
        });
    }

    public Set<String> getSelectTableNames() {
        ObservableList<Node> children = tables.getChildren();
        Set<String> result = new HashSet<>();
        children.forEach(s -> {
            CheckBox checkBox = (CheckBox) s;
            if (checkBox.isSelected()) {
                result.add(checkBox.getText());
            }
        });
        return result;
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
