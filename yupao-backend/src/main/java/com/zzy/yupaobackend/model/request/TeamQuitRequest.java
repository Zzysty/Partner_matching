package com.zzy.yupaobackend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户退出队伍请求体
 *
 * @author zzy
 */
@Data
public class TeamQuitRequest implements Serializable {
    private static final long serialVersionUID = 1301263077720615362L;
    /**
     * 队伍id
     */
    private Long teamId;
}
