package io.github.bigbird0101.code.core.template;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;

/**
 * @author fpp
 * @version 1.0
 */
public abstract class AbstractMultipleTemplate implements MultipleTemplate {
    private static Logger logger= LogManager.getLogger(AbstractMultipleTemplate.class);
    @Override
    public Object clone(){
        return ObjectUtil.cloneByStream(this);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    /**
     * 组合模板序列化
     */
    public static class MultipleTemplateSerializer implements ObjectSerializer{

        @Override
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
            AbstractMultipleTemplate abstractTemplate= (AbstractMultipleTemplate) object;
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("templates",abstractTemplate.getTemplates().stream().map(Template::getTemplateName).toArray());
            jsonObject.put("name",abstractTemplate.getTemplateName());
            if(logger.isInfoEnabled()){
                logger.info(" JSON Serializer {}",jsonObject);
            }
            serializer.write(jsonObject);
        }
    }
}
