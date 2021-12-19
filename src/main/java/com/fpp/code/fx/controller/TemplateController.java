package com.fpp.code.fx.controller;
import com.fpp.code.common.AlertUtil;
import com.fpp.code.common.Utils;
import com.fpp.code.core.config.AbstractEnvironment;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.core.factory.DefaultListableTemplateFactory;
import com.fpp.code.core.factory.GenericTemplateDefinition;
import com.fpp.code.core.template.*;
import com.fpp.code.core.context.aware.TemplateContextProvider;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author Administrator
 */
public class TemplateController extends TemplateContextProvider implements Initializable {
    private static Logger logger= LogManager.getLogger(TemplateController.class);

    @FXML
    public TextArea projectUrl;
    @FXML
    public TextField moduleName;
    @FXML
    public TextField srcPackageName;
    @FXML
    public TextField templateName;
    @FXML
    public TextField fileSuffixName;
    @FXML
    public RadioButton trueHandleRadio;
    @FXML
    public RadioButton falseHandleRadio;
    @FXML
    public FlowPane filePrefixNameStrategy;
    @FXML
    public TextField sourcesRootName;
    @FXML
    public Label fileName;
    @FXML
    public VBox vBox;
    @FXML
    public TextField filePrefixNameStrategyPattern;
    @FXML
    public FlowPane filePrefixNameStrategyPane;
    @FXML
    public Button button;
    @FXML
    private Stage primaryStage;

    private Boolean isHandleFunction;

    private Integer filePrefixNameStrategyValue;
    private File file;
    private final Insets insets = new Insets(0, 10, 10, 0);
    private String sourceTemplateName;
    private ComplexController complexController;

    /**
     * 0-修改模式 1-添加模式
     */
    private int mode = 1;

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
        ToggleGroup toggleGroup = new ToggleGroup();
        trueHandleRadio.setToggleGroup(toggleGroup);
        falseHandleRadio.setToggleGroup(toggleGroup);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton radioButton = (RadioButton) newValue;
            this.isHandleFunction = radioButton == trueHandleRadio;
        });
        ServiceLoader<TemplateFilePrefixNameStrategy> load = ServiceLoader.load(TemplateFilePrefixNameStrategy.class);
        for (TemplateFilePrefixNameStrategy next : load) {
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
            if (filePrefixNameStrategyValue == 3) {
                filePrefixNameStrategyPane.setVisible(true);
            } else {
                filePrefixNameStrategyPane.setVisible(false);
            }
        });
    }

    @FXML
    public void selectTemplateFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Template files (*.template)", "*.template");
        fileChooser.getExtensionFilters().add(extFilter);
        this.file = fileChooser.showOpenDialog(vBox.getScene().getWindow());
        fileName.setText(this.file.getName());
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
                defaultListableTemplateFactory.removeTemplateDefinition(sourceTemplateName);
                defaultListableTemplateFactory.removeTemplate(sourceTemplateName);
            }
            GenericTemplateDefinition genericTemplateDefinition = buildTemplateDefinition();
            templateContext.registerTemplateDefinition(templateName.getText(), genericTemplateDefinition);
            defaultListableTemplateFactory.preInstantiateTemplates();
            Template newTemplate = templateContext.getTemplate(templateName.getText());
            defaultListableTemplateFactory.refreshTemplate(newTemplate);
            if(mode!=1){
                //刷新父模板
                TreeItem<Label> root = complexController.listViewTemplate.getRoot();
                for(String multipleTemplateName:templateContext.getMultipleTemplateNames()){
                    MultipleTemplate multipleTemplate = defaultListableTemplateFactory.getMultipleTemplate(multipleTemplateName);
                    if(multipleTemplate.getTemplates().stream().anyMatch(template -> template.getTemplateName().equals(sourceTemplateName))) {
                        Set<Template> templates = multipleTemplate.getTemplates();
                        templates.removeIf(oldTemplate -> oldTemplate.getTemplateName().equals(sourceTemplateName));
                        templates.add(newTemplate);
                        multipleTemplate.setTemplates(templates);
                        defaultListableTemplateFactory.refreshMultipleTemplate(multipleTemplate);
                        TreeItem<Label> labelTreeItemLabel = root.getChildren().stream().filter(labelTreeItem -> labelTreeItem.getValue().getText().equals(multipleTemplate.getTemplateName())).findFirst().get();
                        root.getChildren().remove(labelTreeItemLabel);
                        complexController.initMultipleTemplateView(multipleTemplate.getTemplateName(), root);
                    }
                }
            }
            complexController.doSelectMultiple();
            AlertUtil.showInfo("Success!");
            ((Stage)vBox.getScene().getWindow()).close();
        } catch (Exception e) {
            logger.error(e);
            AlertUtil.showError("failed:" + e.getMessage());
        }
    }

    private GenericTemplateDefinition buildTemplateDefinition() throws IOException {
        TemplateFilePrefixNameStrategyFactory templateFilePrefixNameStrategyFactory = new TemplateFilePrefixNameStrategyFactory();
        GenericTemplateDefinition genericTemplateDefinition = new GenericTemplateDefinition();
        TemplateFilePrefixNameStrategy templateFilePrefixNameStrategy = templateFilePrefixNameStrategyFactory.getTemplateFilePrefixNameStrategy(Utils.setIfNull(filePrefixNameStrategyValue, 1));
        if(templateFilePrefixNameStrategy instanceof PatternTemplateFilePrefixNameStrategy){
            PatternTemplateFilePrefixNameStrategy patternTemplateFilePrefixNameStrategy= (PatternTemplateFilePrefixNameStrategy) templateFilePrefixNameStrategy;
            patternTemplateFilePrefixNameStrategy.setPattern(filePrefixNameStrategyPattern.getText());
        }
        genericTemplateDefinition.setFilePrefixNameStrategy(templateFilePrefixNameStrategy);
        genericTemplateDefinition.setFileSuffixName(fileSuffixName.getText());
        genericTemplateDefinition.setHandleFunction(Utils.setIfNull(isHandleFunction, true));
        genericTemplateDefinition.setModule(moduleName.getText());
        genericTemplateDefinition.setProjectUrl(projectUrl.getText());
        genericTemplateDefinition.setSourcesRoot(sourcesRootName.getText());
        genericTemplateDefinition.setSrcPackage(srcPackageName.getText());
        String newFileName = getTemplateContext().getEnvironment().getProperty(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH) + "/" + file.getName();
        File newFile = new File(newFileName);
        if(!this.file.getAbsolutePath().equals(newFile.getAbsolutePath())) {
            FileUtils.copyFile(this.file, newFile);
        }
        genericTemplateDefinition.setTemplateFile(newFile);
        AbstractEnvironment.putTemplateContent(newFile.getAbsolutePath(), IOUtils.toString(new FileInputStream(newFile), StandardCharsets.UTF_8));
        return genericTemplateDefinition;
    }

    public boolean check() {
        if (Utils.isEmpty(templateName.getText())) {
            AlertUtil.showError("请输入模板名");
            return false;
        }
        if (null == isHandleFunction) {
            AlertUtil.showError("请选择是否控制方法");
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
        return true;
    }
}