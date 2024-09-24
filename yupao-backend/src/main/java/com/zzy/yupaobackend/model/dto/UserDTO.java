package com.zzy.yupaobackend.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserDTO {

    private long id;
    private String avatarUrl;
    private String username;
    private Integer gender;
    private String phone;
    private String email;
}
