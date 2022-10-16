package io.github.bigbird0101.code.core.filebuilder.definedfunction;

import io.github.bigbird0101.code.core.domain.DefinedFunctionDomain;
import io.github.bigbird0101.code.core.domain.TableInfo;
import io.github.bigbird0101.code.util.Utils;
import org.apache.logging.log4j.LogManager;

import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义方法mybatis sqlwhere条件解析规则
 * @author fpp
 * @version 1.0
 * @date 2020/7/13 10:15
 */
public class MybatisSqlWhereDefinedFunctionResolverRule extends DbTemplateWhereDefinedFunctionResolverRule {
    private static final String JDBC_TYPE = "\\s*,\\s*jdbcType\\s*=\\s*";
    private static org.apache.logging.log4j.Logger logger= LogManager.getLogger(MybatisSqlWhereDefinedFunctionResolverRule.class);
    /**
     * 将模板方法根据规则解析成自定义方法
     *
     * @param definedFunctionDomain
     * @return 解析后自定义方法
     */
    @Override
    public String doRule(DefinedFunctionDomain definedFunctionDomain) {
        String representFactor=definedFunctionDomain.getRepresentFactor();
        String srcFunctionBody=definedFunctionDomain.getTemplateFunction();
        String definedValue=definedFunctionDomain.getDefinedValue();
        String oldSelectParamPattern="("+representFactor+"\\s*)\\=\\s*\\#\\s*\\{"+ Utils.getFieldName(representFactor) +"(.*?)\\s*\\}";
        Matcher matcher = Utils.getIgnoreLowerUpperMather(srcFunctionBody,oldSelectParamPattern);
        boolean isIncludeJdbcType=false;
        boolean isLower=false;
        if(matcher.find()){
            try {
                String srcGroup = matcher.group(1).trim();
                if(srcGroup.equals(srcGroup.toLowerCase())){
                    isLower=true;
                }
                String groupType = matcher.group(2);
                if(MYSQL_TYPES.contains(groupType.replaceAll(JDBC_TYPE,"").trim())){
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
                        "=#{"+ Utils.firstLowerCase(Utils.underlineToHump(s))
                        +(!isIncludeJdbcType?"":",jdbcType="+tableInfo.getColumnList().stream().filter(v->v.getName().equals(s)).map(TableInfo.ColumnInfo::getJdbcType).findFirst().orElse("VARCHAR"))
                        +"}")
                .collect(Collectors.joining(isLower?" and ":" and ".toUpperCase()));
    }
}
