package main.java.org;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/4/30 15:54
 */
public enum FileTypeEnum {
    /**
     * 控制器文件
     */
    CONTROLLER(1),
    /**
     * 业务文件
     */
    SERVICE(2),

    /**
     * domain文件
     */
    DOMAIN(4),

    /**
     * 数据层文件
     */
    DAO(8),

    ;
    private int type;

    public int getType() {
        return type;
    }

    FileTypeEnum(int type) {
        this.type = type;
    }
}
