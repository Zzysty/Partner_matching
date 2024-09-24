package com.zzy.yupaobackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzy.yupaobackend.common.BaseResponse;
import com.zzy.yupaobackend.common.ErrorCode;
import com.zzy.yupaobackend.common.ResultUtils;
import com.zzy.yupaobackend.exception.BusinessException;
import com.zzy.yupaobackend.model.domain.User;
import com.zzy.yupaobackend.model.dto.UserDTO;
import com.zzy.yupaobackend.model.request.UserLoginRequest;
import com.zzy.yupaobackend.model.request.UserRegisterRequest;
import com.zzy.yupaobackend.model.vo.UserVO;
import com.zzy.yupaobackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.zzy.yupaobackend.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author zzy
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求体
     * @return
     */
    @PostMapping("/register")
    public BaseResponse userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户注册请求体不能为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            // 用户账号、密码、校验密码不能为空
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号、密码、校验密码不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户登录请求体不能为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            // 用户账号、密码不能为空
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号、密码不能为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求不能为空");
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafeUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 查询用户列表（仅管理员）
     *
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非管理员，无权限");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不能为空");
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 获取最匹配的用户
     *
     * @param num
     * @param request
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num, HttpServletRequest request) {
        if (num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, user));
    }

    /**
     * 推荐多个用户
     * @param pageNum
     * @param pageSize
     * @param request
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                   HttpServletRequest request) {
        // 使用redis缓存
        User loginUser = userService.getLoginUser(request);
        // 如果有缓存，直接读取缓存
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
        Page<User> userPage = (Page<User>) valueOperations.get(redisKey);
        if (userPage != null) {
            return ResultUtils.success(userPage);
        }
        // 无缓存，查数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        // 写缓存
        try {
            valueOperations.set(redisKey, userPage, 60000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return ResultUtils.success(userPage);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody UserDTO userDto, HttpServletRequest request) {
        // 判空
        if (userDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        Integer result = userService.updateUser(userDto, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 删除用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "非管理员");   // 非管理员，返回false
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "没有该用户");
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

}
