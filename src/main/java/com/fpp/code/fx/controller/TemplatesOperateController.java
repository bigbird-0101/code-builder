package com.fpp.code.fx.controller;

import com.alibaba.fastjson.JSON;
import com.fpp.code.Main;
import com.fpp.code.common.AlertUtil;
import com.fpp.code.common.Utils;
import com.fpp.code.core.config.CodeConfigException;
import com.fpp.code.core.config.GeneratePropertySource;
import com.fpp.code.core.domain.DefinedFunctionDomain;
import com.fpp.code.core.factory.DefaultListableTemplateFactory;
import com.fpp.code.core.factory.OperateTemplateBeanFactory;
import com.fpp.code.core.template.AbstractHandleFunctionTemplate;
import com.fpp.code.core.template.MultipleTemplate;
import com.fpp.code.core.template.Template;
import com.fpp.code.core.template.TemplateResolveException;
import com.fpp.code.fx.aware.TemplateContextProvider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author fpp
 * @version 1.0
 * @date 2021/1/7 22:55
 */
public class TemplatesOperateController extends TemplateContextProvider implements Initializable {

    private static Logger logger = LogManager.getLogger(TemplatesOperateController.class);

    private static final String DEFAULT_SOURCES_ROOT = "src/main/java";

    private static final String DEFAULT_USER_SAVE_TEMPLATE_CONFIG = "code.user.save.config";

    @FXML
    private VBox box;

    @FXML
    private FlowPane templates;

    public static Map<String, List<String>> selectTemplateGroup = new HashMap<>();

    public static List<DefinedFunctionDomain> definedFunctionDomainList = new ArrayList<>();

