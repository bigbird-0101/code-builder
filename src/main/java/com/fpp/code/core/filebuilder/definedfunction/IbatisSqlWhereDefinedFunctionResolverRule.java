package com.fpp.code.core.filebuilder.definedfunction;

import com.fpp.code.common.Utils;
import com.fpp.code.core.domain.DefinedFunctionDomain;

import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * 自定义方法ibatis sqlwhere条件解析规则
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 10:15
 */
public class IbatisSqlWhereDefinedFunctionResolverRule  implements DefinedFunctionResolverRule {
    @Override
    public String doRule(DefinedFunctionDomain definedFunctionDomain) {
        String definedValue=definedFunctionDomain.getDefinedValue();
        String representFactor=definedFunctionDomain.getRepresentFactor();
        String srcFunctionBody=definedFunctionDomain.getTemplateFunction();
        String[] definedValues=definedValue.split("\\,");
        String oldSelectParamPattern=""+representFactor+"\\s*\\=\\s*\\#\\s*"+ Utils.getFieldName(representFactor) +"#";
        String newSelectParam= Stream.of(definedValues).map(s->s+"=#"+Utils.getFieldName(s)+"#").collect(Collectors.joining(" and "));
        return srcFunctionBody.replaceAll(oldSelectParamPattern,newSelectParam);
    }
}
