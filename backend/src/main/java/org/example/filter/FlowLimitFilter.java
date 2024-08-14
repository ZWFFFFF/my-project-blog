package org.example.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.RestBean;
import org.example.utils.Const;
import org.example.utils.FlowUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 限流控制过滤器
 * 防止用户高频请求接口，使用Redis进行限流
 */
@Slf4j
@Component
@Order(Const.FILTER_ORDER_FLOW_LIMIT)
public class FlowLimitFilter extends HttpFilter {
    //指定时间内最大请求次数限制
    @Value("${my-config.web.flow.limit}")
    int limit;
    //计数时间周期
    @Value("${my-config.web.flow.period}")
    int period;
    //超出请求限制封禁时间
    @Value("${my-config.web.flow.block}")
    int block;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private FlowUtil flowUtil;

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        if (this.tryCount(ip)) {
            chain.doFilter(request, response);
        } else {
            this.writeBlockMessage(response);
        }
    }

    /**
     * 尝试对指定IP地址请求计数，如果被限制则无法继续访问
     * @param ip IP地址
     * @return 是否可以继续请求
     */
    private boolean tryCount(String ip) {
        synchronized (ip.intern()) { // 保证线程安全，串行处理
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.FLOW_LIMIT_BLOCK + ip)))
                return false;
            return flowUtil.limitPeriodCheck(Const.FLOW_LIMIT_COUNTER + ip, Const.FLOW_LIMIT_BLOCK + ip, limit, period, block);
        }
    }

    /**
     * 为响应编写拦截内容，提示用户操作频繁
     * @param response 响应对象
     * @throws IOException IO异常
     */
    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden("请求过于频繁，请稍后再试").asJsonString());
    }
}
