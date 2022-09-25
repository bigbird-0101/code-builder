package com.fpp.code.core.domain;

import cn.hutool.core.util.StrUtil;
import com.fpp.code.core.config.Environment;
import com.fpp.code.core.exception.CodeConfigException;

/**
 * @author Administrator
 */
public class DataSourceConfig {
    private String quDongName;
    private String userName;
    private String url;
    private String password;

    public DataSourceConfig(String quDongName, String userName, String url, String password) {
        this.quDongName = quDongName;
        this.userName = userName;
        this.url = url;
        this.password = password;
    }

    public String getQuDongName() {
        return quDongName;
    }

    public void setQuDongName(String quDongName) {
        this.quDongName = quDongName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DataSourcePojo{" +
                "quDongName='" + quDongName + '\'' +
                ", userName='" + userName + '\'' +
                ", url='" + url + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    /**
     * 获取数据源配置
     *
     * @return
     */
    public static DataSourceConfig getDataSourceConfig(Environment environment) {
        final String selectDataSource = environment.getProperty("code.datasource");
        if (StrUtil.isNotBlank(selectDataSource)) {
            String url = environment.getProperty(StrUtil.format("code.datasource.{}.url", selectDataSource));
            String userName = environment.getProperty(StrUtil.format("code.datasource.{}.username", selectDataSource));
            String password = environment.getProperty(StrUtil.format("code.datasource.{}.password", selectDataSource));
            if (!StrUtil.isAllNotBlank(url, userName, password)) {
                throw new CodeConfigException("datasource config error ，not get url or userName or password");
            }
            return new DataSourceConfig(null, userName, url, password);
        }
        String url = environment.getProperty("code.datasource.url");
        String userName = environment.getProperty("code.datasource.username");
        String password = environment.getProperty("code.datasource.password");
        if (!StrUtil.isAllNotBlank(url, userName, password)) {
            throw new CodeConfigException("datasource config error,not get url {} or userName {} or password {}"
                    ,url,userName,password);
        }
        return new DataSourceConfig(null, userName, url, password);
    }
}
