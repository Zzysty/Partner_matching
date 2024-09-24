package com.zzy.yupaobackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzy.yupaobackend.model.domain.UserTeam;
import com.zzy.yupaobackend.mapper.UserTeamMapper;
import com.zzy.yupaobackend.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author zzy
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-05-17 09:53:05
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




