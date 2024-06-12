package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.share.MultipleTemplateDefinitionWrapper;
import io.github.bigbird0101.code.core.share.ShareClient;
import io.github.bigbird0101.code.core.share.TemplateDefinitionWrapper;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fpp
 * @version 1.0
 * @since 2021/1/12 19:29
 */
public class ImportShareTemplateController implements Initializable {
    @FXML
    public TextArea url;
    @FXML
    public Button copy;

    private final ShareClient shareClient = new ShareClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        copy.setOnAction(event -> {
            String urlText = url.getText();
            if (StrUtil.isBlank(urlText)) {
                AlertUtil.showWarning("地址不能为空");
            } else {
                if (shareClient.isTemplateShareUrl(urlText)) {
                    TemplateDefinitionWrapper templateDefinitionWrapper = shareClient.template(urlText);
                    templateDefinitionWrapper.registerAndRefreshTemplate();
                } else if (shareClient.isMultipleTemplateShareUrl(urlText)) {
                    MultipleTemplateDefinitionWrapper multipleTemplateDefinitionWrapper = shareClient.multipleTemplate(urlText);
                    multipleTemplateDefinitionWrapper.registerAndRefreshMultipleTemplate();
                } else {
                    AlertUtil.showWarning("无法解析的地址");
                }
            }
        });
    }
}
