package org.zwf.entity.dto;

import lombok.Data;
import java.util.Date;

/**
 * 操作日志实体类
 */
@Data
public class OperationLogEntity {
    private Integer id;
    private Date operationTime;
    private Integer operatorId;
    private String operationType;
    private String operationDetail;
    private String operationResult;
}