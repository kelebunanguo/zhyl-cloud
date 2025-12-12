package com.zhyl.framework.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
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
        this.strictInsertFill(metaObject, "createBy", String.class, String.valueOf(getLoginUserId()));
        this.strictInsertFill(metaObject, "createTime", Date.class, DateUtils.getNowDate());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", String.valueOf(getLoginUserId()), metaObject);
//        this.strictInsertFill(metaObject, "updateBy", String.class, String.valueOf(getLoginUserId()));
//        this.strictUpdateFill(metaObject, "updateTime", Date.class, DateUtils.getNowDate());
    }

    public Long getLoginUserId() {
        try {
            // 获取到当前登录人的信息
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (ObjectUtils.isNotEmpty(loginUser)) {
                return loginUser.getUserId();
            }
            return 1L;
        } catch (Exception e) {
            return 1L;
        }
    }

}
