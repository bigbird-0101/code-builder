package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.util.ObjectUtil;
import io.github.bigbird0101.code.core.config.AbstractEnvironment;
import io.github.bigbird0101.code.core.config.FileUrlResource;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.factory.DefaultListableTemplateFactory;
import io.github.bigbird0101.code.core.factory.RootTemplateDefinition;
import io.github.bigbird0101.code.core.factory.config.TemplateDefinition;
import io.github.bigbird0101.code.core.template.HaveDependTemplate;
import io.github.bigbird0101.code.core.template.MultipleTemplate;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.core.template.targetfile.PatternTargetFilePrefixNameStrategy;
import io.github.bigbird0101.code.core.template.targetfile.TargetFilePrefixNameStrategy;
import io.github.bigbird0101.code.core.template.targetfile.TemplateFilePrefixNameStrategyFactory;
import io.github.bigbird0101.code.fxui.common.AlertUtil;
import io.github.bigbird0101.code.util.ClassUtil;
import io.github.bigbird0101.code.util.Utils;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH;
import static io.github.bigbird0101.code.core.config.AbstractEnvironment.DEFAULT_TEMPLATE_FILE_SUFFIX;

/**
 * @author Administrator
 */
public class AbstractTemplateController extends AbstractTemplateContextProvider implements Initializable {
    private static final Logger logger = LogManager.getLogger(AbstractTemplateController.class);

    @FXML
    TextArea projectUrl;
    @FXML
    TextField moduleName;
    @FXML
    TextArea srcPackageName;
    @FXML
    TextField templateName;
    @FXML
    TextField fileSuffixName;
    @FXML
    FlowPane filePrefixNameStrategy;
    @FXML
    TextField sourcesRootName;
    @FXML
    Label fileName;
    @FXML
    VBox vBox;
    @FXML
    TextField filePrefixNameStrategyPattern;
    @FXML
    FlowPane filePrefixNameStrategyPane;
    @FXML
    Button button;
    @FXML
    CheckComboBox<String> depends;
    @FXML
    ComboBox<String> selectTemplateClassName;

    private Integer filePrefixNameStrategyValue;
    private File file;
    private final Insets insets = new Insets(0, 10, 10, 0);
    private String sourceTemplateName;
    private ComplexController complexController;
    private final Set<String> selectDepends = new LinkedHashSet<>();
    /**
     * 0-修改模式 1-添加模式
     */
    private int mode = 1;

    public AbstractTemplateController() {
    }

    public void setSourceTemplateName(String sourceTemplateName) {
        this.sourceTemplateName = sourceTemplateName;
    }

