package com.zzy.yupaobackend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author zzy
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 4204809824315322653L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;

}
