package org.example.service.serviceImpl;

import jakarta.annotation.Resource;
import org.example.entity.dto.OperationLogEntity;
import org.example.mapper.OperationLogMapper;
import org.example.service.OperationLogService;
import org.springframework.stereotype.Service;

/**
 * 日志记录服务实现类
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {
    @Resource
    private OperationLogMapper mapper;

    @Override
    public void saveLog(OperationLogEntity log) {
        // 异步写入 MySQL
        new Thread(() -> {
            mapper.insertLog(log);
        }).start();
    }
}
