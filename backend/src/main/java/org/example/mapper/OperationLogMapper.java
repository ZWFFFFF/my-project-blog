package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.dto.OperationLogEntity;

@Mapper
public interface OperationLogMapper {
    @Insert("INSERT INTO operation_log (operation_time, operatorId, operation_type, operation_detail, operation_result) " +
            "VALUES (#{operationTime}, #{operatorId}, #{operationType}, #{operationDetail}, #{operationResult})")
    void insertLog(OperationLogEntity log);
}
