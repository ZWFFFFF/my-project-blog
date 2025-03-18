package org.example.service.serviceImpl;

import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.dto.OperationLogEntity;
import org.example.entity.vo.response.OperationLogVO;
import org.example.mapper.OperationLogMapper;
import org.example.service.OperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 日志记录服务实现类
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Resource
    private OperationLogMapper mapper;

    /**
     * 保存操作日志
     * @param log 操作日志实体
     */
    @Override
    public void saveLog(OperationLogEntity log) {
        // 异步写入 MySQL
        new Thread(() -> {
            mapper.insertLog(log);
        }).start();
    }

    /**
     * 获取操作日志
     * @return 响应实体
     */
    @Override
    public RestBean<List<OperationLogVO>> getLogs() {
        return RestBean.success(mapper.getOperationLogs());
    }
}
