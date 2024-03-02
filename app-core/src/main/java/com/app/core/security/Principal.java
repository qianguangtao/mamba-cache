package com.app.core.security;

import com.app.core.constant.Constants;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/10/17 13:23
 * @description: 当前用户的DTO
 */
@Data
public class Principal {
    /**
     * 用户主键
     */
    Serializable primaryKey;

    /**
     * 用户唯一标识（可以是主键，手机号，身份证号等等）
     */
    Serializable identity;

    /**
     * 名称
     */
    String name;

    /**
     * 角色集合
     */
    List<String> roleList;

    /**
     * 权限集合
     */
    List<String> permissionList;

    public static Principal defaultPrincipal() {
        Principal principal = new Principal();
        principal.setPrimaryKey(Constants.DEFAULT_CREATE_BY);
        principal.setIdentity(Constants.DEFAULT_CREATE_BY);
        principal.setName(Constants.DEFAULT_CREATOR);
        principal.setPermissionList(Collections.emptyList());
        principal.setRoleList(Collections.emptyList());
        return principal;
    }
}
