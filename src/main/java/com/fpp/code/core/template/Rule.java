package com.fpp.code.core.template;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * 模板规则类
 * @author fpp
 * @version 1.0
 * @date 2020/6/15 11:06
 */
public interface Rule {

    /**
     * 获取 模板排除某些正则key 这些正则key是模板中语言的 类型 的set
     * @return
     */
    Set<Pattern> getExcludeVariablePatten();
}
