package com.zzy.yupaobackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzy.yupaobackend.model.domain.User;
import com.zzy.yupaobackend.model.dto.UserDTO;
import com.zzy.yupaobackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
* @author zzy
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-09-03 17:52:19
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword  用户密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    User getSafeUser(User user);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     * @param tagNameList
     * @return
     */
    List<User> searchUserByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    Integer updateUser(UserDTO user, User loginUser);

    /**
     * 获取登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 鉴权，判断是否为管理员
     * @param request 请求
     * @return  是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 鉴权，判断是否为管理员
     * @param loginUser 登录用户
     * @return  是否为管理员
     */
    boolean isAdmin(User loginUser);

    /**
     * 获取最匹配的用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
}
