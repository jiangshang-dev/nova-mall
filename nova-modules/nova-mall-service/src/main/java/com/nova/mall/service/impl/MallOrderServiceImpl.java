package com.nova.mall.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.base.LoginUser;
import com.nova.mall.dto.OrderCreateDTO;
import com.nova.mall.entity.*;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.*;
import com.nova.mall.service.MallCartService;
import com.nova.mall.service.MallFreightService;
import com.nova.mall.service.MallOrderService;
import com.nova.mall.utils.UserUtil;
import com.nova.mall.vo.FreightOptionVO;
import com.nova.mall.vo.OrderItemVO;
import com.nova.mall.vo.OrderVO;
import com.nova.mall.vo.PayInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MallOrderServiceImpl extends ServiceImpl<MallOrderMapper, MallOrder> implements MallOrderService {
    private final MallOrderItemMapper orderItemMapper;
    private final MallAddressMapper addressMapper;
    private final MallGoodsMapper goodsMapper;
    private final MallCartMapper cartMapper;
    private final MallCartService cartService;
    private final MallFreightService freightService;

    private Integer requireUserId() {
        LoginUser user = UserUtil.getUser();
        if (user == null || user.getId() == null) throw new ServiceException(401, "请先登录");
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO create(OrderCreateDTO dto) {
        Integer userId = requireUserId();
        String payType = normalizePayType(dto.getPayType());
        MallAddress address = addressMapper.selectById(dto.getAddressId());
        if (address == null || !userId.equals(address.getUserId())) {
            throw new ServiceException("收货地址不存在");
        }
        assertLocalCity(address);

        List<Line> lines = buildLines(userId, dto);
        if (lines.isEmpty()) throw new ServiceException("没有可结算的商品");

        BigDecimal goodsAmount = lines.stream()
                .map(l -> l.price.multiply(BigDecimal.valueOf(l.quantity)))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        FreightOptionVO freightOpt = freightService.calc(dto.getDeliveryType(), goodsAmount);
        BigDecimal freight = freightOpt.getPayableFreight();
        BigDecimal total = goodsAmount.add(freight).setScale(2, RoundingMode.HALF_UP);

        String orderNo = "NM" + System.currentTimeMillis() + IdUtil.getSnowflakeNextIdStr().substring(10);

        MallOrder order = new MallOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setPayType(payType);
        order.setDeliveryType(freightOpt.getDeliveryType());
        order.setDeliveryName(freightOpt.getDeliveryName());
        order.setTotalAmount(total);
        order.setGoodsAmount(goodsAmount);
        order.setFreightAmount(freight);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(formatAddress(address));
        order.setRemark(StrUtil.blankToDefault(dto.getRemark(), null));

        if ("cod".equals(payType)) {
            // 货到付款：下单即占库存，待运营核销
            order.setStatus(5);
            deductStock(lines);
        } else {
            order.setStatus(0);
        }
        this.save(order);

        for (Line line : lines) {
            MallOrderItem item = new MallOrderItem();
            item.setOrderId(order.getId());
            item.setOrderNo(orderNo);
            item.setGoodsId(line.goodsId);
            item.setGoodsName(line.name);
            item.setGoodsCover(line.cover);
            item.setPrice(line.price);
            item.setQuantity(line.quantity);
            item.setAmount(line.price.multiply(BigDecimal.valueOf(line.quantity)).setScale(2, RoundingMode.HALF_UP));
            orderItemMapper.insert(item);
            if (line.cartId != null) {
                cartService.remove(line.cartId);
            }
        }
        return toVO(order, true);
    }

    private void assertLocalCity(MallAddress address) {
        String serviceCity = freightService.getServiceCity();
        String city = StrUtil.blankToDefault(address.getCity(), "");
        String province = StrUtil.blankToDefault(address.getProvince(), "");
        if (!city.contains(serviceCity.replace("市", ""))
                && !serviceCity.contains(city.replace("市", ""))
                && !province.contains(serviceCity.replace("市", ""))
                && !serviceCity.equals(city)
                && !serviceCity.equals(province)) {
            throw new ServiceException("仅支持「" + serviceCity + "」同城配送，暂不支持外地");
        }
    }

    private void deductStock(List<Line> lines) {
        for (Line line : lines) {
            MallGoods goods = goodsMapper.selectById(line.goodsId);
            if (goods == null) continue;
            int stock = goods.getStock() == null ? 0 : goods.getStock();
            if (stock < line.quantity) throw new ServiceException("库存不足：" + goods.getName());
            goods.setStock(stock - line.quantity);
            goods.setSales((goods.getSales() == null ? 0 : goods.getSales()) + line.quantity);
            goodsMapper.updateById(goods);
        }
    }

    private List<Line> buildLines(Integer userId, OrderCreateDTO dto) {
        List<Line> lines = new ArrayList<>();
        if (dto.getGoodsId() != null) {
            int qty = dto.getQuantity() == null || dto.getQuantity() < 1 ? 1 : dto.getQuantity();
            lines.add(fromGoods(dto.getGoodsId(), qty, null));
            return lines;
        }
        LambdaQueryWrapper<MallCart> qw = new LambdaQueryWrapper<MallCart>().eq(MallCart::getUserId, userId);
        if (dto.getCartItemIds() != null && !dto.getCartItemIds().isEmpty()) {
            qw.in(MallCart::getId, dto.getCartItemIds());
        } else {
            qw.eq(MallCart::getChecked, 1);
        }
        List<MallCart> carts = cartMapper.selectList(qw);
        if (carts.isEmpty()) throw new ServiceException("购物车没有可结算商品");
        for (MallCart cart : carts) {
            lines.add(fromGoods(cart.getGoodsId(), cart.getQuantity(), cart.getId()));
        }
        return lines;
    }

    private Line fromGoods(Integer goodsId, int quantity, Long cartId) {
        MallGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getStatus() == null || goods.getStatus() != 1) {
            throw new ServiceException("商品不存在或已下架");
        }
        if (goods.getStock() == null || goods.getStock() < quantity) {
            throw new ServiceException("库存不足：" + goods.getName());
        }
        Line line = new Line();
        line.cartId = cartId;
        line.goodsId = goodsId;
        line.name = goods.getName();
        line.cover = goods.getCover();
        line.price = goods.getPrice() == null ? BigDecimal.ZERO : goods.getPrice();
        line.quantity = quantity;
        return line;
    }

    private static String formatAddress(MallAddress a) {
        return StrUtil.nullToEmpty(a.getProvince())
                + StrUtil.nullToEmpty(a.getCity())
                + StrUtil.nullToEmpty(a.getDistrict())
                + StrUtil.nullToEmpty(a.getDetail());
    }

    private static String normalizePayType(String payType) {
        if ("alipay".equalsIgnoreCase(payType)) return "alipay";
        if ("wxpay".equalsIgnoreCase(payType) || "wechat".equalsIgnoreCase(payType)) return "wxpay";
        if ("cod".equalsIgnoreCase(payType) || "cash".equalsIgnoreCase(payType)) return "cod";
        throw new ServiceException("请选择支付宝、微信或货到付款");
    }

    @Override
    public OrderVO detailMine(String orderNo) {
        Integer userId = requireUserId();
        MallOrder order = getByOrderNo(orderNo);
        if (!userId.equals(order.getUserId())) throw new ServiceException("订单不存在");
        return toVO(order, true);
    }

    @Override
    public Page<OrderVO> pageMine(long pageNum, long pageSize) {
        Integer userId = requireUserId();
        Page<MallOrder> page = this.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<MallOrder>()
                        .eq(MallOrder::getUserId, userId)
                        .orderByDesc(MallOrder::getId));
        return mapPage(page);
    }

    @Override
    public Page<OrderVO> pageAdmin(long pageNum, long pageSize, String orderNo, Integer status) {
        Page<MallOrder> page = this.page(new Page<>(pageNum, pageSize),
                new LambdaQueryWrapper<MallOrder>()
                        .like(StrUtil.isNotBlank(orderNo), MallOrder::getOrderNo, orderNo)
                        .eq(status != null, MallOrder::getStatus, status)
                        .orderByDesc(MallOrder::getId));
        return mapPage(page);
    }

    @Override
    public PayInfoVO preparePay(String orderNo, String payType) {
        Integer userId = requireUserId();
        MallOrder order = getByOrderNo(orderNo);
        if (!userId.equals(order.getUserId())) throw new ServiceException("订单不存在");
        if ("cod".equals(order.getPayType()) || order.getStatus() != null && order.getStatus() == 5) {
            throw new ServiceException("货到付款订单无需在线支付，请等待配送核销");
        }
        if (order.getStatus() != null && order.getStatus() != 0) throw new ServiceException("订单状态不可支付");
        if (StrUtil.isNotBlank(payType)) {
            String t = normalizePayType(payType);
            if ("cod".equals(t)) throw new ServiceException("已生成订单不可改为货到付款");
            order.setPayType(t);
            this.updateById(order);
        }
        String type = order.getPayType();
        PayInfoVO vo = new PayInfoVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setPayType(type);
        vo.setPayTypeText(payTypeText(type));
        vo.setAmount(order.getTotalAmount());
        vo.setDemoMode(true);
        vo.setPayTip("当前为演示环境：未配置真实商户密钥，确认后将模拟" + payTypeText(type) + "支付成功");
        vo.setMockPayUrl("/order/pay/" + order.getOrderNo());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO mockPaySuccess(String orderNo) {
        Integer userId = requireUserId();
        MallOrder order = getByOrderNo(orderNo);
        if (!userId.equals(order.getUserId())) throw new ServiceException("订单不存在");
        if (order.getStatus() != null && order.getStatus() == 1) return toVO(order, true);
        if (order.getStatus() == null || order.getStatus() != 0) throw new ServiceException("订单状态不可支付");
        if ("cod".equals(order.getPayType())) throw new ServiceException("货到付款订单无需在线支付");

        List<MallOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<MallOrderItem>()
                .eq(MallOrderItem::getOrderId, order.getId()));
        List<Line> lines = items.stream().map(i -> {
            Line l = new Line();
            l.goodsId = i.getGoodsId();
            l.quantity = i.getQuantity() == null ? 0 : i.getQuantity();
            l.name = i.getGoodsName();
            return l;
        }).collect(Collectors.toList());
        deductStock(lines);

        order.setStatus(1);
        order.setPayTime(System.currentTimeMillis());
        order.setPayTradeNo(("alipay".equals(order.getPayType()) ? "ALI" : "WX")
                + System.currentTimeMillis() + IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase());
        this.updateById(order);
        return toVO(order, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelMine(String orderNo) {
        Integer userId = requireUserId();
        MallOrder order = getByOrderNo(orderNo);
        if (!userId.equals(order.getUserId())) throw new ServiceException("订单不存在");
        if (order.getStatus() == null || (order.getStatus() != 0 && order.getStatus() != 5)) {
            throw new ServiceException("当前订单不可取消");
        }
        // 货到付款已扣库存，取消时回补
        if (order.getStatus() == 5) {
            List<MallOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<MallOrderItem>()
                    .eq(MallOrderItem::getOrderId, order.getId()));
            for (MallOrderItem i : items) {
                MallGoods goods = goodsMapper.selectById(i.getGoodsId());
                if (goods == null) continue;
                int qty = i.getQuantity() == null ? 0 : i.getQuantity();
                goods.setStock((goods.getStock() == null ? 0 : goods.getStock()) + qty);
                int sales = (goods.getSales() == null ? 0 : goods.getSales()) - qty;
                goods.setSales(Math.max(sales, 0));
                goodsMapper.updateById(goods);
            }
        }
        order.setStatus(2);
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO verifyCod(String orderNo) {
        MallOrder order = getByOrderNo(orderNo);
        if (!"cod".equals(order.getPayType())) throw new ServiceException("非货到付款订单");
        if (order.getStatus() == null || (order.getStatus() != 5 && order.getStatus() != 3)) {
            throw new ServiceException("仅待核销/已发货的货到付款订单可核销");
        }
        order.setStatus(4);
        order.setPayTime(System.currentTimeMillis());
        if (StrUtil.isBlank(order.getPayTradeNo())) {
            order.setPayTradeNo("COD" + System.currentTimeMillis());
        }
        this.updateById(order);
        return toVO(order, true);
    }

    @Override
    public OrderVO ship(String orderNo) {
        MallOrder order = getByOrderNo(orderNo);
        Integer st = order.getStatus();
        if (st == null || (st != 1 && st != 5)) {
            throw new ServiceException("仅已支付或货到付款待核销订单可发货");
        }
        order.setStatus(3);
        this.updateById(order);
        return toVO(order, true);
    }

    @Override
    public boolean hasPurchased(Integer userId, Integer goodsId) {
        if (userId == null || goodsId == null) return false;
        List<MallOrder> orders = this.list(new LambdaQueryWrapper<MallOrder>()
                .eq(MallOrder::getUserId, userId)
                .in(MallOrder::getStatus, Arrays.asList(1, 3, 4, 5)));
        if (orders.isEmpty()) return false;
        List<Long> ids = orders.stream().map(MallOrder::getId).collect(Collectors.toList());
        Long cnt = orderItemMapper.selectCount(new LambdaQueryWrapper<MallOrderItem>()
                .in(MallOrderItem::getOrderId, ids)
                .eq(MallOrderItem::getGoodsId, goodsId));
        return cnt != null && cnt > 0;
    }

    private MallOrder getByOrderNo(String orderNo) {
        MallOrder order = this.getOne(new LambdaQueryWrapper<MallOrder>()
                .eq(MallOrder::getOrderNo, orderNo).last("LIMIT 1"));
        if (order == null) throw new ServiceException("订单不存在");
        return order;
    }

    private Page<OrderVO> mapPage(Page<MallOrder> page) {
        Page<OrderVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<MallOrder> records = page.getRecords();
        if (records.isEmpty()) {
            voPage.setRecords(Collections.emptyList());
            return voPage;
        }
        List<Long> ids = records.stream().map(MallOrder::getId).collect(Collectors.toList());
        List<MallOrderItem> allItems = orderItemMapper.selectList(new LambdaQueryWrapper<MallOrderItem>()
                .in(MallOrderItem::getOrderId, ids));
        Map<Long, List<MallOrderItem>> group = allItems.stream().collect(Collectors.groupingBy(MallOrderItem::getOrderId));
        voPage.setRecords(records.stream().map(o -> toVO(o, group.getOrDefault(o.getId(), Collections.emptyList()))).collect(Collectors.toList()));
        return voPage;
    }

    private OrderVO toVO(MallOrder order, boolean withItems) {
        List<MallOrderItem> items = withItems
                ? orderItemMapper.selectList(new LambdaQueryWrapper<MallOrderItem>().eq(MallOrderItem::getOrderId, order.getId()))
                : Collections.emptyList();
        return toVO(order, items);
    }

    private OrderVO toVO(MallOrder order, List<MallOrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setStatus(order.getStatus());
        vo.setStatusText(statusText(order.getStatus()));
        vo.setPayType(order.getPayType());
        vo.setPayTypeText(payTypeText(order.getPayType()));
        vo.setDeliveryType(order.getDeliveryType());
        vo.setDeliveryName(order.getDeliveryName());
        vo.setPayTime(order.getPayTime());
        vo.setPayTradeNo(order.getPayTradeNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setGoodsAmount(order.getGoodsAmount());
        vo.setFreightAmount(order.getFreightAmount());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setReceiverAddress(order.getReceiverAddress());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(items.stream().map(i -> {
            OrderItemVO iv = new OrderItemVO();
            iv.setId(i.getId());
            iv.setGoodsId(i.getGoodsId());
            iv.setGoodsName(i.getGoodsName());
            iv.setGoodsCover(i.getGoodsCover());
            iv.setPrice(i.getPrice());
            iv.setQuantity(i.getQuantity());
            iv.setAmount(i.getAmount());
            return iv;
        }).collect(Collectors.toList()));
        return vo;
    }

    private static String statusText(Integer status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已取消";
            case 3 -> "已发货";
            case 4 -> "已完成";
            case 5 -> "货到付款·待核销";
            default -> "未知";
        };
    }

    private static String payTypeText(String payType) {
        if ("alipay".equals(payType)) return "支付宝";
        if ("wxpay".equals(payType)) return "微信支付";
        if ("cod".equals(payType)) return "货到付款";
        return "未选择";
    }

    private static class Line {
        Long cartId;
        Integer goodsId;
        String name;
        String cover;
        BigDecimal price;
        int quantity;
    }
}
