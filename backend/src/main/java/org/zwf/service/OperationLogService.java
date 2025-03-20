package org.zwf.service;

import org.zwf.entity.RestBean;
import org.zwf.entity.dto.OperationLogEntity;
import org.zwf.entity.vo.response.OperationLogVO;

import java.util.List;

public interface OperationLogService {
    void saveLog(OperationLogEntity log);
    RestBean<List<OperationLogVO>> getLogs();
}
