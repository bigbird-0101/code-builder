package com.fpp.code.fxui;

import cn.hutool.system.UserInfo;
import com.fpp.code.core.config.JFramePageEnvironment;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.core.context.aware.TemplateContextProvider;
import com.fpp.code.core.template.Template;
import com.fpp.code.core.template.TemplateTraceContext;
import com.fpp.code.fxui.common.ClassUtil;
import com.fpp.code.fxui.fx.MinWindow;
import com.fpp.code.fxui.fx.cache.UserOperateCache;
import com.fpp.code.fxui.fx.component.FxApp;
import com.fpp.code.fxui.fx.controller.ComplexController;
import com.fpp.code.util.Utils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author Administrator
 */
public class Main extends Application {
    public static final UserOperateCache USER_OPERATE_CACHE=new UserOperateCache();
    public static final String ICON_PNG = "images/icon.png";
    private static Logger logger= LogManager.getLogger(Main.class);
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("views/main.fxml")));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        addKeyCodeCombination(scene,fxmlLoader.getController());
        primaryStage.setTitle("code-builder");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(ICON_PNG))));
        primaryStage.setOnCloseRequest(event -> {
            ButtonType restart = new ButtonType("重启");
            ButtonType hide = new ButtonType("最小化托盘");
            ButtonType out = new ButtonType("退出");
            ButtonType cancel = new ButtonType("取消");
            Alert alert = new Alert(Alert.AlertType.NONE,"",restart,hide,out,cancel);
            alert.setTitle("操作");
            alert.setHeaderText("");
            ButtonType buttonType = alert.showAndWait().orElse(new ButtonType("error"));
            if(buttonType.equals(hide)){
                MinWindow.getInstance().hide(primaryStage);
            }else if(buttonType.equals(restart)){
                restart(primaryStage);
            }else if(buttonType.equals(out)){
                Platform.exit();
                System.exit(0);
            }else{
                event.consume();
            }
            alert.setOnCloseRequest(ev->{
                alert.close();
            });
        });
        primaryStage.show();
        FxApp.init(primaryStage,ICON_PNG);
        MinWindow.getInstance().listen(primaryStage);
    }

    public void restart(Stage primaryStage){
        try {
            primaryStage.close();
            this.stop();
            this.init();
            this.start(new Stage());
        } catch (Exception e) {
            logger.error("restart error",e);
        }
    }

    private void addKeyCodeCombination(Scene scene, ComplexController controller) {
        KeyCodeCombination kc1 = new KeyCodeCombination(KeyCode.A, KeyCodeCombination.CONTROL_DOWN);
        KeyCodeCombination kc3 = new KeyCodeCombination(KeyCode.B, KeyCodeCombination.CONTROL_DOWN);
        KeyCodeCombination kc2 = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCodeCombination.CONTROL_DOWN);
        scene.getAccelerators().put(kc1, controller::doBuildCore);
        scene.getAccelerators().put(kc2, controller::doBuildCoreAfter);
        scene.getAccelerators().put(kc3, controller::doBuildCoreOverride);
    }

    @Override
    public void init() {
        Parameters parameters = getParameters();
        List<String> raw = parameters.getRaw();
        JFramePageEnvironment environment=new JFramePageEnvironment();
        if(raw.isEmpty()) {
            String userHome = System.getProperty("user.home");
            String projectHome=userHome+"\\Desktop\\tool\\";
            environment.setCoreConfigPath(projectHome+"codebuilder\\conf\\code.properties");
            environment.setTemplateConfigPath(projectHome+"codebuilder\\conf\\templates.json");
            environment.setTemplatesPath(projectHome+"codebuilder\\data\\templates");
        }else{
            logger.info("run params {},{}",raw.toString(),new UserInfo().getCurrentDir());
            environment.setCoreConfigPath(raw.get(0));
            environment.setTemplateConfigPath(raw.get(1));
            environment.setTemplatesPath(raw.get(2));
            try {
                String logFilePath = raw.get(3);
                if(Utils.isNotEmpty(logFilePath)) {
                    setLogFilePath(logFilePath);
                }
            }catch (Exception e){
                logger.warn("load log file warning not get config log file");
            }
        }
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        TemplateContextProvider.setTemplateContext(genericTemplateContext);
        CompletableFuture.runAsync(()-> ClassUtil.getAllClassByInterface(Template.class));
    }

    public void setLogFilePath(String logFilePath){
        ConfigurationSource source;
        File log4jFile = new File(logFilePath);
        try {
            if (log4jFile.exists()) {
                source = new ConfigurationSource(Files.newInputStream(log4jFile.toPath()), log4jFile);
                Configurator.reconfigure(source.getURI());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        TemplateTraceContext.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
