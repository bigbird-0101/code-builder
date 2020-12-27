package com.fpp.code.orgv2;

import com.fpp.code.config.ProjectFileConfig;
import com.fpp.code.template.MultipleTemplate;

/**
 * 生成代码客户端
 * @author fpp
 * @version 1.0
 * @date 2020/6/16 13:33
 */
public class CodeBuilderAdmin {

    private MultipleTemplate multipleTemplate;

    private ProjectFileConfig projectFileConfig;

    public CodeBuilderAdmin(MultipleTemplate multipleTemplate,ProjectFileConfig projectFileConfig) {
        this.multipleTemplate = multipleTemplate;
        this.projectFileConfig = projectFileConfig;
    }

    public void done(){
        String projectCompleteUrl=projectFileConfig.getProperty("project-complete-url");
        String srcUrl=projectFileConfig.getProperty("src-url");
    }

}
