package com.nova.mall.log.event;
import com.nova.mall.log.enums.BusinessType;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
@Data
@Builder
public class OperLogEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private Integer businessType;
    private String method;
    private String requestMethod;
    private String operName;
    private String operUrl;
    private String operIp;
    private String operParam;
    private String jsonResult;
    private Integer status;
    private String errorMsg;
    private Long costTime;
    private Long operTime;
    public static Integer typeCode(BusinessType type) {
        return type == null ? BusinessType.OTHER.getCode() : type.getCode();
    }
}
