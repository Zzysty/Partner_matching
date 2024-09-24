package com.zzy.yupaobackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzy.yupaobackend.common.BaseResponse;
import com.zzy.yupaobackend.common.ErrorCode;
import com.zzy.yupaobackend.common.ResultUtils;
import com.zzy.yupaobackend.exception.BusinessException;
import com.zzy.yupaobackend.model.domain.Team;
import com.zzy.yupaobackend.model.domain.User;
import com.zzy.yupaobackend.model.domain.UserTeam;
import com.zzy.yupaobackend.model.dto.TeamQuery;
import com.zzy.yupaobackend.model.request.*;
import com.zzy.yupaobackend.model.vo.TeamUserVO;
import com.zzy.yupaobackend.service.TeamService;
import com.zzy.yupaobackend.service.UserService;
import com.zzy.yupaobackend.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 组队接口
 *
 * @author zzy
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")    // 允许跨域
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;

    /**
     * 添加队伍
     *
     * @param teamAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        // 判空
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加队伍信息不能为空");
        }
        User loginUser = userService.getLoginUser(request); // 获取登录用户
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加队伍失败");
        }
        return ResultUtils.success(team.getId());
    }

//    /**
//     * 删除队伍
//     *
//     * @param id
//     * @return
//     */
//    @PostMapping("/delete")
//    public BaseResponse<Boolean> deleteTeam(@RequestBody long id) {
//        // 判空
//        if (id <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除队伍 id 不能为空");
//        }
//        boolean result = teamService.removeById(id);
//        if (!result) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除队伍失败");
//        }
//        return ResultUtils.success(true);
//    }

    /**
     * 修改队伍信息
     *
     * @param teamUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        // 判空
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "修改队伍信息不能为空");
        }
        User loginUser = userService.getLoginUser(request); // 获取登录用户
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "修改队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取队伍信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        System.out.println("id = " + id);
        // 判空
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询队伍 id 不能为空");
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return ResultUtils.success(team);
    }

//    @GetMapping("/list")
//    public BaseResponse<Team> getTeamList(TeamQuery teamQuery) {
//        // 判空
//        if (teamQuery == null) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询队伍信息列表不能为空");
//        }
//        Team team = new Team();
//        BeanUtils.copyProperties(teamQuery, team);
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
//        List<Team> teamList = teamService.list(queryWrapper);
//        return ResultUtils.success((Team) teamList);
//    }

    /**
     * 获取队伍列表
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> getTeamList(TeamQuery teamQuery, HttpServletRequest request) {
        // 判空
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询队伍信息列表不能为空");
        }
        // 是否为管理员
        boolean admin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, admin);    // 获取队伍列表

        // 判断当前用户是否加入队伍
        List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());  // 获取队伍 id 列表
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("userId", loginUser.getId());
            userTeamQueryWrapper.in("teamId", teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);   // 当前登录用户加入的队伍
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());   // 获取当前用户加入的队伍 id
            teamList.forEach(teamUserVO -> {
                // 如果当前队伍 id 在当前用户加入的队伍 id 中，则设置 hasJoin 为 true 表示已加入
                if (hasJoinTeamIdSet.contains(teamUserVO.getId())) {
                    teamUserVO.setHasJoin(true);
                }
            });
        } catch (Exception e) {
            log.error("获取当前用户信息失败", e);
        }

        // 获取当前已加入队伍的人数
        teamList.forEach(teamUserVO -> {
            QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId", teamUserVO.getId());
            int count = (int) userTeamService.count(queryWrapper);
            teamUserVO.setHasJoinNum(count);
        });

        return ResultUtils.success(teamList);
    }

    /**
     * 获取队伍列表
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamListByPage(TeamQuery teamQuery) {
        // 判空
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询队伍信息不能为空");
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(teamQuery, team);  // 将 TeamQuery 的属性拷贝到 Team 中
        } catch (Exception e) {
            log.error("拷贝属性失败", e);
            throw new BusinessException(ErrorCode.SYSTRM_ERROR, "查询队伍信息不能为空");
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize()),
                queryWrapper);
        return ResultUtils.success(teamPage);
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param request
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        // 判空
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入队伍信息不能为空");
        }
        User loginUser = userService.getLoginUser(request); // 获取登录用户
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param request
     * @return
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        // 判空
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "退出队伍信息不能为空");
        }
        User loginUser = userService.getLoginUser(request); // 获取登录用户
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 删除队伍
     * @param teamDeleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest, HttpServletRequest request) {
        // 判空
        if (teamDeleteRequest == null || teamDeleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除队伍信息不能为空");
        }
        long teamId = teamDeleteRequest.getId();
        User loginUser = userService.getLoginUser(request); // 获取登录用户
        boolean result = teamService.deleteTeam(teamId, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTRM_ERROR, "删除队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取我创建的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/add")
    public BaseResponse<List<TeamUserVO>> ListMyAddTeam(TeamQuery teamQuery, HttpServletRequest request) {
        // 判空
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询队伍信息列表不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId()); // 根据当前用户id查询
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我加入的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> ListMyJoinTeam(TeamQuery teamQuery, HttpServletRequest request) {
        // 判空
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询队伍信息列表不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 取出不重复的队伍 id
        // teamId userId map
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);

        List<TeamUserVO> teamList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(teamList);
    }
}
