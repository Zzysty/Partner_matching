package com.zzy.yupaobackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    protected int pageNum = 1;

    /**
     * 每页数量
     */
    protected int pageSize = 10;
}
