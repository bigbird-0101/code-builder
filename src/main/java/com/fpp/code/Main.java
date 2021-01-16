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

import java.util.Objects;

/**
 * @author Administrator
 */
public class Main extends Application {
    public static final UserOperateCache userOperateCache=new UserOperateCache();
    private static Logger logger= LogManager.getLogger(Main.class);
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("main.fxml")));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        addKeyCodeCombination(scene,fxmlLoader.getController());
        primaryStage.setTitle("spring-code");
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
        JFramePageEnvironment environment=new JFramePageEnvironment();
        environment.setCoreConfigPath("C:\\Users\\Administrator\\Desktop\\tool\\codebuilder\\conf\\code.properties");
        environment.setTemplateConfigPath("C:\\Users\\Administrator\\Desktop\\tool\\codebuilder\\conf\\templates2.0.json");
        environment.setTemplatesPath("C:\\Users\\Administrator\\Desktop\\tool\\codebuilder\\data\\templates");
        GenericTemplateContext genericTemplateContext =new GenericTemplateContext(environment);
        TemplateContextProvider.setTemplateContext(genericTemplateContext);
    }
    public static void main(String[] args) {
        launch(args);
    }
}