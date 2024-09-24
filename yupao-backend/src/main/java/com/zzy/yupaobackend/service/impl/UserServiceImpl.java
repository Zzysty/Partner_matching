package com.zzy.yupaobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zzy.yupaobackend.common.ErrorCode;
import com.zzy.yupaobackend.common.ResultUtils;
import com.zzy.yupaobackend.contant.UserConstant;
import com.zzy.yupaobackend.exception.BusinessException;
import com.zzy.yupaobackend.model.domain.User;
import com.zzy.yupaobackend.model.dto.UserDTO;
import com.zzy.yupaobackend.model.vo.UserVO;
import com.zzy.yupaobackend.service.UserService;

import com.zzy.yupaobackend.mapper.UserMapper;
import com.zzy.yupaobackend.utils.AlgorithmUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.zzy.yupaobackend.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
* @author zzy
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-09-03 17:52:19
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 加密盐,混淆密码
     */
    private static final String SALT = "zzy";

    // 用户注册
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            // 用户账号、密码、校验密码不能为空
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号、密码、校验密码不能为空");
        }
        if (userAccount.length() < 4) {
            // 用户账号长度不能小于4
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度不能小于4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            // 用户密码长度不能小于8
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度不能小于8");
        }
        // 账户不能包含特殊字符
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }
        // 密码和校验密码必须一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码必须一致");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能重复");
        }
        // 2.加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户注册失败");
        }
        return user.getId();
    }

    // 用户登录
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            // 用户账号、密码、校验密码不能为空
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号、密码不能为空");
        }
        if (userAccount.length() < 4) {
            // 用户账号长度不能小于4
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号长度不能小于4");
        }
        if (userPassword.length() < 8) {
            // 用户密码长度不能小于8
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码长度不能小于8");
        }
        // 账户不能包含特殊字符
        String validPattern = "\\pP|\\pS|\\s+";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }
        // 2.加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", newPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount can not match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号或密码错误");
        }
        // 3.用户脱敏
        User safeUser = getSafeUser(user);
        // 4.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);
        return safeUser;
    }

    // 用户脱敏
    @Override
    public User getSafeUser(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息不能为空");
        }
        User safeUser = new User();
        safeUser.setId(user.getId());
        safeUser.setUsername(user.getUsername());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setPlanetCode(user.getPlanetCode());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(user.getUpdateTime());
        safeUser.setTags(user.getTags());
        safeUser.setProfile(user.getProfile());
        return safeUser;
    }

    // 用户注销
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);     // 移除用户登录态
        return ResultUtils.success("用户注销成功").getCode();
    }

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {
        // 判空
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.sql查询
        // 循环拼接 tag 到 queryWrapper
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        for (String tagName : tagNameList) {
//            queryWrapper = queryWrapper.like("tags", tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        return userList.stream().map(this::getSafeUser).collect(Collectors.toList());

        // 2.sql结合内存
        // 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
//            if (StringUtils.isBlank(tagsStr)) {
//                return false;
//            }
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>(){}.getType());
            // 判断是否包含所有标签 Optional 替换掉 if
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName: tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafeUser).collect(Collectors.toList());
    }

    /**
     * 更新用户信息
     * @param userDto
     * @return
     */
    @Override
    public Integer updateUser(UserDTO userDto, User loginUser) {
        // 校验权限
        long userId = loginUser.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户id不能为空");
        }
        // 如果用户没有传任何要更新的值，直接返回，不用执行 update 操作
        if (userDto == null) {
            return 0;
        }
        // if 管理员，可以修改任意用户信息
        // if 非管理员，只能修改自己的信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非管理员并且不是登录用户，无权限修改用户信息");
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 将 UserDTO 中的值复制到 User 对象中
        oldUser.setAvatarUrl(userDto.getAvatarUrl());
        oldUser.setUsername(userDto.getUsername());
        oldUser.setGender(userDto.getGender());
        oldUser.setPhone(userDto.getPhone());
        oldUser.setEmail(userDto.getEmail());
        // 更新用户信息
        // 更新用户信息
        return userMapper.updateById(oldUser);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObject == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户未登录");
        }
        return (User) userObject;
    }

    /**
     * 鉴权，判断是否为管理员
     * @param request 请求
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObject = request.getSession().getAttribute(USER_LOGIN_STATE);    // 获取登录用户
        User user = (User) userObject;  // 强转为 User 对象
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;   // 判断是否为管理员
    }

    /**
     * 鉴权，判断是否为管理员
     * @param loginUser 登录用户
     * @return
     */
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    /**
     * 获取最匹配的用户
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);  // 获取用户列表，且只取 id 和 tags 字段，筛掉了没有 tags 的用户
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {}.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (User user : userList) {
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {}.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(this::getSafeUser)
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }
}