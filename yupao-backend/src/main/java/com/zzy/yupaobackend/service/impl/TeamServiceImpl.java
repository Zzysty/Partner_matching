package com.zzy.yupaobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzy.yupaobackend.common.ErrorCode;
import com.zzy.yupaobackend.exception.BusinessException;
import com.zzy.yupaobackend.mapper.TeamMapper;
import com.zzy.yupaobackend.model.domain.Team;
import com.zzy.yupaobackend.model.domain.User;
import com.zzy.yupaobackend.model.domain.UserTeam;
import com.zzy.yupaobackend.model.dto.TeamQuery;
import com.zzy.yupaobackend.model.enums.TeamStatusEnum;
import com.zzy.yupaobackend.model.request.TeamJoinRequest;
import com.zzy.yupaobackend.model.request.TeamQuitRequest;
import com.zzy.yupaobackend.model.request.TeamUpdateRequest;
import com.zzy.yupaobackend.model.vo.TeamUserVO;
import com.zzy.yupaobackend.model.vo.UserVO;
import com.zzy.yupaobackend.service.TeamService;
import com.zzy.yupaobackend.service.UserService;
import com.zzy.yupaobackend.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author zzy
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-05-17 09:51:34
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private TeamMapper teamMapper;

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 添加队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)   // 事务
    public long addTeam(Team team, User loginUser) {
        // 1.判空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加队伍信息不能为空");
        }
        // 2.是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请先登录");
        }
        final long userId = loginUser.getId();  // 获取用户id
        // 3.校验
        //  1.队伍人数 > 1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum <= 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        //  2.队伍标题 1-20个字符
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不符合要求");
        }
        //  3.队伍描述 1-512个字符
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //  4.状态是否公开 int 不传默认为0 公开
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);  // 通过值获取枚举
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不符合要求");
        }
        //  5.如果状态为加密，一定要有密码，密码1-20个字符
        String teamPassword = team.getPassword();
        if (statusEnum.equals(TeamStatusEnum.SECRET) && StringUtils.isBlank(teamPassword) || teamPassword.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码不符合要求");
        }
        //  6.超时时间 大于 当前时间
        Date expireTime = team.getExpireTime();
        if (expireTime == null || expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍超时时间不符合要求");
        }
        //  7.校验用户最多创建5个队伍
        // todo bug，可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建 5 个队伍");
        }
        //  8.插入队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加队伍失败");
        }
        // 9.插入用户-队伍关系到关系表
        UserTeam userTeam = new UserTeam();
//        userTeam.setId(userId);
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加队伍失败");
        }
        return teamId;
    }

    /**
     * 列表查询队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamQuery != null) {
            // 取其中信息
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC; // 默认公开
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH, "无权限查看");
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        // 不显示已过期的队伍
        queryWrapper.and(qw -> qw.isNull("expireTime").or().gt("expireTime", new Date()));

        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            // 脱敏team
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏user
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreatUser(userVO);    // 设置创建人用户信息
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        // 外部判空了，这里不重复
        Long id = teamUpdateRequest.getId();
        Team oldTeam = this.getTeamById(id);

        // 只有管理员或者队伍创建人才能修改
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限修改");
        }
        // 如果更改状态为加密，必须要求密码
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET) && StringUtils.isBlank(teamUpdateRequest.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍必须要有密码");
        }
        // 检查用户传入的新值与老值一致，不更新
        // 创建一个副本，用于对比是否有变化
        Team updatedTeam = new Team();
        BeanUtils.copyProperties(oldTeam, updatedTeam);
        BeanUtils.copyProperties(teamUpdateRequest, updatedTeam);
        // 检查是否有变化
        if (ObjectUtils.nullSafeEquals(oldTeam, updatedTeam)) {
            return true; // 没有变化，直接返回成功
        }
        // 更新
        return this.updateById(updatedTeam);
    }

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        // 非数据库校验
        // 2.队伍必须存在，只能加入未满、未过期的队伍
        Long teamId = teamJoinRequest.getTeamId();
        Team team = this.getTeamById(teamId);
        // 2.2.队伍是否过期
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        // 4.禁止加入私有的队伍
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(team.getStatus());
        if (statusEnum.equals(TeamStatusEnum.PRIVATE)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "私有队伍，无法加入");
        }
        // 5.如果加入队伍是加密的，必须密码匹配
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            String password = teamJoinRequest.getPassword();
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        // 数据库校验
        // 1.用户最多加入和创建5个队伍
        // synchronized
        synchronized (this ) {
            QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
            if (hasJoinNum > 5) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多加入和创建 5 个队伍");
            }
            // 2.1.队伍是否已满
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId", teamId);
            long hasJoinTeamNum = userTeamService.count(queryWrapper);
            if (hasJoinTeamNum >= team.getMaxNum()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
            }
            // 3.不能重复加入已加入的队伍（幂等性）
            queryWrapper.eq("userId", loginUser.getId());
            long hasJoin = userTeamService.count(queryWrapper);
            if (hasJoin > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入队伍");
            }
            // 新增用户-队伍关系
            UserTeam userTeam = new UserTeam();
            userTeam.setUserId(loginUser.getId());
            userTeam.setTeamId(teamId);
            userTeam.setJoinTime(new Date());
            return userTeamService.save(userTeam);
        }
    }

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        // 如果队伍不存在
        Long teamId = teamQuitRequest.getTeamId();
        Team team = this.getTeamById(teamId);

        // 校验我是否已加入队伍
        long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }

        // 如果队伍只剩下一个人，直接删除队伍
        long hasJoinTeamNum = this.countTeamUserByTeamId(teamId);
        if (hasJoinTeamNum == 1) {
            this.removeById(teamId);    // 删除队伍
        } else {
            // 如果队伍有多个人，队长退出，转移队长
            // 是队长
            if (team.getUserId() == userId) {
                // 将队伍转移给最早加入的用户（id最小的用户）
                // 1.查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new BusinessException(ErrorCode.SYSTRM_ERROR);
                }
                UserTeam nextTeamLeader = userTeamList.get(1);
                Long nextTeamLeaderUserId = nextTeamLeader.getUserId();
                // 2.更新队伍表队长信息
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderUserId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BusinessException(ErrorCode.SYSTRM_ERROR);
                }
            }
        }
        // 删除用户-队伍关系
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 删除队伍
     *
     * @param teamId
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long teamId, User loginUser) {
        // 1.校验请求参数
        // 2.校验队伍是否存在
        Team team = getTeamById(teamId);
        long newTeamId = team.getId();
        // 3.校验你是否为队伍队长
        if (team.getUserId() != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "队长才有权限删除队伍");
        }
        // 4.移除所有加入队伍的关联信息
        // select ? from user_team where teamId = ?
//        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("teamId", newTeamId);
//        boolean result = userTeamService.remove(queryWrapper);
//        if (!result) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除队伍关联信息失败");
//        }
//        // 5.删除队伍
//        return this.removeById(newTeamId);
        int relationResult = teamMapper.logicDeleteUserTeamByTeamId(newTeamId);
        if (relationResult <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除队伍关联信息失败");
        }
        // 5.删除队伍
        int teamResult = teamMapper.logicDeleteTeamById(newTeamId);
        return teamResult > 0;
    }

    /**
     * 根据id获取队伍信息
     *
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id不能为空");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }

    /**
     * 获取某队伍当前人数
     *
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(Long teamId) {
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        return userTeamService.count(queryWrapper);
    }
}


