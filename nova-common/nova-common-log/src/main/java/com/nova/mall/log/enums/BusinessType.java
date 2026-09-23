package com.nova.mall.log.enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum BusinessType {
    OTHER(0), INSERT(1), UPDATE(2), DELETE(3), SELECT(4);
    private final int code;
}
