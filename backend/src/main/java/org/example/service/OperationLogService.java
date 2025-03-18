package org.example.service;

import org.example.entity.RestBean;
import org.example.entity.dto.OperationLogEntity;
import org.example.entity.vo.response.OperationLogVO;

import java.util.List;

public interface OperationLogService {
    void saveLog(OperationLogEntity log);
    RestBean<List<OperationLogVO>> getLogs();
}
