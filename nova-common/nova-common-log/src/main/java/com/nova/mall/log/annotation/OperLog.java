package com.nova.mall.log.annotation;
import com.nova.mall.log.enums.BusinessType;
import java.lang.annotation.*;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {
    String title() default "";
    BusinessType businessType() default BusinessType.OTHER;
}
