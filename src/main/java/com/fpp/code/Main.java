package com.fpp.code;

import com.fpp.code.core.config.JFramePageEnvironment;
import com.fpp.code.core.context.GenerateTemplateContext;
import com.fpp.code.fx.aware.TemplateContextProvider;
import com.fpp.code.fx.cache.UserOperateCache;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("ComplexApplication_css.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setTitle("spring-code");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        Parameters parameters = getParameters();
        JFramePageEnvironment environment=new JFramePageEnvironment();
        environment.setCoreConfigPath("C:\\Users\\Administrator\\Desktop\\codebuilder\\conf\\code.properties");
        environment.setTemplateConfigPath("C:\\Users\\Administrator\\Desktop\\codebuilder\\conf\\templates2.0.json");
        environment.setTemplatesPath("C:\\Users\\Administrator\\Desktop\\codebuilder\\data\\templates");
        GenerateTemplateContext generateTemplateContext=new GenerateTemplateContext(environment);
        TemplateContextProvider.setTemplateContext(generateTemplateContext);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
