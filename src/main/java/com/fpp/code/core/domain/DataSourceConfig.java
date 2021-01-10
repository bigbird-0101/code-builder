package com.fpp.code.core.domain;

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
}