    private final URL resource = getClass().getResource("/views/template_info.fxml");
    private final Insets inserts = new Insets(5, 5, 5, 0);
    public static final BorderWidths DEFAULT = new BorderWidths(1, 0, 0, 0, false, false, false, false);
    private final BorderStroke borderStroke = new BorderStroke(null, null, Color.BLACK, null, BorderStrokeStyle.SOLID, null, null, null, null, DEFAULT, new Insets(0, 0, 0, 0));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            templates.prefWidthProperty().bind(box.widthProperty());
            templates.prefHeightProperty().bind(box.heightProperty());
            initTemplateConfig();
            String templateNameSelected = Main.USER_OPERATE_CACHE.getTemplateNameSelected();
            MultipleTemplate multipleTemplate = getTemplateContext().getMultipleTemplate(templateNameSelected);
            if (null != multipleTemplate) {
                int size = 1;
                for (Template template : multipleTemplate.getTemplates()) {
                    VBox vBox = initTemplateInfo(template);
                    if (size > 3) {
                        vBox.setBorder(new Border(borderStroke));
                    }
                    templates.getChildren().add(vBox);
                    size++;
                }
            }
        } catch (IOException | CodeConfigException | TemplateResolveException e) {
            logger.info("Template Operate init error", e);
            e.printStackTrace();
        }
    }

    private void initTemplateConfig() {
        try {
            if(selectTemplateGroup.isEmpty()) {
                String property = getTemplateContext().getEnvironment().getProperty(DEFAULT_USER_SAVE_TEMPLATE_CONFIG);
                if (Utils.isNotEmpty(property)) {
                    selectTemplateGroup = JSON.toJavaObject((JSON) JSON.parse(property), Map.class);
                }
                if (!selectTemplateGroup.isEmpty()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("user save config {}", property);
                    }
                }
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("初始化用户配置异常: {} {} {} {} {}", DEFAULT_USER_SAVE_TEMPLATE_CONFIG, ",", e.getClass().getName(), ":", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public VBox initTemplateInfo(Template template) throws IOException, TemplateResolveException {
        VBox root = FXMLLoader.load(resource);
        initTemplateInfo(root, template);
        return root;
    }

    public void initTemplateInfo(VBox root, Template template) throws TemplateResolveException {
        Scene scene = new Scene(root);
        String templateName = template.getTemplateName();
        FlowPane flowPane = (FlowPane) scene.lookup("#functionPane");
        CheckBox templateNameCheckBox = (CheckBox) scene.lookup("#templateName");
        if (template instanceof AbstractHandleFunctionTemplate) {
            AbstractHandleFunctionTemplate HandlerTemplate = (AbstractHandleFunctionTemplate) template;
            Set<String> templateFunctionNameS = HandlerTemplate.getTemplateFunctionNameS();
            templateFunctionNameS.forEach(templateFunction -> {
                CheckBox checkBox = new CheckBox(templateFunction);
                checkBox.setPadding(inserts);
                List<String> functionNames = selectTemplateGroup.get(templateName);
                if (null != functionNames && functionNames.contains(templateFunction)) {
                    checkBox.setSelected(true);
                }
                addCheckBoxListen(checkBox, templateNameCheckBox, templateName, templateFunction);
                flowPane.getChildren().add(checkBox);
            });
        } else {
            Label label = new Label(template.getTemplateName());
            flowPane.getChildren().add(label);
        }
        if (selectTemplateGroup.containsKey(templateName)) {
            templateNameCheckBox.setSelected(true);
        }
        templateNameCheckBox.setText("模板名:" + templateName);
        templateNameCheckBox.setUserData(templateName);
        templateNameCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selectTemplateGroup.putIfAbsent(templateName, new ArrayList<>());
            } else {
                if (template instanceof AbstractHandleFunctionTemplate) {
                    flowPane.getChildren().forEach(node -> {
                        CheckBox checkBox = (CheckBox) node;
                        checkBox.setSelected(false);
                    });
                }
                selectTemplateGroup.remove(templateName);
            }
        });

        TextArea projectUrlTextArea = (TextArea) scene.lookup("#projectUrl");
        projectUrlTextArea.setText(template.getProjectUrl());

        TextField moduleNameTextField = (TextField) scene.lookup("#moduleName");
        moduleNameTextField.setText(template.getModule());

        TextField sourcesRootTextField = (TextField) scene.lookup("#sourcesRoot");
        sourcesRootTextField.setText(Utils.setIfNull(template.getSourcesRoot(), DEFAULT_SOURCES_ROOT));

        TextField srcPackageTextField = (TextField) scene.lookup("#srcPackage");
        srcPackageTextField.setText(template.getSrcPackage());
    }

    private void addCheckBoxListen(CheckBox checkBox, CheckBox templateNameCheckBox, String templateName, String templateFunction) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (!selectTemplateGroup.containsKey(templateName)) {
                    List<String> functionNames = new ArrayList<>();
                    functionNames.add(templateFunction);
                    selectTemplateGroup.put(templateName, functionNames);
                } else {
                    List<String> functionNames = selectTemplateGroup.get(templateName);
                    if (null == functionNames) {
                        functionNames = new ArrayList<>();
                    }
                    if (!functionNames.contains(templateFunction)) {
                        functionNames.add(templateFunction);
                    }
                    selectTemplateGroup.put(templateName, functionNames);
                }
                if (!templateNameCheckBox.isSelected()) {
                    templateNameCheckBox.setSelected(true);
                }
            } else {
                List<String> functionNames = selectTemplateGroup.get(templateName);
                if (null != functionNames) {
                    functionNames.remove(templateFunction);
                    selectTemplateGroup.put(templateName, functionNames);
                }
                if (null == functionNames ||  functionNames.isEmpty()) {
                    if (templateNameCheckBox.isSelected()) {
                        templateNameCheckBox.setSelected(false);
                    }
                    selectTemplateGroup.remove(templateName);
                }
            }
        });
    }

    @FXML
    public void saveConfig() {
        try {
            for (Node node : templates.getChildren()) {
                VBox box = (VBox) node;
                AnchorPane anchorPane = (AnchorPane) box.getChildren().get(0);
                CheckBox checkBox2 = (CheckBox) anchorPane.getChildren().get(0);
                if (checkBox2.isSelected()) {
                    String templateName = (String) checkBox2.getUserData();
                    TextArea textArea = (TextArea) anchorPane.getChildren().get(2);
                    String projectUrl = textArea.getText();
                    TextField textField = (TextField) anchorPane.getChildren().get(4);
                    String moduleName = textField.getText();
                    TextField textField2 = (TextField) anchorPane.getChildren().get(6);
                    String sourcesRoot = textField2.getText();
                    TextField textField3 = (TextField) anchorPane.getChildren().get(8);
                    String srcPackage = textField3.getText();
                    Template template = getTemplateContext().getTemplate(templateName);
                    template.setProjectUrl(Utils.convertTruePathIfNotNull(projectUrl));
                    template.setModule(Utils.convertTruePathIfNotNull(moduleName));
                    template.setSourcesRoot(Utils.convertTruePathIfNotNull(sourcesRoot));
                    template.setSrcPackage(Utils.convertTruePathIfNotNull(srcPackage));
                    DefaultListableTemplateFactory operateTemplateBeanFactory = (DefaultListableTemplateFactory) getTemplateContext().getTemplateFactory();
                    operateTemplateBeanFactory.refreshTemplate(template);
                }
            }
            if (!selectTemplateGroup.isEmpty()) {
                getTemplateContext().getEnvironment().refreshPropertySourceSerialize(new GeneratePropertySource<>(DEFAULT_USER_SAVE_TEMPLATE_CONFIG, JSON.toJSONString(selectTemplateGroup)));
            }
            AlertUtil.showInfo("保存成功");
        } catch (CodeConfigException | IOException e) {
            AlertUtil.showError("save config error :" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void refreshTemplate() {
        try {
            OperateTemplateBeanFactory operateTemplateBeanFactory = (OperateTemplateBeanFactory) getTemplateContext().getTemplateFactory();
            MultipleTemplate multipleTemplate = getTemplateContext().getMultipleTemplate(Main.USER_OPERATE_CACHE.getTemplateNameSelected());
            operateTemplateBeanFactory.refreshMultipleTemplate(multipleTemplate.getTemplateName());
            AlertUtil.showInfo("刷新成功");
        } catch (CodeConfigException | IOException e) {
            AlertUtil.showError("refresh error :" + e.getMessage());
            e.printStackTrace();
        }
    }
}