    public void setComplexController(ComplexController complexController) {
        this.complexController = complexController;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceLoader<TargetFilePrefixNameStrategy> load = ServiceLoader.load(TargetFilePrefixNameStrategy.class);
        for (TargetFilePrefixNameStrategy next : load) {
            RadioButton radioButton = new RadioButton(String.valueOf(next.getTypeValue()));
            radioButton.setPadding(insets);
            filePrefixNameStrategy.getChildren().add(radioButton);
        }
        ToggleGroup filePrefixNameStrategyToggleGroup = new ToggleGroup();
        filePrefixNameStrategy.getChildren().forEach(node -> {
            RadioButton radioButton = (RadioButton) node;
            radioButton.setToggleGroup(filePrefixNameStrategyToggleGroup);
        });
        filePrefixNameStrategyToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton radioButton = (RadioButton) newValue;
            this.filePrefixNameStrategyValue = Integer.valueOf(radioButton.getText());
            filePrefixNameStrategyPane.setVisible(filePrefixNameStrategyValue == 3);
        });
        ArrayList<Class<?>> allClassByInterface = ClassUtil.getAllClassByInterface(Template.class);
        List<String> collect = allClassByInterface.stream().map(Class::getName).collect(Collectors.toList());
        selectTemplateClassName.getItems().addAll(collect);
        selectDepends.clear();
        depends.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    selectDepends.addAll(change.getAddedSubList());
                } else if (change.wasRemoved()) {
                    change.getRemoved().forEach(selectDepends::remove);
                }
            }
        });
    }

    @FXML
    public void selectTemplateFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Template files (*.template)", "*.template");
        fileChooser.getExtensionFilters().add(extFilter);
        this.file = fileChooser.showOpenDialog(vBox.getScene().getWindow());
        if (null != this.file) {
            fileName.setText(this.file.getName());
        }
    }

    @FXML
    public void createTemplate() {
        if (!check()) {
            return;
        }
        GenericTemplateContext templateContext = (GenericTemplateContext) getTemplateContext();
        DefaultListableTemplateFactory defaultListableTemplateFactory = templateContext.getTemplateFactory();
        try {
            if (mode != 1) {
                if (!templateName.getText().equals(sourceTemplateName)
                        &&templateContext.getTemplateNames().contains(templateName.getText())) {
                    AlertUtil.showError("模板名已存在");
                    return;
                }
                defaultListableTemplateFactory.removeTemplate(sourceTemplateName);
            }
            buildTemplate(templateContext);
            if(mode!=1){
                //刷新父模板
                Template newTemplate = templateContext.getTemplate(templateName.getText());
                TreeItem<Label> root = complexController.listViewTemplate.getRoot();
                for(String multipleTemplateName:templateContext.getMultipleTemplateNames()){
                    MultipleTemplate multipleTemplate = defaultListableTemplateFactory.getMultipleTemplate(multipleTemplateName);
                    if(multipleTemplate.getTemplates().stream().anyMatch(template -> template.getTemplateName().equals(sourceTemplateName))) {
                        Set<Template> templates = multipleTemplate.getTemplates();
                        templates.removeIf(oldTemplate -> oldTemplate.getTemplateName().equals(sourceTemplateName));
                        templates.add(newTemplate);
                        multipleTemplate.setTemplates(templates);
                        defaultListableTemplateFactory.refreshMultipleTemplate(multipleTemplate);
                        root.getChildren()
                                .stream()
                                .filter(labelTreeItem -> labelTreeItem.getValue().getText().equals(multipleTemplate.getTemplateName()))
                                .findFirst()
                                .ifPresent(s -> s.getChildren()
                                        .filtered(v -> v.getValue().getText().equals(sourceTemplateName))
                                        .stream()
                                        .findFirst()
                                        .ifPresent(d -> {
                                            int i = s.getChildren().indexOf(d);
                                            final TreeItem<Label> andInitTemplateView = complexController
                                                    .getAndInitTemplateView(newTemplate, multipleTemplate.getTemplateName(), s);
                                            s.getChildren().set(i, andInitTemplateView);
                                        }));
                    }
                }
            }
            if(null!=complexController) {
                complexController.doSelectMultiple();
            }
            AlertUtil.showInfo("Success!");
            ((Stage)vBox.getScene().getWindow()).close();
        } catch (Exception e) {
            logger.error("create template error",e);
            AlertUtil.showError("failed:" + e.getMessage());
        }
    }

    private void buildTemplate(GenericTemplateContext templateContext) throws IOException {
        DefaultListableTemplateFactory defaultListableTemplateFactory = templateContext.getTemplateFactory();
        TemplateFilePrefixNameStrategyFactory templateFilePrefixNameStrategyFactory = new TemplateFilePrefixNameStrategyFactory();
        TemplateDefinition templateDefinition = templateContext.getTemplateDefinition(sourceTemplateName);
        boolean isNotHave = null == templateDefinition;
        RootTemplateDefinition rootTemplateDefinition =isNotHave?new RootTemplateDefinition(): (RootTemplateDefinition) templateDefinition;
        TargetFilePrefixNameStrategy targetFilePrefixNameStrategy = templateFilePrefixNameStrategyFactory
                .getTemplateFilePrefixNameStrategy(ObjectUtil.defaultIfNull(filePrefixNameStrategyValue, 1));
        if(targetFilePrefixNameStrategy instanceof PatternTargetFilePrefixNameStrategy){
            PatternTargetFilePrefixNameStrategy patternTemplateFilePrefixNameStrategy= (PatternTargetFilePrefixNameStrategy) targetFilePrefixNameStrategy;
            patternTemplateFilePrefixNameStrategy.setPattern(filePrefixNameStrategyPattern.getText());
        }
        rootTemplateDefinition.setTemplateClassName(selectTemplateClassName.getSelectionModel().getSelectedItem());
        rootTemplateDefinition.setTargetFilePrefixNameStrategy(targetFilePrefixNameStrategy);
        rootTemplateDefinition.setTargetFileSuffixName(fileSuffixName.getText());
        rootTemplateDefinition.setModule(moduleName.getText());
        rootTemplateDefinition.setProjectUrl(projectUrl.getText());
        rootTemplateDefinition.setSourcesRoot(sourcesRootName.getText());
        rootTemplateDefinition.setSrcPackage(srcPackageName.getText());
        String templateNameText = templateName.getText();
        LinkedHashSet<String> dependTemplates = new LinkedHashSet<>(selectDepends);
        logger.info("templateName {} dependTemplate {}",templateNameText,dependTemplates);
        rootTemplateDefinition.setDependTemplates(dependTemplates);
        String newFileName = getTemplateContext().getEnvironment()
                .getProperty(DEFAULT_CORE_TEMPLATE_FILES_PATH) + File.separator +
                templateNameText + DEFAULT_TEMPLATE_FILE_SUFFIX;
        File newFile = new File(newFileName);
        if(!this.file.equals(newFile)) {
            FileUtils.copyFile(this.file, newFile);
        }
        rootTemplateDefinition.setTemplateResource(new FileUrlResource(newFile.getAbsolutePath()));
        AbstractEnvironment.putTemplateContent(newFile.getAbsolutePath(), IOUtils.toString(Files.newInputStream(newFile.toPath()), StandardCharsets.UTF_8));
        if(isNotHave||!templateNameText.equals(sourceTemplateName)) {
            templateContext.registerTemplateDefinition(templateNameText, rootTemplateDefinition);
            if(!templateNameText.equals(sourceTemplateName)){
                defaultListableTemplateFactory.removeTemplateDefinition(sourceTemplateName);
            }
        }else{
            templateContext.registerTemplateDefinition(templateNameText, rootTemplateDefinition);
        }
        defaultListableTemplateFactory.preInstantiateTemplates();
        defaultListableTemplateFactory.refreshTemplate(templateContext.getTemplate(templateNameText));

        //修改模板名也要修改此时依赖此模板的所依赖的模板名
        HaveDependTemplate.updateDependTemplate(defaultListableTemplateFactory, sourceTemplateName, templateNameText);
    }

    public boolean check() {
        if (Utils.isEmpty(templateName.getText())) {
            AlertUtil.showError("请输入模板名");
            return false;
        }
        if (selectTemplateClassName.getSelectionModel().isEmpty()) {
            AlertUtil.showError("请选择模板实现类");
            return false;
        }
        if (null == file) {
            AlertUtil.showError("请输入模板文件");
            return false;
        }
        if (mode==1&&getTemplateContext().getTemplateNames().contains(templateName.getText())) {
            AlertUtil.showError("该模板名已存在,请填写其他模板名!");
            return false;
        }
        try {
            if (Class.forName(selectTemplateClassName.getSelectionModel().getSelectedItem()).newInstance()
                    instanceof HaveDependTemplate && depends.getCheckModel().getCheckedItems().isEmpty()) {
                AlertUtil.showError("该模板为依赖型模板,请填写依赖模板名!");
                return false;
            }
        } catch (Exception e) {
            logger.error(e);
        }


        if (!depends.getCheckModel().getCheckedItems().isEmpty()) {
            final List<String> collect = depends.getCheckModel().getCheckedItems();
            for(String dependTemplateName:collect){
                if(!getTemplateContext().getTemplateNames().contains(dependTemplateName)){
                    AlertUtil.showError(String.format("所依赖的%s模板不存在",dependTemplateName));
                    return false;
                }
            }
        }
        return true;
    }
}