package com.zzy.yupaobackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.yupaobackend.model.domain.Team;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;

/**
* @author zzy
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2024-05-17 09:51:34
* @Entity generator.domain.Team
*/
public interface TeamMapper extends BaseMapper<Team> {
    @Update("UPDATE team SET isDelete = 1 WHERE id = #{id}")
    int logicDeleteTeamById(Long id);

    @Update("UPDATE user_team SET isDelete = 1 WHERE teamId = #{teamId}")
    int logicDeleteUserTeamByTeamId(Long teamId);
}




