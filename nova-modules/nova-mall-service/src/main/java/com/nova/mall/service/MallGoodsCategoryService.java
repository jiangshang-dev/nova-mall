package com.nova.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.dto.CategorySaveDTO;
import com.nova.mall.entity.MallGoodsCategory;
import com.nova.mall.vo.CategoryTreeVO;

import java.util.List;
import java.util.Set;

public interface MallGoodsCategoryService extends IService<MallGoodsCategory> {
    /** 启用中的扁平列表（兼容旧接口） */
    List<MallGoodsCategory> listAll();

    /** 树形分类；onlyEnabled=true 仅返回启用节点 */
    List<CategoryTreeVO> listTree(boolean onlyEnabled);

    MallGoodsCategory saveCategory(CategorySaveDTO dto);

    void removeCategory(Integer id);

    /** 自身及所有子孙分类 ID */
    Set<Integer> collectSelfAndDescendantIds(Integer categoryId);
}
