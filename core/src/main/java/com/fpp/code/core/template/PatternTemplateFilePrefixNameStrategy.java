package com.fpp.code.core.template;

public class PatternTemplateFilePrefixNameStrategy implements TemplateFilePrefixNameStrategy {
    private static final int TYPE_VALUE=3;

    private String pattern;
    /**
     * 获取命名策略代表值
     *
     * @return
     */
    @Override
    public int getTypeValue() {
        return TYPE_VALUE;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * 命名策略
     *
     * @param template  模板
     * @param srcSource 源资源 比如表名
     * @return
     */
    @Override
    public String prefixStrategy(Template template, String srcSource) {
        return null;
    }
}
