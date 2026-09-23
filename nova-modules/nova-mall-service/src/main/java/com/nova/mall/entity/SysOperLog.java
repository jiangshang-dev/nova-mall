package com.nova.mall.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
@Data @TableName("sys_oper_log")
public class SysOperLog {
  @TableId(type = IdType.AUTO) private Long id;
  private String title; private Integer businessType; private String method;
  private String requestMethod; private String operName; private String operUrl;
  private String operIp; private String operParam; private String jsonResult;
  private Integer status; private String errorMsg; private Long costTime; private Long operTime;
}
