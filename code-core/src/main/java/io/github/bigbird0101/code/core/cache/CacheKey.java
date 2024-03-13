package io.github.bigbird0101.code.core.cache;

import io.github.bigbird0101.code.core.template.AbstractHandleFunctionTemplate;
import io.github.bigbird0101.code.core.template.AbstractNoHandleFunctionTemplate;

import java.util.Map;
import java.util.Objects;

/**
 * 缓存key 用于缓存模板解析结果
 * @see AbstractHandleFunctionTemplate
 * @see AbstractNoHandleFunctionTemplate
 * @author fpp
 * @version 1.0
 */
public class CacheKey {
    /**
     * 模板名
     */
    private final String templateName;
    /**
     * 解析模板时传的map参数
     */
    private final Map<String, Object> map;

    private final int hashCode;
    public CacheKey(String templateName, Map<String, Object> map) {
        this.templateName = templateName;
        this.map = map;
        int hashCodeTemp=0;
        for(Map.Entry<String,Object> entry:map.entrySet()) {
            hashCodeTemp+=entry.getValue().hashCode();
        }
        this.hashCode=templateName.hashCode()+hashCodeTemp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CacheKey cacheKey = (CacheKey) o;

        return Objects.equals(templateName, cacheKey.templateName) &&
                equalsMap(map, cacheKey.map);
    }

    private boolean equalsMap(Map<String, Object> map, Map<String, Object> map1) {
        if(map==null||map1==null){
            return false;
        }
        for(Map.Entry<String,Object> entry:map.entrySet()){
            Object data=map1.get(entry.getKey());
            if(null==data||!data.equals(entry.getValue())){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "CacheKey{" +
                "templateName='" + templateName + '\'' +
                ", newServiceMap=" + map +
                ", hashCode=" + hashCode +
                '}';
    }
}
