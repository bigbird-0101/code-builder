package main.java.org;

/**
 * @author fpp
 * @version 1.0
 * @date 2020/4/30 15:45
 */
public enum FunctionTypeEnum {
    /**
     * 添加方法
     */
    ADD(1),
    /**
     * 编辑方法
     */
    EDIT(2),
    /**
     * 删除方法
     */
    DELETE(4),
    /**
     * 根据id查询
     */
    GETBYID(8),
    /**
     * 分页方法
     */
    GETALL(16)
    ;

    private int type;

    public int getType() {
        return type;
    }

    FunctionTypeEnum(int type) {
        this.type = type;
    }
}
