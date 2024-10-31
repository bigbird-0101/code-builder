package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import io.github.bigbird0101.code.core.config.AbstractEnvironment;
import io.github.bigbird0101.code.core.config.FileUrlResource;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.exception.CodeConfigException;
import io.github.bigbird0101.code.core.factory.AbstractTemplateDefinition;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.GenericMultipleTemplateDefinition;
import io.github.bigbird0101.code.core.factory.RootTemplateDefinition;
import io.github.bigbird0101.code.core.template.HaveDependTemplate;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import io.github.bigbird0101.code.util.Utils;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH;
import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_TEMPLATE_FILE_SUFFIX;
import static io.github.bigbird0101.code.fxui.CodeBuilderApplication.USER_OPERATE_CACHE;

/**
 * @author Administrator
 */
public class MultipleAbstractTemplateController extends AbstractTemplateContextProvider implements Initializable {
    @FXML
    public TextField searchField;
    @FXML
    public HBox templateBox;
    @FXML
    public HBox module;
    @FXML
    public TextField moduleNameOld;
    @FXML
    public TextField moduleNameNew;
    @FXML
    public Button replaceModule;
    @FXML
    public HBox sourcesRootName;
    @FXML
    public TextField sourcesRootNameOld;
    @FXML
    public TextField sourcesRootNameNew;
    @FXML
    public Button replaceSourcesRootName;
    @FXML
    public HBox srcPackageName;
    @FXML
    public TextField srcPackageNameOld;
    @FXML
    public Button replaceSrcPackageName;
    @FXML
    public TextField srcPackageNameNew;
    @FXML
    public HBox templateName;
    @FXML
    public TextField templateNameOld;
    @FXML
    public TextField templateNameNew;
    @FXML
    public Button replaceTemplateName;
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

    public HBox getModule() {
        return module;
    }

    public HBox getTemplateName() {
        return templateName;
    }

    public HBox getSourcesRootName() {
        return sourcesRootName;
    }

