package com.zzy.yupaobackend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍添加请求体
 *
 * @author zzy
 */
@Data
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = -513829268249906606L;

    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

}
