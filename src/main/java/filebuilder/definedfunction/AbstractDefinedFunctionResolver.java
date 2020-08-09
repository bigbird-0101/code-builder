package main.java.filebuilder.definedfunction;

import main.java.orgv2.DefinedFunctionDomain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 9:20
 */
public abstract class AbstractDefinedFunctionResolver implements DefinedFunctionResolver {

    protected List<DefinedFunctionResolverRule> ruleList=new ArrayList<>(10);

    @Override
    public void addResolverRule(DefinedFunctionResolverRule definedFunctionResolverRule) {
        this.ruleList.add(definedFunctionResolverRule);
    }

    @Override
    public String doResolver(DefinedFunctionDomain definedFunctionDomain) {
        for(DefinedFunctionResolverRule rule:ruleList){
            definedFunctionDomain.setTemplateFunction(rule.doRule(definedFunctionDomain));
        }
        return definedFunctionDomain.getTemplateFunction();
    }
}
