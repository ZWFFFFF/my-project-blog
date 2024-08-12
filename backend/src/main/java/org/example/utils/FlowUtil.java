package org.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 请求限流工具（redis实现）
 */
@Component
public class FlowUtil {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 针对于单次频率限制，请求成功后，在锁定时间内不得再次进行请求，如3秒内不能再次发起请求
     * @param key 根据ip生成的键
     * @param blockTime ip锁定时间，单位为秒
     * @return true表示请求成功进行限制，false表示请求失败
     */
    public boolean limitOnceCheck(String key, int blockTime) {
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return false;
        } else {
            stringRedisTemplate.opsForValue().set(key, "", blockTime, TimeUnit.SECONDS);
            return true;
        }
    }
}
