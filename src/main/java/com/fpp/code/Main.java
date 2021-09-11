package com.fpp.code;

import com.fpp.code.core.config.JFramePageEnvironment;
import com.fpp.code.core.context.GenericTemplateContext;
import com.fpp.code.fx.aware.TemplateContextProvider;
import com.fpp.code.fx.cache.UserOperateCache;
import com.fpp.code.fx.controller.ComplexController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Objects;

/**
 * @author Administrator
 */
public class Main extends Application {
    public static final UserOperateCache USER_OPERATE_CACHE=new UserOperateCache();
    private static Logger logger= LogManager.getLogger(Main.class);
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("main.fxml")));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        addKeyCodeCombination(scene,fxmlLoader.getController());
        primaryStage.setTitle("code-builder");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("icon.png"))));
        primaryStage.show();
    }

    private void addKeyCodeCombination(Scene scene,ComplexController controller) {
        KeyCodeCombination kc1 = new KeyCodeCombination(KeyCode.A, KeyCodeCombination.CONTROL_DOWN);
        KeyCodeCombination kc2 = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCodeCombination.CONTROL_DOWN);
        scene.getAccelerators().put(kc1, controller::doBuildCore);
        scene.getAccelerators().put(kc2, controller::doBuildCoreAfter);
    }

    @Override
    public void init() throws Exception {
        Parameters parameters = getParameters();
        List<String> raw = parameters.getRaw();
        JFramePageEnvironment environment=new JFramePageEnvironment();
        if(raw.isEmpty()) {
            environment.setCoreConfigPath("C:\\Users\\Administrator\\Desktop\\tool\\codebuilder\\conf\\code.properties");
            environment.setTemplateConfigPath("C:\\Users\\Administrator\\Desktop\\tool\\codebuilder\\conf\\templates.json");
            environment.setTemplatesPath("C:\\Users\\Administrator\\Desktop\\tool\\codebuilder\\data\\templates");
        }else{
            logger.info("run params {},{}",raw.toString(),System.getProperty("exe.filePath"));
            environment.setCoreConfigPath(raw.get(0));
            environment.setTemplateConfigPath(raw.get(1));
            environment.setTemplatesPath(raw.get(2));
            try {
                String logFilePath = raw.get(3);
                setLogFilePath(logFilePath);
            }catch (Exception e){
                logger.warn("load log file warning",e);
            }
        }
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        TemplateContextProvider.setTemplateContext(genericTemplateContext);
    }

    public void setLogFilePath(String logFilePath){
        ConfigurationSource source;
        File log4jFile = new File(logFilePath);
        try {
            if (log4jFile.exists()) {
                source = new ConfigurationSource(new FileInputStream(log4jFile), log4jFile);
                Configurator.initialize(null, source);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
