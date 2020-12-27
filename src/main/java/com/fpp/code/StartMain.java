package com.fpp.code;

import com.fpp.code.config.CodeConfigException;
import com.fpp.code.config.JFramePageEnvironment;
import com.fpp.code.orgv2.MainJFramePage;

import java.util.Arrays;

/**
 * 启动类
 */
public class StartMain {

    private static final String DEFAULT_CONFIG_URL = "META-INF/code.properties";
    private static final String DEFAULT_CONFIG_TEMPLATE_URL = "META-INF/templates.json";
    private static final String DEFAULT_CONFIG_TEMPLATE_PATH = "META-INF/templates";

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        MainJFramePage mainJFramePage = new MainJFramePage();
        try {
            System.out.println(Arrays.asList(args).toString());
            JFramePageEnvironment jFramePageEnvironment = new JFramePageEnvironment();
            if (args.length > 0) {
                jFramePageEnvironment.setCoreConfigPath(args[0]);
                jFramePageEnvironment.setTemplateConfigPath(args[1]);
                jFramePageEnvironment.setTemplatesPath(args[2]);
            } else {
                jFramePageEnvironment.setCoreConfigPath(DEFAULT_CONFIG_URL);
                jFramePageEnvironment.setTemplateConfigPath(DEFAULT_CONFIG_TEMPLATE_URL);
                jFramePageEnvironment.setTemplatesPath(DEFAULT_CONFIG_TEMPLATE_PATH);
            }
            jFramePageEnvironment.parse();
            mainJFramePage.setjFramePageEnvironment(jFramePageEnvironment);
            mainJFramePage.start();
        } catch (CodeConfigException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}