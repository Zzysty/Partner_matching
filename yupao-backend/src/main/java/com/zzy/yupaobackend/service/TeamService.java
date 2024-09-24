package com.zzy.yupaobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzy.yupaobackend.model.domain.Team;
import com.zzy.yupaobackend.model.domain.User;
import com.zzy.yupaobackend.model.dto.TeamQuery;
import com.zzy.yupaobackend.model.request.TeamDeleteRequest;
import com.zzy.yupaobackend.model.request.TeamJoinRequest;
import com.zzy.yupaobackend.model.request.TeamQuitRequest;
import com.zzy.yupaobackend.model.request.TeamUpdateRequest;
import com.zzy.yupaobackend.model.vo.TeamUserVO;

import java.util.List;

/**
* @author zzy
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-05-17 09:51:34
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 获取队伍列表
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍信息
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除队伍
     * @param teamId
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long teamId, User loginUser);
}
