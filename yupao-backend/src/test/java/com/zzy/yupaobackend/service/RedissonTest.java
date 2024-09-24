package com.zzy.yupaobackend.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redisson 测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test() {
        // list
        List<String> list = new ArrayList<>();
        list.add("dog");
        System.out.println(list.get(0));
//        list.remove(0);

        RList<String> rlist = redissonClient.getList("List");
        System.out.println("rlist:" + rlist.get(0));

        // map
        Map<String, Integer> map = new HashMap<>();
        map.put("dog", 1);
        System.out.println("map:" + map.get("dog"));

        RMap<String, Integer> rMap = redissonClient.getMap("Map");
        rMap.put("dog", 1);
        System.out.println("rMap:" + rMap.get("dog"));
        // set
    }
}
