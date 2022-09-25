package com.fpp.code.core.filebuilder.definedfunction;

import com.fpp.code.core.domain.DefinedFunctionDomain;
import com.fpp.code.core.domain.TableInfo;
import com.fpp.code.util.Utils;
import org.apache.logging.log4j.LogManager;

import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义方法ibatis sqlwhere条件解析规则
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 10:15
 */
public class IbatisSqlWhereDefinedFunctionResolverRule  extends DbTemplateWhereDefinedFunctionResolverRule {
    private static org.apache.logging.log4j.Logger logger= LogManager.getLogger(IbatisSqlWhereDefinedFunctionResolverRule.class);
    @Override
    public String doRule(DefinedFunctionDomain definedFunctionDomain) {
        String definedValue=definedFunctionDomain.getDefinedValue();
        String representFactor=definedFunctionDomain.getRepresentFactor();
        String srcFunctionBody=definedFunctionDomain.getTemplateFunction();
        String oldSelectParamPattern="("+representFactor+"\\s*)\\=\\s*\\#\\s*"+ Utils.getFieldName(representFactor) +"\\s*(.*?)\\s*#";
        Matcher matcher = Utils.getIgnoreLowerUpperMather(srcFunctionBody,oldSelectParamPattern);
        boolean isIncludeJdbcType=false;
        boolean isLower=false;
        if(matcher.find()){
            try {
                String srcGroup = matcher.group(1).trim();
                if(srcGroup.trim().equals(srcGroup.toLowerCase())){
                    isLower=true;
                }
                String groupType = matcher.group(2);
                if(MYSQL_TYPES.contains(groupType.replace(":","").trim())){
                    isIncludeJdbcType=true;
                }
            }catch (Exception e){
                logger.debug("IbatisSqlWhereDefinedFunctionResolverRule doRule error mather group(1)");
            }
        }
        return matcher.replaceAll(getNewSelectParam(definedValue,isLower,definedFunctionDomain.getTableInfo(),isIncludeJdbcType));
    }

    private String getNewSelectParam(String definedValue, boolean isLower, TableInfo tableInfo, boolean isIncludeJdbcType){
        return Stream.of(definedValue.split("\\,"))
                .map(s->(isLower?s.toLowerCase():s.toUpperCase())+
                        "=#"+Utils.firstLowerCase(Utils.underlineToHump(s))+
                        (!isIncludeJdbcType?"":
                                (":"
                                + tableInfo.getColumnList()
                                        .stream()
                                        .filter(columnInfo -> columnInfo.getName().equals(s))
                                        .map(TableInfo.ColumnInfo::getJdbcType)
                                        .findFirst()
                                        .orElse("VARCHAR")
                                )
                        )
                +"#")
                .collect(Collectors.joining(isLower?" and ":" and ".toUpperCase()));
    }

}
