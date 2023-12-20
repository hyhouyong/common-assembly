package com.kcwl.common.label.configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.kcwl.ddd.infrastructure.session.SessionContext;
import com.kcwl.ddd.infrastructure.session.SessionData;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;
import java.util.Optional;

public class DefaultInsertOrUpdateFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "createUserId", Long.class, Optional.ofNullable(SessionContext.getSessionData()).map(SessionData::getUserId).orElse(0L));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateUserId", Long.class, Optional.ofNullable(SessionContext.getSessionData()).map(SessionData::getUserId).orElse(0L));
    }

}