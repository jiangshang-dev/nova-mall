package com.nova.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.base.LoginUser;
import com.nova.mall.dto.CartAddDTO;
import com.nova.mall.entity.MallCart;
import com.nova.mall.entity.MallGoods;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.MallCartMapper;
import com.nova.mall.mapper.MallGoodsMapper;
import com.nova.mall.service.MallCartService;
import com.nova.mall.utils.UserUtil;
import com.nova.mall.vo.CartItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MallCartServiceImpl extends ServiceImpl<MallCartMapper, MallCart> implements MallCartService {
    private final MallGoodsMapper mallGoodsMapper;

    private Integer requireUserId() {
        LoginUser user = UserUtil.getUser();
        if (user == null || user.getId() == null) throw new ServiceException(401, "请先登录");
        return user.getId();
    }

    @Override
    public List<CartItemVO> listMine() {
        Integer userId = requireUserId();
        List<MallCart> carts = this.list(new LambdaQueryWrapper<MallCart>()
                .eq(MallCart::getUserId, userId)
                .orderByDesc(MallCart::getId));
        List<CartItemVO> result = new ArrayList<>();
        for (MallCart cart : carts) {
            MallGoods goods = mallGoodsMapper.selectById(cart.getGoodsId());
            if (goods == null) continue;
            CartItemVO vo = new CartItemVO();
            vo.setId(cart.getId());
            vo.setGoodsId(cart.getGoodsId());
            vo.setQuantity(cart.getQuantity());
            vo.setChecked(cart.getChecked());
            vo.setName(goods.getName());
            vo.setCover(goods.getCover());
            vo.setPrice(goods.getPrice());
            vo.setOriginalPrice(goods.getOriginalPrice());
            vo.setStock(goods.getStock());
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(CartAddDTO dto) {
        Integer userId = requireUserId();
        MallGoods goods = mallGoodsMapper.selectById(dto.getGoodsId());
        if (goods == null || goods.getStatus() == null || goods.getStatus() != 1) {
            throw new ServiceException("商品不存在或已下架");
        }
        int qty = dto.getQuantity() == null || dto.getQuantity() < 1 ? 1 : dto.getQuantity();
        MallCart exist = this.getOne(new LambdaQueryWrapper<MallCart>()
                .eq(MallCart::getUserId, userId)
                .eq(MallCart::getGoodsId, dto.getGoodsId())
                .last("LIMIT 1"));
        if (exist != null) {
            exist.setQuantity((exist.getQuantity() == null ? 0 : exist.getQuantity()) + qty);
            this.updateById(exist);
            return;
        }
        MallCart cart = new MallCart();
        cart.setUserId(userId);
        cart.setGoodsId(dto.getGoodsId());
        cart.setQuantity(qty);
        cart.setChecked(1);
        this.save(cart);
    }

    @Override
    public void updateQuantity(Long id, Integer quantity) {
        Integer userId = requireUserId();
        MallCart cart = this.getById(id);
        if (cart == null || !userId.equals(cart.getUserId())) throw new ServiceException("购物车项不存在");
        if (quantity == null || quantity < 1) throw new ServiceException("数量至少为 1");
        cart.setQuantity(quantity);
        this.updateById(cart);
    }

    @Override
    public void remove(Long id) {
        Integer userId = requireUserId();
        MallCart cart = this.getById(id);
        if (cart == null || !userId.equals(cart.getUserId())) throw new ServiceException("购物车项不存在");
        this.removeById(id);
    }

    @Override
    public void clearChecked() {
        Integer userId = requireUserId();
        this.remove(new LambdaQueryWrapper<MallCart>()
                .eq(MallCart::getUserId, userId)
                .eq(MallCart::getChecked, 1));
    }
}
