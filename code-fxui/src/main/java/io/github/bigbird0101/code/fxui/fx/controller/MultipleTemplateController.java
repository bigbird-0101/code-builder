package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.context.aware.TemplateContextProvider;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.GenericMultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.RootTemplateDefinition;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.fxui.CodeBuilderApplication;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import io.github.bigbird0101.code.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Administrator
 */
public class MultipleTemplateController extends TemplateContextProvider implements Initializable {
    @FXML
    public TextField searchField;
    @FXML
    FlowPane templates;
    private final Insets insets=new Insets(0,10,10,0);
    private final Set<String> selectTemplateNames=new HashSet<>();
    @FXML
    TextField multipleTemplateName;
    @FXML
    AnchorPane anchorPane;
    TreeView<Label> listViewTemplate;
    @FXML
    Button button;
    @FXML
    TextArea projectUrl;

    private String sourceMultipleTemplateName;

    /**
     * 0-修改模式 1-添加模式
     */
    private int mode=1;

    public void setListViewTemplate(TreeView<Label> listViewTemplate) {
        this.listViewTemplate = listViewTemplate;
    }

    public void setSourceMultipleTemplateName(String sourceMultipleTemplateName) {
        this.sourceMultipleTemplateName = sourceMultipleTemplateName;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private ComplexController complexController;

    public TextField getMultipleTemplateName() {
        return multipleTemplateName;
    }

    public Button getButton() {
        return button;
    }

    public FlowPane getTemplates() {
        return templates;
    }

    public void setComplexController(ComplexController complexController) {
        this.complexController = complexController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTemplateCheckBox();
        searchField.textProperty().addListener((observable,old,newValue)->{
            if(StrUtil.isNotBlank(newValue)){
                Set<String> templateNames = getTemplateContext().getTemplateNames();
                Set<String> collect =
                        templateNames.stream()
                                .filter(s -> s.contains(newValue))
                                .collect(Collectors.toSet());
                initTemplateCheckBox(collect);
            }else{
                initTemplateCheckBox();
            }
        });
    }

    @FXML
    public void createMultipleTemplate() throws CodeConfigException {
        if(Utils.isEmpty(multipleTemplateName.getText())){
            AlertUtil.showWarning("请填写组合模板名");
            return;
        }
        if(mode==1&&getTemplateContext().getMultipleTemplateNames().contains(multipleTemplateName.getText())){
            AlertUtil.showWarning("该模板名已有，请填写其他模板名");
            return;
        }
        if(selectTemplateNames.isEmpty()){
            AlertUtil.showWarning("请选择模板");
            return;
        }
        GenericTemplateContext genericTemplateContext = (GenericTemplateContext) getTemplateContext();
        DefaultListableTemplateFactory defaultListableTemplateFactory = genericTemplateContext.getTemplateFactory();
        if (mode != 1) {
            defaultListableTemplateFactory.removeMultipleTemplate(sourceMultipleTemplateName);
        }
        buildNewMultipleTemplate(genericTemplateContext);
        //刷新组合模板ListView页面
        listViewTemplate.getRoot().getChildren().stream()
                .filter(labelTreeItem -> labelTreeItem.getValue().getText().equals(sourceMultipleTemplateName))
                .findFirst().ifPresent(labelTreeItemOld->{
            final TreeItem<Label> labelTreeItemNew = complexController.initMultipleTemplateView(multipleTemplateName.getText(), listViewTemplate.getRoot());
            final int i = listViewTemplate.getRoot().getChildren().indexOf(labelTreeItemOld);
            listViewTemplate.getRoot().getChildren().set(i,labelTreeItemNew);
        });
        AlertUtil.showInfo("Success!");
        ((Stage)anchorPane.getScene().getWindow()).close();
        if(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected().equals(multipleTemplateName.getText())||
                sourceMultipleTemplateName.equals(CodeBuilderApplication.USER_OPERATE_CACHE.getTemplateNameSelected())){
            CodeBuilderApplication.USER_OPERATE_CACHE.setTemplateNameSelected(multipleTemplateName.getText());
            complexController.doSelectMultiple();
        }
    }

    /**
     * 创建新的组合模板
     * @param genericTemplateContext 模板容器
     * @throws CodeConfigException
     */
    private void buildNewMultipleTemplate(GenericTemplateContext genericTemplateContext) throws CodeConfigException {
        DefaultListableTemplateFactory defaultListableTemplateFactory = genericTemplateContext.getTemplateFactory();
        boolean isNotHave = null == genericTemplateContext.getMultipleTemplateDefinition(sourceMultipleTemplateName);
        GenericMultipleTemplateDefinition genericMultipleTemplateDefinition =isNotHave?
        new GenericMultipleTemplateDefinition(): (GenericMultipleTemplateDefinition) genericTemplateContext.getMultipleTemplateDefinition(sourceMultipleTemplateName);
        genericMultipleTemplateDefinition.setTemplateNames(selectTemplateNames);
        if(isNotHave||!multipleTemplateName.getText().equals(sourceMultipleTemplateName)) {
            genericTemplateContext.registerMultipleTemplateDefinition(multipleTemplateName.getText(), genericMultipleTemplateDefinition);
            if(!multipleTemplateName.getText().equals(sourceMultipleTemplateName)){
                defaultListableTemplateFactory.removeMultipleTemplateDefinition(sourceMultipleTemplateName);
            }
        }
        if(Utils.isNotEmpty(projectUrl.getText())){
            selectTemplateNames.forEach(templateName->{
                RootTemplateDefinition templateDefinition = (RootTemplateDefinition)genericTemplateContext.getTemplateDefinition(templateName);
                templateDefinition.setProjectUrl(projectUrl.getText());
                defaultListableTemplateFactory.removeTemplate(templateName);
            });
        }
        defaultListableTemplateFactory.preInstantiateTemplates();
        final MultipleTemplate multipleTemplate = genericTemplateContext.getMultipleTemplate(multipleTemplateName.getText());
        defaultListableTemplateFactory.refreshMultipleTemplate(multipleTemplate);
        multipleTemplate.getTemplates().forEach(defaultListableTemplateFactory::refreshTemplate);
    }

    private void initTemplateCheckBox() {
        initTemplateCheckBox(getTemplateContext().getTemplateNames());
    }

    private void initTemplateCheckBox(Set<String> templateNames) {
        templates.getChildren().clear();
        templateNames.forEach(templateName->{
            CheckBox checkBox = new CheckBox(templateName);
            checkBox.setText(templateName);
            checkBox.setPadding(insets);
            templates.getChildren().add(checkBox);
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue){
                    selectTemplateNames.add(templateName);
                }else{
                    selectTemplateNames.remove(templateName);
                }
            });
        });
    }

    @FXML
    public void selectAll(ActionEvent actionEvent) {
        templates.getChildren().forEach(checkbox->{
            CheckBox checkBox= (CheckBox) checkbox;
            checkBox.setSelected(true);
        });
    }

    @FXML
    public void clearAll(ActionEvent actionEvent) {
        templates.getChildren().forEach(checkbox->{
            CheckBox checkBox= (CheckBox) checkbox;
            checkBox.setSelected(false);
        });
    }
}