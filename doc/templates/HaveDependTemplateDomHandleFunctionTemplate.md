### dom可控制方法有依赖模版（HaveDependTemplateDomHandleFunctionTemplate）

```xml
<template>
<var name="doName" value="*{depend[0].className}*"/>
<var name="doSimpleName" value="*{depend[0].simpleClassName}*"/>
<var name="firstLowerDoSimpleName" value="*{tool.firstLower(*{doSimpleName}*)}*"/>
<var name="daoName" value="*{depend[1].className}*"/>
<var name="daoSimpleName" value="*{depend[1].simpleClassName}*"/>
<var name="firstLowerDaoSimpleName" value="*{tool.firstLower(*{daoSimpleName}*)}*"/>
<var name="reqVoName" value="*{depend[2].className}*"/>
<var name="reqVoSimpleName" value="*{depend[2].simpleClassName}*"/>
<var name="firstLowerReqVoSimpleName" value="*{tool.firstLower(*{reqVoSimpleName}*)}*"/>
<var name="serviceImplName" value="*{depend[3].className}*"/>
<var name="serviceImplSimpleName" value="*{depend[3].simpleClassName}*"/>
<var name="respVoName" value="*{depend[4].className}*"/>
<var name="respVoSimpleName" value="*{depend[4].simpleClassName}*"/>
<prefix>
package *{packageName}*;

import *{doName}*;
import *{daoName}*;
import *{reqVoName}*;
import *{serviceImplName}*;
import *{respVoName}*;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import cn.com.infinova.govern.common.model.utils.PageResult;
import cn.com.infinova.govern.common.model.vo.PageReqVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import static cn.com.infinova.govern.common.model.enums.Status.INTERNAL_SERVER_ERROR;
import static cn.com.infinova.govern.common.model.enums.Status.REQUEST_DATA_QUERY_ERROR;
import static cn.com.infinova.govern.common.model.utils.PageUtil.toPageResult;
import cn.com.infinova.govern.common.model.exception.BusinessException;
import cn.com.infinova.govern.common.model.utils.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Optional;
/**
 * *{tableInfo.tableComment}*业务处理接口实现类
 * @since  *{tool.currentDateTime()}*
 * @version 1.0.0
 * @author *{tool.author()}*
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class *{simpleClassName}* implements *{serviceImplSimpleName}* {
    private final *{daoSimpleName}* *{firstLowerDaoSimpleName}*;
</prefix>
<function name="add">
    /**
     * 添加*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*POJO
     * @return 返回是否成功
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public *{respVoSimpleName}* add*{doSimpleName}*(*{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
        *{firstLowerReqVoSimpleName}*.setId(null);
        Objects.requireNonNull(*{firstLowerReqVoSimpleName}*);
        *{doSimpleName}* *{firstLowerDoSimpleName}*=BeanUtil.copyProperties(*{firstLowerReqVoSimpleName}*,*{doSimpleName}*.class);
        if(*{firstLowerDaoSimpleName}*.insert(*{firstLowerDoSimpleName}*)!=1){
           throw new BusinessException(INTERNAL_SERVER_ERROR);
        }
        return BeanUtil.copyProperties(*{firstLowerDoSimpleName}*,*{respVoSimpleName}*.class);
    }
</function>
<function name="delete">
    /**
     * 删除*{tableInfo.tableComment}* 根据其id数组
     *
     * @param ids *{tableInfo.tableComment}*id数组
     * @return 返回是否成功
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean delete*{doSimpleName}*(Set&lt;Long&gt; ids<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>) {
        for(Long id:ids){
            get*{doSimpleName}*(id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,userId</if></foreach>).orElseThrow(()-> new BusinessException(REQUEST_DATA_QUERY_ERROR,id));
        }
        return CollUtil.isNotEmpty(ids) &amp;&amp; *{firstLowerDaoSimpleName}*.deleteBatchIds(ids) == ids.size();
    }
</function>
<function name="edit">
    /**
     * 编辑*{tableInfo.tableComment}*
     *
     * @param *{firstLowerReqVoSimpleName}* *{tableInfo.tableComment}*POJO
     * @return 返回是否成功
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public *{depend[4].simpleClassName}* edit*{doSimpleName}*(*{reqVoSimpleName}* *{firstLowerReqVoSimpleName}*) {
        Objects.requireNonNull(*{firstLowerReqVoSimpleName}*);
        Long id = *{firstLowerReqVoSimpleName}*.getId();
        get*{doSimpleName}*(id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,*{firstLowerReqVoSimpleName}*.getUserId()</if></foreach>).orElseThrow(()-> new BusinessException(REQUEST_DATA_QUERY_ERROR,id));
        *{doSimpleName}* *{firstLowerDoSimpleName}*=BeanUtil.copyProperties(*{firstLowerReqVoSimpleName}*,*{doSimpleName}*.class);
        if(*{firstLowerDaoSimpleName}*.updateById(*{firstLowerDoSimpleName}*)!=1){
            throw new BusinessException(INTERNAL_SERVER_ERROR);
        }
        return BeanUtil.copyProperties(*{firstLowerDoSimpleName}*,*{respVoSimpleName}*.class);
    }
</function>
<function name="get">
    /**
     * 根据id获取*{tableInfo.tableComment}*信息
     *
     * @param id *{tableInfo.tableComment}*id
     * @return *{tableInfo.tableComment}*POJO
     */
    @Override
    public Optional&lt;*{respVoSimpleName}*&gt; get*{doSimpleName}*(Long id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>) {
        LambdaQueryWrapper&lt;*{doSimpleName}*&gt; lambdaQueryWrapper=new LambdaQueryWrapper&lt;&gt;();
        lambdaQueryWrapper.eq(*{doSimpleName}*::getId,id);
        <foreach v-for="column in tableInfo.columnList">
        <if v-if="column.name=='user_id'">
        lambdaQueryWrapper.eq(*{doSimpleName}*::getUserId,userId);
        </if>
        </foreach>
        return Optional.ofNullable(*{firstLowerDaoSimpleName}*.selectOne(lambdaQueryWrapper))
                .map(s-&gt;BeanUtil.copyProperties(s,*{respVoSimpleName}*.class));
    }
</function>
<function name="getList">
    /**
     * 根据id获取*{tableInfo.tableComment}*信息列表
     *
     * @param id *{tableInfo.tableComment}*id
     * @return *{tableInfo.tableComment}*POJO
     */
    @Override
    public List&lt;*{respVoSimpleName}*&gt; get*{doSimpleName}*List(Long id<foreach v-for="column in tableInfo.columnList"><if v-if="column.name=='user_id'">,Long userId</if></foreach>) {
        LambdaQueryWrapper&lt;*{doSimpleName}*&gt; lambdaQueryWrapper=new LambdaQueryWrapper&lt;&gt;();
        lambdaQueryWrapper.eq(*{doSimpleName}*::getId,id);
        <foreach v-for="column in tableInfo.columnList">
        <if v-if="column.name=='user_id'">
        lambdaQueryWrapper.eq(*{doSimpleName}*::getUserId,userId);
        </if>
        </foreach>
        return BeanUtil.copyToList(*{firstLowerDaoSimpleName}*.selectList(lambdaQueryWrapper),*{respVoSimpleName}*.class);
    }
</function>
<function name="getPage">
    /**
     * 分页方法获取*{tableInfo.tableComment}*信息列表
     *
     * @param pageReqVo 分页请求参数
     * @return *{tableInfo.tableComment}*数据列表
     */
    @Override
    public &lt;T extends PageReqVo&gt; PageResult&lt;*{respVoSimpleName}*&gt; get*{doSimpleName}*Page(T pageReqVo) {
        LambdaQueryWrapper&lt;*{doSimpleName}*&gt; lambdaQueryWrapper=new LambdaQueryWrapper&lt;&gt;();
        if(StrUtil.isNotBlank(pageReqVo.getSearchKey())) {
            <foreach v-for="column in tableInfo.columnList">
            <if v-if="column.name not in ['id','create_time','create_by', 'update_time', 'update_by', 'created_time', 'created_by', 'updated_time', 'updated_by'] &amp;&amp; column.javaType=='String'">
            lambdaQueryWrapper.likeRight(*{doSimpleName}*::get*{tool.firstUpper(*{column.domainPropertyName}*)}*, pageReqVo.getSearchKey());
            </if>
            </foreach>
        }
        <foreach v-for="column in tableInfo.columnList">
        <if v-if="column.name=='user_id'">
        if(pageReqVo instanceof UserPageReqVo){
            lambdaQueryWrapper.eq(*{doSimpleName}*::getUserId, ((UserPageReqVo) pageReqVo).getUserId());
        }
        </if>
        </foreach>
        pageReqVo.bindDefaultQuery(lambdaQueryWrapper);
        Page&lt;*{doSimpleName}*&gt; page=new Page&lt;&gt;(pageReqVo.getPageNo(),pageReqVo.getPageSize());
        IPage&lt;*{doSimpleName}*&gt; pageResult = *{firstLowerDaoSimpleName}*.selectPage(page, lambdaQueryWrapper);
        return toPageResult(pageResult,*{respVoSimpleName}*.class);
    }
</function>
<suffix>
}
</suffix>
</template>
```