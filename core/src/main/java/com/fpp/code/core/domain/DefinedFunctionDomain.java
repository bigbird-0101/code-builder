package com.fpp.code.core.domain;


import com.fpp.code.core.template.TableInfo;

/**
 * 自定义方法domain
 * @author Administrator
 */
public class DefinedFunctionDomain implements Cloneable{
    /**
     * 用户输入的自定义方法的条件
     */
    private String definedValue;
    /**
     * 类似已有方法的方法名 以此作为模板
     */
    private String templateFunctionName;

    /**
     * 代表因子 用于对 替换 模板方法中的类似
     * 比如 模板为 abdcdd中   代表因子为bd 此时会用现有 需要替换的 输入的自定义方法的条件 替换模板中的代表因子
     */
    private String representFactor;

    /**
     * 模板方法
     */
    private String templateFunction;

    private TableInfo tableInfo;

    public DefinedFunctionDomain(String definedValue, String templateFunctionName, String representFactor, String templateFunction) {
        this.definedValue = definedValue;
        this.templateFunctionName = templateFunctionName;
        this.representFactor = representFactor;
        this.templateFunction = templateFunction;
    }

    public DefinedFunctionDomain(String definedValue, String templateFunctionName, String representFactor) {
       this(definedValue,templateFunctionName,representFactor,null);
    }

    public void setDefinedValue(String definedValue) {
        this.definedValue = definedValue;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public String getDefinedValue() {
        return definedValue;
    }

    public String getTemplateFunctionName() {
        return templateFunctionName;
    }

    public void setTemplateFunctionName(String templateFunctionName) {
        this.templateFunctionName = templateFunctionName;
    }

    public String getRepresentFactor() {
        return representFactor;
    }

    public void setRepresentFactor(String representFactor) {
        this.representFactor = representFactor;
    }

    public String getTemplateFunction() {
        return templateFunction;
    }

    public void setTemplateFunction(String templateFunction) {
        this.templateFunction = templateFunction;
    }

    @Override
    public Object clone() {
        DefinedFunctionDomain definedFunctionDomain = null;
        try {
            definedFunctionDomain = (DefinedFunctionDomain)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return definedFunctionDomain;
    }
}