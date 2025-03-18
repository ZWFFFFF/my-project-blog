package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.dto.OperationLogEntity;
import org.example.entity.vo.response.OperationLogVO;

import java.util.List;

@Mapper
public interface OperationLogMapper {
    @Insert("INSERT INTO operation_log (operation_time, operatorId, operation_type, operation_detail, operation_result) " +
            "VALUES (#{operationTime}, #{operatorId}, #{operationType}, #{operationDetail}, #{operationResult})")
    void insertLog(OperationLogEntity log);
    @Select("select ol.id, ol.operatorId, a.username as operator, ol.operation_type as operationType, ol.operation_detail as operationDetail, ol.operation_result as result, ol.operation_time as operationTime " +
            "from operation_log ol left join account a on ol.operatorId = a.id")
    List<OperationLogVO> getOperationLogs();
}
