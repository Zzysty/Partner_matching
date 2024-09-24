package com.zzy.yupaobackend.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzy.yupaobackend.model.domain.User;
import com.zzy.yupaobackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 指定重点用户
    private List<Long> mainUserList = Arrays.asList(1L, 2L, 3L);

    @Scheduled(cron = "0 5 12 * * *")
    public void doPreCacheRecommendUser() {
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");  // 获取锁
        try {
            // 只有一个线程能够获取到锁
            // watch dog 在当前节点存活时每10s给分布式锁的key续期 30s，当leaseTime = -1，启动watch dog
            if (lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)) {
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    // 缓存预热逻辑
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    // 写缓存
                    try {
                        valueOperations.set(redisKey, userPage, 60000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 释放自己的锁
            if (lock.isHeldByCurrentThread()) { // 判断当前线程是否持有锁
                lock.unlock();
            }
        }


    }
}
