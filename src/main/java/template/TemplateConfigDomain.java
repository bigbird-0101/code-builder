package main.java.template;

/**
 * 模板配置定义
 */
public class TemplateConfigDomain {

    private String url;
    private String name;
    private String path;
    private int fileNameStrategyType;
    private int isHandleFunction;
    private String parent;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFileNameStrategyType() {
        return fileNameStrategyType;
    }

    public void setFileNameStrategyType(int fileNameStrategyType) {
        this.fileNameStrategyType = fileNameStrategyType;
    }

    public int getIsHandleFunction() {
        return isHandleFunction;
    }

    public void setIsHandleFunction(int isHandleFunction) {
        this.isHandleFunction = isHandleFunction;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public TemplateConfigDomain(String url, String name, String path, int fileNameStrategyType, int isHandleFunction, String parent) {
        this.url = url;
        this.name = name;
        this.path = path;
        this.fileNameStrategyType = fileNameStrategyType;
        this.isHandleFunction = isHandleFunction;
        this.parent = parent;
    }

    public TemplateConfigDomain() {
    }

    @Override
    public String toString() {
        return "TemplateConfigDomain{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", fileNameStrategyType=" + fileNameStrategyType +
                ", isHandleFunction=" + isHandleFunction +
                ", parent='" + parent + '\'' +
                '}';
    }
}
