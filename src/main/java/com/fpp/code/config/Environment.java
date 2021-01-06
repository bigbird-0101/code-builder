package com.fpp.code.config;

import com.fpp.code.template.MultipleTemplate;

import java.util.List;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/12/22 11:14
 */
public interface Environment extends Config {
    Config getProjectFileConfig();
    Config getDataSourceFileConfig();
    List<MultipleTemplate> getMultipleTemplates();
    void parse() throws CodeConfigException;
    void refresh() throws CodeConfigException;
}
