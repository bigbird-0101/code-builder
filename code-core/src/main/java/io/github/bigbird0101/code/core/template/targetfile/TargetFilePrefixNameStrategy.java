package io.github.bigbird0101.code.core.template.targetfile;

import com.alibaba.fastjson.annotation.JSONField;
import io.github.bigbird0101.code.core.template.Template;

import java.io.Serializable;
import java.util.Map;

/**
 * 模板最终生成文件的文件前缀名命名策略
 * @author fpp
 * @version 1.0
 */
public interface TargetFilePrefixNameStrategy extends Serializable {
    /**
     * 获取命名策略代表值
     * @return 命名策略代表值
     */
    @JSONField(name = "value")
    int getTypeValue();
    /**
     * 命名策略
     * @param template 模板
     * @param srcSource 源资源 比如表名
     * @param dataModel 数据模型
     * @return 文件名前缀
     */
    String prefixStrategy(Template template, String srcSource, Map<String,Object> dataModel);

}