    public HBox getSrcPackageName() {
        return srcPackageName;
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
    public void createOrUpdateMultipleTemplate() throws CodeConfigException {
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
        doRefreshMainView();
    }

    private void doRefreshMainView() {
        AlertUtil.showInfo("Success!");
        ((Stage) anchorPane.getScene().getWindow()).close();
        if (StrUtil.isBlank(USER_OPERATE_CACHE.getTemplateNameSelected())) {
            USER_OPERATE_CACHE.setTemplateNameSelected(multipleTemplateName.getText());
        } else if (multipleTemplateName.getText().equals(USER_OPERATE_CACHE.getTemplateNameSelected()) ||
                (StrUtil.isNotBlank(sourceMultipleTemplateName) && sourceMultipleTemplateName.equals(USER_OPERATE_CACHE.getTemplateNameSelected()))) {
            USER_OPERATE_CACHE.setTemplateNameSelected(multipleTemplateName.getText());
        }
        complexController.initMultipleTemplateViews();
        complexController.doSelectMultiple();
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
    public void selectAll() {
        templates.getChildren().forEach(checkbox->{
            CheckBox checkBox= (CheckBox) checkbox;
            checkBox.setSelected(true);
        });
    }

    @FXML
    public void clearAll() {
        templates.getChildren().forEach(checkbox->{
            CheckBox checkBox= (CheckBox) checkbox;
            checkBox.setSelected(false);
        });
    }

    @FXML
    public void replaceModule() {
        String newModule = moduleNameNew.getText();
        String oldModule = moduleNameOld.getText();
        if(StrUtil.hasBlank(newModule,oldModule)){
            AlertUtil.showWarning("替换的模块名,新旧都不能为空");
            return;
        }
        MultipleTemplate multipleTemplate = getMultipleTemplate();
        multipleTemplate.getTemplates().forEach(s->{
            String moduleSrc = s.getModule();
            moduleSrc=StrUtil.replace(moduleSrc,oldModule,newModule);
            s.setModule(moduleSrc);
            DefaultListableTemplateFactory operateTemplateBeanFactory = (DefaultListableTemplateFactory)
                    getTemplateContext().getTemplateFactory();
            operateTemplateBeanFactory.refreshTemplate(s);
        });
        doRefreshMainView();
    }

    private MultipleTemplate getMultipleTemplate() {
        String templateNameSelected = USER_OPERATE_CACHE.getTemplateNameSelected();
        return getTemplateContext().getMultipleTemplate(templateNameSelected);
    }

    @FXML
    public void replaceSourcesRootName() {
        String newSourcesRootName = sourcesRootNameNew.getText();
        String oldSourcesRootName = sourcesRootNameOld.getText();
        if(StrUtil.hasBlank(newSourcesRootName,oldSourcesRootName)){
            AlertUtil.showWarning("替换的源码包根路径名,新旧都不能为空");
            return;
        }
        MultipleTemplate multipleTemplate = getMultipleTemplate();
        multipleTemplate.getTemplates().forEach(s->{
            String sourcesRoot = s.getSourcesRoot();
            sourcesRoot=StrUtil.replace(sourcesRoot,oldSourcesRootName,newSourcesRootName);
            s.setSourcesRoot(sourcesRoot);
            DefaultListableTemplateFactory operateTemplateBeanFactory = (DefaultListableTemplateFactory)
                    getTemplateContext().getTemplateFactory();
            operateTemplateBeanFactory.refreshTemplate(s);
        });
        doRefreshMainView();
    }

    @FXML
    public void replaceSrcPackageName() {
        String newSrcPackageName = srcPackageNameNew.getText();
        String oldSrcPackageName = srcPackageNameOld.getText();
        if(StrUtil.hasBlank(newSrcPackageName,oldSrcPackageName)){
            AlertUtil.showWarning("替换的源码包名,新旧都不能为空");
            return;
        }
        MultipleTemplate multipleTemplate = getMultipleTemplate();
        multipleTemplate.getTemplates().forEach(s->{
            String srcPackage = s.getSrcPackage();
            srcPackage=StrUtil.replace(srcPackage,oldSrcPackageName,newSrcPackageName);
            s.setSrcPackage(srcPackage);
            DefaultListableTemplateFactory operateTemplateBeanFactory = (DefaultListableTemplateFactory)
                    getTemplateContext().getTemplateFactory();
            operateTemplateBeanFactory.refreshTemplate(s);
        });
        doRefreshMainView();
    }

    @FXML
    public void replaceTemplateName() {
        String templateNameOldStr = templateNameOld.getText();
        String templateNameNewText = Optional.ofNullable(templateNameNew.getText()).orElse("");
        if (StrUtil.hasBlank(templateNameOldStr)) {
            AlertUtil.showWarning("旧的模版名不能为空");
            return;
        }
        MultipleTemplate multipleTemplate = getMultipleTemplate();
        Set<Template> newTemplates = new LinkedHashSet<>(multipleTemplate.getTemplates());
        DefaultListableTemplateFactory operateTemplateBeanFactory = (DefaultListableTemplateFactory)
                getTemplateContext().getTemplateFactory();
        multipleTemplate.getTemplates().forEach(t -> {
            String templateNameSrc = t.getTemplateName();
            String templateNameNewSrc = StrUtil.replace(templateNameSrc, templateNameOldStr, templateNameNewText);
            if (!templateNameNewSrc.equals(templateNameSrc)) {
                newTemplates.remove(t);
                operateTemplateBeanFactory.removeTemplate(templateNameSrc);
                AbstractTemplateDefinition templateDefinition = (AbstractTemplateDefinition) operateTemplateBeanFactory.getTemplateDefinition(templateNameSrc);
                operateTemplateBeanFactory.removeTemplateDefinition(templateNameSrc);
                try {
                    File oldFile = t.getTemplateResource().getFile();
                    AbstractTemplateDefinition fromClone = (AbstractTemplateDefinition) templateDefinition.clone();
                    String newFileName = getTemplateContext().getEnvironment()
                            .getProperty(DEFAULT_CORE_TEMPLATE_FILES_PATH) + File.separator +
                            templateNameNewSrc + DEFAULT_TEMPLATE_FILE_SUFFIX;
                    File newFile = new File(newFileName);
                    if (!oldFile.equals(newFile)) {
                        FileUtils.copyFile(oldFile, newFile);
                    }
                    fromClone.setTemplateResource(new FileUrlResource(newFile.getAbsolutePath()));
                    AbstractEnvironment.putTemplateContent(newFile.getAbsolutePath(), IOUtils.toString(Files.newInputStream(newFile.toPath()), StandardCharsets.UTF_8));
                    operateTemplateBeanFactory.registerTemplateDefinition(templateNameNewSrc, fromClone);
                    operateTemplateBeanFactory.preInstantiateTemplates();
                    Template template = getTemplateContext().getTemplate(templateNameNewSrc);
                    newTemplates.add(template);
                    operateTemplateBeanFactory.refreshTemplate(template);

                    //修改模板名也要修改此时依赖此模板的所依赖的模板名
                    HaveDependTemplate.updateDependTemplate(operateTemplateBeanFactory, templateNameSrc, templateNameNewSrc);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        multipleTemplate.setTemplates(newTemplates);
        operateTemplateBeanFactory.refreshMultipleTemplate(multipleTemplate);
        doRefreshMainView();
    }


}