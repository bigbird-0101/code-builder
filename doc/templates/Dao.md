### Dao 模版

```xml
package *{packageName}*;
        import *{depend[0].className}*;
        import com.baomidou.mybatisplus.core.mapper.BaseMapper;
        /**
        * *{tableInfo.tableComment}*业务处理接口DB类
        * @since  *{tool.currentDateTime()}*
        * @version 1.0.0
        * @author *{tool.author()}*
        */
        public interface *{simpleClassName}* extends BaseMapper
<*{depend[0].simpleClassName}*>{

        }
```