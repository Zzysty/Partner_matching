package com.zzy.yupaobackend.model.dto;

import com.zzy.yupaobackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 队伍查询参数封装类
 */
@EqualsAndHashCode(callSuper = true)    // 生成 equals 和 hashCode 方法
@Data
public class TeamQuery extends PageRequest {

    private static final long serialVersionUID = -1184580669939840241L;

    /**
     * id
     */
    private Long id;

    /**
     * id 列表
     */
    private List<Long> idList;

    /**
     * 搜索文本(同时对队伍名称和描述进行模糊查询)
     */
    private String searchText;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;


}
