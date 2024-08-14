package org.example.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
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

    /**
     * 多次请求的限制，如3秒内最多请求10次，超过10次则锁定30秒
     * @param counterKey 请求计数器键
     * @param blockKey 请求锁定键
     * @param limit 请求限制次数
     * @param period 请求限制时间，单位秒
     * @param blockTime 请求锁定时间，单位秒
     * @return true表示请求成功counter+1，false表示请求失败，以达到最大请求次数，进行限制
     */
    public boolean limitPeriodCheck(String counterKey, String blockKey, int limit, int period, int blockTime) {
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(counterKey))) {
            Long increment = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(counterKey)).orElse(0L);
            if(increment > limit) {
                stringRedisTemplate.opsForValue().set(blockKey, "", blockTime, TimeUnit.SECONDS);
                return false;
            }
        } else {
            stringRedisTemplate.opsForValue().set(counterKey, "1", period, TimeUnit.SECONDS);
        }
        return true;
    }
}
