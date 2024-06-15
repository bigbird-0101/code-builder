package io.github.bigbird0101.code.fxui;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.system.UserInfo;
import io.github.bigbird0101.code.core.config.StandardEnvironment;
import io.github.bigbird0101.code.core.context.GenericTemplateContext;
import io.github.bigbird0101.code.core.context.aware.AbstractTemplateContextProvider;
import io.github.bigbird0101.code.core.share.AbstractShareServerProvider;
import io.github.bigbird0101.code.core.share.ShareServer;
import io.github.bigbird0101.code.core.spi.inject.instance.InstanceContext;
import io.github.bigbird0101.code.core.template.Template;
import io.github.bigbird0101.code.core.template.TemplateTraceContext;
import io.github.bigbird0101.code.fxui.fx.MinWindow;
import io.github.bigbird0101.code.fxui.fx.cache.UserOperateCache;
import io.github.bigbird0101.code.fxui.fx.component.FxApp;
import io.github.bigbird0101.code.fxui.fx.controller.ComplexController;
import io.github.bigbird0101.code.util.ClassUtil;
import io.github.bigbird0101.code.util.Utils;
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

/**
 * @author Administrator
 */
public class CodeBuilderApplication extends Application {
    public static final UserOperateCache USER_OPERATE_CACHE=new UserOperateCache();
    public static final String ICON_PNG = "images/icon.png";
    private static final Logger logger= LogManager.getLogger(CodeBuilderApplication.class);
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
                logger.info("see you again");
                stop();
                logger.info("Platform exit start ");
                Platform.exit();
                logger.info("Platform exit end ");
                logger.info("System exit start ");
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
        StandardEnvironment environment=new StandardEnvironment();
        if(!raw.isEmpty()) {
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
        environment.setContextTemplateInitRefresh(true);
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        AbstractTemplateContextProvider.setTemplateContext(genericTemplateContext);
        ThreadUtil.execAsync(() -> {
            try {
                ShareServer shareServer = new ShareServer();
                shareServer.init();
                shareServer.start();
                AbstractShareServerProvider.setShareServer(shareServer);
            } catch (Exception exception) {
                logger.error("shareServer start failed", exception);
            }
        });
        ThreadUtil.execAsync(() -> ClassUtil.getAllClassByInterface(Template.class));
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
            logger.error("setLogFilePath", e);
        }
    }

    @Override
    public void stop() {
        TemplateTraceContext.clear();
        AbstractShareServerProvider.getShareServer().destroy();
        InstanceContext.getInstance().destroy();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
