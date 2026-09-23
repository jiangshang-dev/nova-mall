package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.dto.CategorySaveDTO;
import com.nova.mall.entity.MallGoods;
import com.nova.mall.entity.MallGoodsCategory;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.MallGoodsCategoryMapper;
import com.nova.mall.mapper.MallGoodsMapper;
import com.nova.mall.service.MallGoodsCategoryService;
import com.nova.mall.vo.CategoryTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MallGoodsCategoryServiceImpl extends ServiceImpl<MallGoodsCategoryMapper, MallGoodsCategory>
        implements MallGoodsCategoryService {
    private final MallGoodsMapper mallGoodsMapper;

    @Override
    public List<MallGoodsCategory> listAll() {
        return this.list(new LambdaQueryWrapper<MallGoodsCategory>()
                .eq(MallGoodsCategory::getStatus, 1)
                .orderByAsc(MallGoodsCategory::getSort)
                .orderByAsc(MallGoodsCategory::getId));
    }

    @Override
    public List<CategoryTreeVO> listTree(boolean onlyEnabled) {
        List<MallGoodsCategory> all = this.list(new LambdaQueryWrapper<MallGoodsCategory>()
                .eq(onlyEnabled, MallGoodsCategory::getStatus, 1)
                .orderByAsc(MallGoodsCategory::getSort)
                .orderByAsc(MallGoodsCategory::getId));
        return buildTree(all);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallGoodsCategory saveCategory(CategorySaveDTO dto) {
        if (StrUtil.isBlank(dto.getName())) throw new ServiceException("请填写分类名称");
        Integer parentId = dto.getParentId() == null ? 0 : dto.getParentId();
        if (parentId < 0) parentId = 0;
        if (parentId > 0) {
            MallGoodsCategory parent = this.getById(parentId);
            if (parent == null) throw new ServiceException("上级分类不存在");
        }

        MallGoodsCategory entity;
        if (dto.getId() != null) {
            entity = this.getById(dto.getId());
            if (entity == null) throw new ServiceException("分类不存在");
            if (Objects.equals(dto.getId(), parentId)) throw new ServiceException("不能将自己设为上级");
            if (parentId > 0 && isDescendant(dto.getId(), parentId)) {
                throw new ServiceException("不能将下级分类设为上级");
            }
        } else {
            entity = new MallGoodsCategory();
        }
        entity.setParentId(parentId);
        entity.setName(dto.getName().trim());
        entity.setSort(dto.getSort() == null ? 0 : dto.getSort());
        entity.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        this.saveOrUpdate(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCategory(Integer id) {
        MallGoodsCategory entity = this.getById(id);
        if (entity == null) throw new ServiceException("分类不存在");
        long childCount = this.count(new LambdaQueryWrapper<MallGoodsCategory>()
                .eq(MallGoodsCategory::getParentId, id));
        if (childCount > 0) throw new ServiceException("请先删除子分类");
        long goodsCount = mallGoodsMapper.selectCount(new LambdaQueryWrapper<MallGoods>()
                .eq(MallGoods::getCategoryId, id));
        if (goodsCount > 0) throw new ServiceException("该分类下仍有商品，无法删除");
        this.removeById(id);
    }

    @Override
    public Set<Integer> collectSelfAndDescendantIds(Integer categoryId) {
        Set<Integer> result = new LinkedHashSet<>();
        if (categoryId == null) return result;
        List<MallGoodsCategory> all = this.list(new LambdaQueryWrapper<MallGoodsCategory>()
                .select(MallGoodsCategory::getId, MallGoodsCategory::getParentId));
        Map<Integer, List<Integer>> childrenMap = all.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getParentId() == null ? 0 : c.getParentId(),
                        Collectors.mapping(MallGoodsCategory::getId, Collectors.toList())));
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(categoryId);
        while (!stack.isEmpty()) {
            Integer cur = stack.pop();
            if (!result.add(cur)) continue;
            List<Integer> kids = childrenMap.getOrDefault(cur, Collections.emptyList());
            for (Integer kid : kids) stack.push(kid);
        }
        return result;
    }

    /** targetId 是否是 rootId 的子孙 */
    private boolean isDescendant(Integer rootId, Integer targetId) {
        return collectSelfAndDescendantIds(rootId).contains(targetId);
    }

    private List<CategoryTreeVO> buildTree(List<MallGoodsCategory> all) {
        Map<Integer, CategoryTreeVO> map = new LinkedHashMap<>();
        for (MallGoodsCategory c : all) {
            CategoryTreeVO vo = new CategoryTreeVO();
            vo.setId(c.getId());
            vo.setParentId(c.getParentId() == null ? 0 : c.getParentId());
            vo.setName(c.getName());
            vo.setSort(c.getSort());
            vo.setStatus(c.getStatus());
            map.put(c.getId(), vo);
        }
        List<CategoryTreeVO> roots = new ArrayList<>();
        for (CategoryTreeVO vo : map.values()) {
            Integer pid = vo.getParentId() == null ? 0 : vo.getParentId();
            if (pid == 0 || !map.containsKey(pid)) {
                roots.add(vo);
            } else {
                map.get(pid).getChildren().add(vo);
            }
        }
        pruneEmptyChildren(roots);
        return roots;
    }

    private void pruneEmptyChildren(List<CategoryTreeVO> nodes) {
        if (nodes == null) return;
        for (CategoryTreeVO n : nodes) {
            if (n.getChildren() == null || n.getChildren().isEmpty()) {
                n.setChildren(null);
            } else {
                pruneEmptyChildren(n.getChildren());
            }
        }
    }
}