package com.nova.mall.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nova.mall.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    List<String> selectPermsByUserId(@Param("userId") Integer userId);
    List<SysPermission> selectMenusByUserId(@Param("userId") Integer userId);
    List<String> selectRoleCodesByUserId(@Param("userId") Integer userId);
}
