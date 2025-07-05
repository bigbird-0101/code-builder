package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.share.MultipleTemplateDefinitionWrapper;
import io.github.bigbird0101.code.core.share.ShareClient;
import io.github.bigbird0101.code.core.share.TemplateDefinitionWrapper;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * @author fpp
 * @version 1.0
 * @since 2021/1/12 19:29
 */
public class ImportShareTemplateController extends AbstractTemplateContextProvider implements Initializable {
    public static final String CONTENT = "当前导入的模版包含依赖模版，是否同步导入依赖模版？";
    @FXML
    public TextArea url;
    @FXML
    public Button importTemplate;

    private static final String REPLACE = "替换";

    private final ShareClient shareClient = new ShareClient();
    private MainController mainController;

    private String oldTemplateName;

    private String multipleTemplateName;

    public void setReplaceButtonText() {
        this.importTemplate.setText(REPLACE);
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
                    AlertUtil.showWarning("地址不能为空");
                } else {
                    if (this.importTemplate.getText().equals(REPLACE)) {
                        String templateContent = shareClient.templateContent(urlText);
                        Template template = getTemplateContext().getTemplate(oldTemplateName);
                        try {
                            File file = template.getTemplateResource().getFile();
                            FileWriter.create(file).write(templateContent);
                            mainController.refreshTemplate();
                            AlertUtil.showInfo("替换成功");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (shareClient.isTemplateShareUrl(urlText)) {
                        if (StrUtil.isNotBlank(multipleTemplateName)) {
                            TemplateDefinitionWrapper templateDefinitionWrapper = shareClient.template(urlText);
                            if (!templateDefinitionWrapper.getDependTemplateDefinitionList().isEmpty() &&
                                    ButtonType.OK.getButtonData() == AlertUtil.showConfirm(CONTENT).getButtonData()) {
                                templateDefinitionWrapper.registerAndRefreshTemplate(multipleTemplateName);
                            } else {
                                templateDefinitionWrapper.setDependTemplateDefinitionList(Collections.emptyList());
                                templateDefinitionWrapper.registerAndRefreshTemplate(multipleTemplateName);
                            }
                        } else {
                            TemplateDefinitionWrapper templateDefinitionWrapper = shareClient.template(urlText);
                            if (!templateDefinitionWrapper.getDependTemplateDefinitionList().isEmpty() &&
                                    ButtonType.OK.getButtonData() == AlertUtil.showConfirm(CONTENT).getButtonData()) {
                                templateDefinitionWrapper.registerAndRefreshTemplate();
                            } else {
                                templateDefinitionWrapper.setDependTemplateDefinitionList(Collections.emptyList());
                                templateDefinitionWrapper.registerAndRefreshTemplate();
                            }
                        }
                        mainController.initMultipleTemplateViews();
                        AlertUtil.showInfo("导入成功");
                        Node source = (Node) event.getSource();
                        Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                    } else if (StrUtil.isNotBlank(multipleTemplateName) && shareClient.isMultipleTemplateShareUrl(urlText)) {
                        AlertUtil.showWarning("无法导入组合模版");
                    } else if (shareClient.isMultipleTemplateShareUrl(urlText)) {
                        MultipleTemplateDefinitionWrapper multipleTemplateDefinitionWrapper = shareClient.multipleTemplate(urlText);
                        multipleTemplateDefinitionWrapper.registerAndRefreshMultipleTemplate();
                        mainController.initMultipleTemplateViews();
                        AlertUtil.showInfo("导入成功");
                        Node source = (Node) event.getSource();
                        Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                    } else {
                        AlertUtil.showWarning("无法解析的地址");
                    }
                }
            } catch (Exception exception) {
                AlertUtil.showError("操作失败 错误信息:" + exception.getMessage());
            }
        });
    }

    public void setComplexController(MainController mainController) {
        this.mainController = mainController;
    }
}
