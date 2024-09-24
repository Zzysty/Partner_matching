package com.zzy.yupaobackend.service;

import com.zzy.yupaobackend.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    UserService userService;

    @Test
    void testAddUser() {
        User user = new User();

        user.setUsername("dogyupi");
        user.setUserAccount("yupi");
        user.setAvatarUrl("http://sqlmother.yupi.icu/assets/logo.e43a01a1.png");
        user.setGender(0);
        user.setUserPassword("88888888");
        user.setPhone("17573204410");
        user.setEmail("1184271384@qq.com");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        // 断言，判断结果是否为true
        Assertions.assertTrue(result);
    }

    @Test
//    测试用户登录
    void userLogin() {
        String userAccount = "zzyy";
        String userPassword = "12345678";
        HttpServletRequest request = null;
        User result = userService.userLogin(userAccount, userPassword, request);
        Assertions.assertEquals(-1, result);
    }

    @Test
    void userRegister() {
        String userAccount = "zzyy";
        String userPassword = "88888888";
        String checkPassword = "88888888";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

//        userAccount = "zy";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, result);
//
//        userAccount = "zzyy";
//        userPassword = "123456";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, result);
//
//        userAccount = "yu pi";
//        userPassword = "12345678";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, result);
//        checkPassword = "123456789";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, result);
//
//        userAccount = "123";
//        checkPassword = "12345678";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertEquals(-1, result);
//
//        userAccount = "zzyyisyyds";
//        result = userService.userRegister(userAccount, userPassword, checkPassword);
//        Assertions.assertTrue(result > 0);

        userAccount = "yupi";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

    }

    @Test
    void testSearchUserByTags() {
        List<String> tagNameList = Arrays.asList("java", "python");
        List<User> userList = userService.searchUserByTags(tagNameList);
//        Assert.assertNotNull(userList);
        System.out.println(userList);
    }
}