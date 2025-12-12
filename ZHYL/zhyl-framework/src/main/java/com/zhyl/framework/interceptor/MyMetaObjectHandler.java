package com.zhyl.framework.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zhyl.common.core.domain.model.LoginUser;
import com.zhyl.common.utils.DateUtils;
import com.zhyl.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, DateUtils.getNowDate());
        this.strictInsertFill(metaObject, "createBy", String.class, String.valueOf(getLoginUser()));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", String.valueOf(getLoginUser()), metaObject);
//        this.strictUpdateFill(metaObject, "updateTime", Date.class, DateUtils.getNowDate());
//        this.strictUpdateFill(metaObject, "updateBy", String.class, String.valueOf(getLoginUser()));
    }

    /**
     * 获取当前登录人的ID
     *
     * @return 登录人ID
     */
    public Long getLoginUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            return loginUser.getUserId();
        }
        return 1L;
    }
}