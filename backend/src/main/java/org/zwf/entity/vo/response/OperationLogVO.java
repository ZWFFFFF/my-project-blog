package org.zwf.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class OperationLogVO {
    private Integer id;
    private Integer operatorId;
    private String operator;
    private String operationType;
    private String operationDetail;
    private String result;
    private Date operationTime;
}
