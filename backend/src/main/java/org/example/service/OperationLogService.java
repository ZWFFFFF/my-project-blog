package org.example.service;

import org.example.entity.dto.OperationLogEntity;

public interface OperationLogService {
    void saveLog(OperationLogEntity log);
}
