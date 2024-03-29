package io.github.bigbird0101.code.fxui.fx.controller;

import cn.hutool.core.collection.CollectionUtil;
import io.github.bigbird0101.code.core.config.AbstractEnvironment;
import io.github.bigbird0101.code.core.config.FileUrlResource;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.context.aware.TemplateContextProvider;
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
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Administrator
 */
public class TemplateController extends TemplateContextProvider implements Initializable {
    private static Logger logger= LogManager.getLogger(TemplateController.class);

    @FXML
    TextArea projectUrl;
    @FXML
    TextField moduleName;
    @FXML
    TextField srcPackageName;
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
    TextField depends;
    @FXML
    ComboBox<String> selectTemplateClassName;

    private Integer filePrefixNameStrategyValue;
    private File file;
    private final Insets insets = new Insets(0, 10, 10, 0);
    private String sourceTemplateName;
    private ComplexController complexController;

    /**
     * 0-修改模式 1-添加模式
     */
    private int mode = 1;

    public TemplateController() {
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
            if (filePrefixNameStrategyValue == 3) {
                filePrefixNameStrategyPane.setVisible(true);
            } else {
                filePrefixNameStrategyPane.setVisible(false);
            }
        });
        ArrayList<Class<?>> allClassByInterface = ClassUtil.getAllClassByInterface(Template.class);
        List<String> collect = allClassByInterface.stream().map(Class::getName).collect(Collectors.toList());
        selectTemplateClassName.getItems().addAll(collect);
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
                                .ifPresent(s->{
                                    final TreeItem<Label> labelTreeItem = s.getChildren().filtered(v -> v.getValue().getText().equals(sourceTemplateName)).stream().findFirst().get();
                                    int i=s.getChildren().indexOf(labelTreeItem);
                                    final TreeItem<Label> andInitTemplateView = complexController.getAndInitTemplateView(newTemplate, multipleTemplate.getTemplateName(), s);
                                    s.getChildren().set(i,andInitTemplateView);
                                });
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
        TargetFilePrefixNameStrategy targetFilePrefixNameStrategy = templateFilePrefixNameStrategyFactory.getTemplateFilePrefixNameStrategy(Utils.setIfNull(filePrefixNameStrategyValue, 1));
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
        final String text = depends.getText();
        rootTemplateDefinition.setDependTemplates(Utils.isNotEmpty(text)?Stream.of(text.split(",")).collect(Collectors.toCollection(LinkedHashSet::new)):new LinkedHashSet<>());

        String newFileName = getTemplateContext().getEnvironment().getProperty(AbstractEnvironment.DEFAULT_CORE_TEMPLATE_FILES_PATH) + "/" + templateName.getText()+AbstractEnvironment.DEFAULT_TEMPLATE_FILE_SUFFIX;
        File newFile = new File(newFileName);
        if(!this.file.equals(newFile)) {
            FileUtils.copyFile(this.file, newFile);
        }
        rootTemplateDefinition.setTemplateResource(new FileUrlResource(newFile.getAbsolutePath()));
        AbstractEnvironment.putTemplateContent(newFile.getAbsolutePath(), IOUtils.toString(Files.newInputStream(newFile.toPath()), StandardCharsets.UTF_8));
        if(isNotHave||!templateName.getText().equals(sourceTemplateName)) {
            templateContext.registerTemplateDefinition(templateName.getText(), rootTemplateDefinition);
            if(!templateName.getText().equals(sourceTemplateName)){
                defaultListableTemplateFactory.removeTemplateDefinition(sourceTemplateName);
            }
        }else{
            templateContext.registerTemplateDefinition(templateName.getText(), rootTemplateDefinition);
        }
        defaultListableTemplateFactory.preInstantiateTemplates();
        defaultListableTemplateFactory.refreshTemplate(templateContext.getTemplate(templateName.getText()));

        //修改模板名也要修改此时依赖此模板的所依赖的模板名
        defaultListableTemplateFactory.getTemplateNames()
                .stream()
                .map(defaultListableTemplateFactory::getTemplate)
                .filter(s->s instanceof HaveDependTemplate)
                .map(s->(HaveDependTemplate)s)
                .forEach(s->{
                    if(CollectionUtil.isNotEmpty(s.getDependTemplates())){
                        if(s.getDependTemplates().removeIf(oldTemplateName->oldTemplateName.equals(sourceTemplateName))){
                            s.getDependTemplates().add(templateName.getText());
                        }
                    }
                    defaultListableTemplateFactory.refreshTemplate(s);
                });
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
            if(Class.forName(selectTemplateClassName.getSelectionModel().getSelectedItem()).newInstance() instanceof HaveDependTemplate&&Utils.isEmpty(depends.getText())){
                AlertUtil.showError("该模板为依赖型模板,请填写依赖模板名!");
                return false;
            }
        } catch (Exception e) {
            logger.error(e);
        }


        if(Utils.isNotEmpty(depends.getText())){
            final Set<String> collect = Stream.of(depends.getText().split(",")).collect(Collectors.toSet());
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