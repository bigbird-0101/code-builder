package com.fpp.code.fxui.fx.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fpp.code.core.config.StringPropertySource;
import com.fpp.code.core.context.aware.TemplateContextProvider;
import com.fpp.code.core.exception.CodeConfigException;
import com.fpp.code.core.factory.DefaultListableTemplateFactory;
import com.fpp.code.core.factory.OperateTemplateBeanFactory;
import com.fpp.code.core.template.AbstractHandleFunctionTemplate;
import com.fpp.code.core.template.MultipleTemplate;
import com.fpp.code.core.template.Template;
import com.fpp.code.fxui.Main;
import com.fpp.code.fxui.common.AlertUtil;
import com.fpp.code.fxui.fx.bean.PageInputSnapshot;
import com.fpp.code.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    public Label fileName;
    @FXML
    public Label currentTemplate;
    @FXML
    public BorderPane progressBarParent;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public TextField targetTable;
    @FXML
    public CheckBox isDefinedFunction;
    @FXML
    public TextField representFactor;
    @FXML
    public TextField fields;
    @FXML
    public CheckBox isAllTable;
    @FXML
    private VBox box;

    @FXML
    private FlowPane templates;

    public FlowPane getTemplates() {
        return templates;
    }

    public BorderPane getProgressBarParent() {
        return progressBarParent;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * 变量文件
     */
    private File file;
    private Map<String,Map<String,List<String>>> selectTemplateGroup = new ConcurrentHashMap<>();
    private final URL resource = getClass().getResource("/views/template_info.fxml");
    private final Insets inserts = new Insets(5, 5, 5, 0);
    public static final BorderWidths DEFAULT = new BorderWidths(1, 0, 0, 0, false, false, false, false);
    private final BorderStroke borderStroke = new BorderStroke(null, null, Color.BLACK, null, BorderStrokeStyle.SOLID, null, null, null, null, DEFAULT, new Insets(0, 0, 0, 0));

    public File getFile() {
        return file;
    }

    public Map<String,Map<String,List<String>>> getSelectTemplateGroup() {
        return selectTemplateGroup;
    }
    public Label getCurrentTemplate() {
        return currentTemplate;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            templates.prefWidthProperty().bind(box.widthProperty());
            templates.prefHeightProperty().bind(box.heightProperty());
            initTemplateConfig();
            templates.getChildren().clear();
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
        } catch (IOException | CodeConfigException e) {
            logger.info("Template Operate init error", e);
            e.printStackTrace();
        }
    }

    private void initTemplateConfig() {
        try {
            if (selectTemplateGroup.isEmpty()) {
                String property = getTemplateContext().getEnvironment().getProperty(DEFAULT_USER_SAVE_TEMPLATE_CONFIG);
                if (Utils.isNotEmpty(property)) {
                    final PageInputSnapshot pageInputSnapshot = JSONObject.parseObject(property, new TypeReference<PageInputSnapshot>() {});
                    selectTemplateGroup =Optional.ofNullable(pageInputSnapshot.getSelectTemplateGroup()).orElse(new HashMap<>());
                    fields.setText(Optional.ofNullable(pageInputSnapshot.getFields()).orElse(StrUtil.EMPTY));
                    isDefinedFunction.setSelected(Optional.ofNullable(pageInputSnapshot.getDefinedFunction()).orElse(false));
                    representFactor.setText(Optional.ofNullable(pageInputSnapshot.getRepresentFactor()).orElse(StrUtil.EMPTY));
                    targetTable.setText(Optional.ofNullable(pageInputSnapshot.getTableNames()).orElse(StrUtil.EMPTY));
                    isAllTable.setSelected(Optional.ofNullable(pageInputSnapshot.getSelectTableAll()).orElse(false));
                }
                if (!selectTemplateGroup.isEmpty()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("user save config {}", property);
                    }
                }
                selectTemplateGroup.putIfAbsent(Main.USER_OPERATE_CACHE.getTemplateNameSelected(),new HashMap<>());
                selectTemplateGroup.forEach((k,v)->{
                    final MultipleTemplate multipleTemplate = getTemplateContext().getMultipleTemplate(k);
                    final Set<String> templateNames = multipleTemplate.getTemplates().stream().map(Template::getTemplateName).collect(Collectors.toSet());
                    final Set<String> strings = v.keySet();
                    for(String templateName:strings){
                        if(!templateNames.contains(templateName)){
                            v.remove(templateName);
                        }
                    }

                });
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("初始化用户配置异常: {} {} {} {} {} {}",e, DEFAULT_USER_SAVE_TEMPLATE_CONFIG, ",", e.getClass().getName(), ":", e.getMessage());
            }
        }
    }

    public VBox initTemplateInfo(Template template) throws IOException {
        VBox root = FXMLLoader.load(resource);
        initTemplateInfo(root, template);
        return root;
    }

    public void initTemplateInfo(VBox root, Template template) {
        Scene scene = new Scene(root);
        String templateName = template.getTemplateName();
        FlowPane flowPane = (FlowPane) scene.lookup("#functionPane");
        CheckBox templateNameCheckBox = (CheckBox) scene.lookup("#templateName");
        Map<String, List<String>> stringListMap = selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected());
        if (template instanceof AbstractHandleFunctionTemplate) {
            AbstractHandleFunctionTemplate handlerTemplate = (AbstractHandleFunctionTemplate) template;
            Set<String> templateFunctionNameS = handlerTemplate.getTemplateFunctionNameS();
            templateFunctionNameS.forEach(templateFunction -> {
                CheckBox checkBox = new CheckBox(templateFunction);
                checkBox.setPadding(inserts);
                if(null!=stringListMap){
                    List<String> functionNames = stringListMap.get(templateName);
                    if (null != functionNames && functionNames.contains(templateFunction)) {
                        checkBox.setSelected(true);
                    }
                }
                addCheckBoxListen(checkBox, templateNameCheckBox, templateName, templateFunction);
                flowPane.getChildren().add(checkBox);
            });
        } else {
            Label label = new Label(template.getTemplateName());
            flowPane.getChildren().add(label);
        }
        if (null!=stringListMap&&stringListMap.containsKey(templateName)) {
            templateNameCheckBox.setSelected(true);
        }
        templateNameCheckBox.setText("模板名:" + templateName);
        templateNameCheckBox.setUserData(templateName);
        templateNameCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            Map<String, List<String>> stringListMap2 = selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected());
            if (newValue) {
                List<String> strings = stringListMap2.get(templateName);
                if(null==strings||strings.isEmpty()){
                    if (template instanceof AbstractHandleFunctionTemplate) {
                        flowPane.getChildren().forEach(node -> {
                            CheckBox checkBox = (CheckBox) node;
                            checkBox.setSelected(true);
                        });
                    }
                }
                stringListMap2.computeIfAbsent(templateName, k -> new ArrayList<>());
            } else {
                if (template instanceof AbstractHandleFunctionTemplate) {
                    flowPane.getChildren().forEach(node -> {
                        CheckBox checkBox = (CheckBox) node;
                        checkBox.setSelected(false);
                    });
                }
                if (null!=stringListMap2) {
                    stringListMap2.remove(templateName);
                }
            }
        });

        TextArea projectUrlTextArea = (TextArea) scene.lookup("#projectUrl");
        projectUrlTextArea.setText(Utils.convertTruePathIfNotNull(template.getProjectUrl()));

        TextField moduleNameTextField = (TextField) scene.lookup("#moduleName");
        moduleNameTextField.setText(Utils.isEmpty(template.getModule())?"/":Utils.convertTruePathIfNotNull(template.getModule()));

        TextField sourcesRootTextField = (TextField) scene.lookup("#sourcesRoot");
        sourcesRootTextField.setText(Utils.convertTruePathIfNotNull(Utils.setIfNull(template.getSourcesRoot(), DEFAULT_SOURCES_ROOT)));

        TextField srcPackageTextField = (TextField) scene.lookup("#srcPackage");
        srcPackageTextField.setText(Utils.convertTruePathIfNotNull(template.getSrcPackage()));

        ImageView editTemplate = (ImageView) scene.lookup("#editTemplate");
        editTemplate.setOnMouseClicked(event -> {
            final File templateFile = template.getTemplateFile();
            Desktop desktop = Desktop.getDesktop();
            if(templateFile.exists()) {
                try {
                    desktop.open(templateFile);
                } catch (IOException ignored) {
                }
            }
        });

    }

    private void addCheckBoxListen(CheckBox checkBox, CheckBox templateNameCheckBox, String templateName, String templateFunction) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                logger.info("selectTemplateGroup newValue {}",newValue);
                if (!selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).containsKey(templateName)) {
                    List<String> functionNames = new ArrayList<>();
                    functionNames.add(templateFunction);
                    Map<String, List<String>> stringListMap = selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected());
                    if(null==stringListMap){
                        stringListMap=new HashMap<>();
                        stringListMap.put(templateName,functionNames);
                        selectTemplateGroup.put(Main.USER_OPERATE_CACHE.getTemplateNameSelected(),stringListMap);
                    }else {
                        stringListMap.put(templateName, functionNames);
                    }
                } else {
                    List<String> functionNames = selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).get(templateName);
                    if (null == functionNames) {
                        functionNames = new ArrayList<>();
                    }
                    if (!functionNames.contains(templateFunction)) {
                        functionNames.add(templateFunction);
                    }
                    selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).put(templateName, functionNames);
                }
                if (!templateNameCheckBox.isSelected()) {
                    templateNameCheckBox.setSelected(true);
                }
                logger.info("selectTemplateGroup {}",selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).get(templateName));
            } else {
                logger.info("selectTemplateGroup oldValue{}",oldValue);
                List<String> functionNames = selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).get(templateName);
                if (null != functionNames) {
                    functionNames.remove(templateFunction);
                    selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).put(templateName, functionNames);
                }
                if (null == functionNames || functionNames.isEmpty()) {
                    if (templateNameCheckBox.isSelected()) {
                        templateNameCheckBox.setSelected(false);
                    }
                    selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).remove(templateName);
                }
                logger.info("selectTemplateGroup {}",selectTemplateGroup.get(Main.USER_OPERATE_CACHE.getTemplateNameSelected()).get(templateName));
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
                    Template template = getTemplateContext().getTemplate(templateName);
                    doSetTemplate(template,anchorPane);
                    DefaultListableTemplateFactory operateTemplateBeanFactory = (DefaultListableTemplateFactory) getTemplateContext().getTemplateFactory();
                    operateTemplateBeanFactory.refreshTemplate(template);
                }
            }
            if (!selectTemplateGroup.isEmpty()) {
                final PageInputSnapshot build = PageInputSnapshot.Builder
                        .builder()
                        .withCurrentMultipleTemplate(Main.USER_OPERATE_CACHE.getTemplateNameSelected())
                        .withFields(fields.getText())
                        .withRepresentFactor(representFactor.getText())
                        .withSelectTemplateGroup(selectTemplateGroup)
                        .withTableNames(targetTable.getText())
                        .withDefinedFunction(isDefinedFunction.isSelected())
                        .withSelectTableAll(isAllTable.isSelected())
                        .build();
                getTemplateContext().getEnvironment().refreshPropertySourceSerialize(new StringPropertySource(DEFAULT_USER_SAVE_TEMPLATE_CONFIG, JSON.toJSONString(build)));
            }
            AlertUtil.showInfo("保存成功");
        } catch (CodeConfigException e) {
            AlertUtil.showError("save config error :" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void doSetTemplate(Template template,AnchorPane anchorPane){
        TextArea textArea = (TextArea)anchorPane.getChildren().get(2);
        String projectUrl = textArea.getText();
        TextField textField = (TextField) anchorPane.getChildren().get(4);
        String moduleName = textField.getText();
        TextField textField2 = (TextField) anchorPane.getChildren().get(6);
        String sourcesRoot = textField2.getText();
        TextField textField3 = (TextField) anchorPane.getChildren().get(8);
        String srcPackage = textField3.getText();
        template.setProjectUrl(Utils.convertTruePathIfNotNull(projectUrl));
        template.setModule(Utils.convertTruePathIfNotNull(moduleName));
        template.setSourcesRoot(Utils.convertTruePathIfNotNull(sourcesRoot));
        template.setSrcPackage(Utils.convertTruePathIfNotNull(srcPackage));
    }

    @FXML
    public void refreshTemplate() {
        try {
            OperateTemplateBeanFactory operateTemplateBeanFactory = (OperateTemplateBeanFactory) getTemplateContext().getTemplateFactory();
            MultipleTemplate multipleTemplate = getTemplateContext().getMultipleTemplate(Main.USER_OPERATE_CACHE.getTemplateNameSelected());
            operateTemplateBeanFactory.refreshMultipleTemplate(multipleTemplate.getTemplateName());
            initialize(null,null);
            AlertUtil.showInfo("刷新成功");
        } catch (CodeConfigException | IOException e) {
            AlertUtil.showError("refresh error :" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void selectVariableFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Template files (*.properties)", "*.properties");
        fileChooser.getExtensionFilters().add(extFilter);
        this.file = fileChooser.showOpenDialog(box.getScene().getWindow());
        if(null!=this.file) {
            fileName.setText(this.file.getName());
        }
    }
}