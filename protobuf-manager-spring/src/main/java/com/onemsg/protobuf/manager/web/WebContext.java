package com.onemsg.protobuf.manager.web;


import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

import com.onemsg.protobuf.manager.exception.DataModelResponseException;
import com.onemsg.protobuf.manager.user.UserModel;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * Web 上下文中的信息
 * 
 * @author mashuguang
 * @since 2022-05
 */
@AllArgsConstructor
@ToString
public class WebContext {
    
    private static final WebContext EMPTY = new WebContext(null);

    @Nullable
    public final UserModel.Info user;

    private static final ThreadLocal<WebContext> HOLDER = new NamedThreadLocal<>("Web Context");

    /**
     * 
     * @return
     * @throws DataModelResponseException 如果 user is null 返回 401 异常
     */
    public UserModel.Info getUser() throws DataModelResponseException {
        if (user == null) {
            throw DataModelResponseException.AUTHENTICATION_FAILURE;
        }
        return user;
    }

    public static WebContext currentWebContext() {
        WebContext wc = HOLDER.get();
        return wc == null ? EMPTY : wc;
    }

    public static void setWebContext(WebContext webContext) {
        HOLDER.set(webContext);
    }

    public static void resetWebContext() {
        HOLDER.remove();
    }
}
