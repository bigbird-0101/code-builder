package io.github.bigbird0101.code.core.config;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author fpp
 * @version 1.0
 */
public class StandardEnvironment extends AbstractEnvironment {
    public StandardEnvironment() {
        init();
    }

    private void init() {
        setCoreConfigPath(StrUtil.removePrefix(ResourceUtil.getResourceObj("META-INF/code.properties").getUrl().getFile(),"/"));
        setTemplatesPath(StrUtil.removePrefix(ResourceUtil.getResourceObj("META-INF/templates").getUrl().getFile(),"/"));
        setTemplateConfigPath(StrUtil.removePrefix(ResourceUtil.getResourceObj("META-INF/templates.json").getUrl().getFile(),"/"));
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
