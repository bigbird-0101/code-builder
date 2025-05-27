package io.github.bigbird0101.code.fxui.event;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.StaticLog;
import io.github.bigbird0101.code.core.event.TemplateListener;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.fxui.fx.controller.TemplatesOperateController;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * @author bigbird-0101
 * @version 1.0.0
 * @since 2022-09-21 22:21:19
 */
public class DoGetTemplateAfterListener extends TemplateListener<DoGetTemplateAfterEvent> {
    @Override
    protected void onTemplateEvent(DoGetTemplateAfterEvent doGetTemplateAfterEvent) {
        StaticLog.info("onTemplateContextEvent {}",doGetTemplateAfterEvent);
        //找到对应的模板checkbox
        final TemplatesOperateController templatesOperateController = doGetTemplateAfterEvent.getTemplatesOperateController();
        final Template template = doGetTemplateAfterEvent.getTemplate();
        if(ObjectUtil.isAllNotEmpty(templatesOperateController,template)) {
            Set<Node> nodes = templatesOperateController.getTemplates().lookupAll("#templateTitle");
            List<CheckBox> collect = nodes.stream().map(node -> (GridPane) node)
                    .map(anchorPane -> anchorPane.lookup("#templateName"))
                    .map(node -> (CheckBox) node)
                    .filter(checkBox -> checkBox.getUserData().equals(template.getTemplateName()))
                    .collect(toList());
            CheckBox checkBoxTarget = collect.stream().findFirst().orElse(null);
            if (null != checkBoxTarget) {
                GridPane gridPane = (GridPane) checkBoxTarget.getParent().getParent().lookup("#projectInfo");
                //重新设置模板值但不持久化
                templatesOperateController.doSetTemplate(template, gridPane);
            } else {
                StaticLog.warn("checkBoxTarget not get {}", template.getTemplateName());
            }
        }
    }
}
