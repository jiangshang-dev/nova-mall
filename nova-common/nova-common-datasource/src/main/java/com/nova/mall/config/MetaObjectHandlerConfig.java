package com.nova.mall.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.nova.mall.base.LoginUser;
import com.nova.mall.utils.UserUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/** MyBatis-Plus 公共字段自动填充 */
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        long now = System.currentTimeMillis();
        this.strictInsertFill(metaObject, "createTime", Long.class, now);
        this.strictInsertFill(metaObject, "updateTime", Long.class, now);
        Integer userId = currentUserId();
        this.strictInsertFill(metaObject, "createUser", Integer.class, userId);
        this.strictInsertFill(metaObject, "updateUser", Integer.class, userId);
    }
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Long.class, System.currentTimeMillis());
        this.strictUpdateFill(metaObject, "updateUser", Integer.class, currentUserId());
    }
    private Integer currentUserId() {
        LoginUser user = UserUtil.getUser();
        return user == null ? 0 : user.getId();
    }
}
