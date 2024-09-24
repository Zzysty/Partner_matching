package com.zzy.yupaobackend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户加入队伍请求体
 *
 * @author zzy
 */
@Data
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = 5902491508236868820L;
    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}
