package io.github.bigbird0101.code.fxui.fx.controller;

import io.github.bigbird0101.code.fxui.common.TooltipUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fpp
 * @version 1.0
 * @since 2021/1/12 19:29
 */
public class ShareController implements Initializable {
    @FXML
    public TextArea url;
    @FXML
    public Button copy;

    public void setUrl(String url) {
        this.url.setText(url);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        copy.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();//创建剪贴板内容
            content.putString(url.getText());//剪贴板内容对象中添加上文定义的图片
            clipboard.setContent(content);
            TooltipUtil.showToast("模板共享地址已复制");
        });
    }
}
