package com.zzy.yupaobackend.once;

import com.google.gson.Gson;
import com.zzy.yupaobackend.mapper.UserMapper;
import com.zzy.yupaobackend.model.domain.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 导入用户任务
 *
 */
@Component
public class InsertUsers {

    private static final Gson GSON =new Gson();

    @Resource
    private UserMapper userMapper;

    private static final String SALT = "zzy";
    private static final List<String> TAGS = Arrays.asList(
            "java", "C", "python", "男", "女", "大一", "大二", "大三", "大四"
    );

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE) // 5s后执行，之后每隔Long.MAX_VALUE毫秒执行一次
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();  // 计时器
        System.out.println("goodgoodgood");
        stopWatch.start();
        final int INSERT_NUM = 500;
        Random random = new Random();

        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假鱼皮" + (i + 1));
            user.setUserAccount("fakeuser" + (i + 1));
            user.setAvatarUrl("https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg");
            user.setGender(random.nextInt(2));  // 随机设置性别为0或1

            String password = "12345678";
            user.setUserPassword(password);
            String encodedPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            user.setUserPassword(encodedPassword);

            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags(generateRandomTags(random));
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("11111111");
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis()); // 输出总耗时
    }

    /**
     * 生成随机标签
     *
     * @return 随机标签的 JSON 字符串
     */
    private String generateRandomTags(Random random) {
        List<String> randomTags = TAGS.stream()
                .filter(tag -> random.nextBoolean())
                .collect(Collectors.toList());
        return GSON.toJson(randomTags);
    }

}
