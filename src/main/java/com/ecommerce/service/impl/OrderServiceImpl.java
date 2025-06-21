package com.ecommerce.service.impl;

import com.ecommerce.common.constant.ErrorCode;
import com.ecommerce.common.exception.BusinessException;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.util.IdGenerator;
import com.github.pagehelper.PageInfo;
import com.ecommerce.mapper.OrderItemMapper;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.dto.DeliveryCreateDTO;
import com.ecommerce.model.dto.OrderCreateDTO;
import com.ecommerce.model.dto.OrderQueryDTO;
import com.ecommerce.model.entity.Delivery;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.entity.OrderItem;
import com.ecommerce.model.entity.Product;
import com.ecommerce.model.view.OrderDetailsView;
import com.ecommerce.model.vo.OrderDetailVO;
import com.ecommerce.model.vo.SimpleOrderVO;
import com.ecommerce.service.DeliveryService;
import com.ecommerce.service.InventoryService;
import com.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;
    private final DeliveryService deliveryService;
    private final IdGenerator idGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderCreateDTO dto) {
        log.info("开始创建订单，客户ID: {}", dto.getCustomerId());

        // 1. 验证订单项不为空
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "订单项不能为空");
        }

        // 2. 生成订单号
        String orderNo = generateOrderNo();

        // 3. 计算订单总金额并验证库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderCreateDTO.OrderItemDTO itemDTO : dto.getItems()) {
            // 查询商品信息
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product == null) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "商品不存在");
            }

            // 验证库存
            boolean stockAvailable = inventoryService.checkStock(itemDTO.getProductId(), itemDTO.getQuantity());
            if (!stockAvailable) {
                throw new BusinessException(ErrorCode.PRODUCT_INSUFFICIENT_STOCK, "商品库存不足");
            }

            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemDTO.getProductId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setItemAmount(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getItemAmount());
        }

        // 4. 创建订单
        Order order = Order.create(orderNo, dto.getCustomerId(), totalAmount);
        int result = orderMapper.insert(order);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单创建失败");
        }

        // 5. 创建订单项
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);

            // 6. 锁定库存
            try {
                inventoryService.lockStock(orderItem.getProductId(), orderItem.getQuantity());
            } catch (Exception e) {
                log.error("库存锁定失败，商品ID: {}, 数量: {}", orderItem.getProductId(), orderItem.getQuantity(), e);
                throw new BusinessException(ErrorCode.INVENTORY_LOCK_FAILED, "库存锁定失败");
            }
        }

        // 7. 创建配送记录
        try {
            DeliveryCreateDTO deliveryCreateDTO = new DeliveryCreateDTO();
            deliveryCreateDTO.setOrderId(order.getId());
            deliveryCreateDTO.setConsigneeName(dto.getConsigneeName());
            deliveryCreateDTO.setConsigneePhone(dto.getConsigneePhone());
            deliveryCreateDTO.setDeliveryAddress(dto.getDeliveryAddress());
            deliveryCreateDTO.setRemark(dto.getRemark());
            
            log.info("准备创建配送记录，订单ID: {}, 收货人: {}, 电话: {}, 地址: {}", 
                    order.getId(), dto.getConsigneeName(), dto.getConsigneePhone(), dto.getDeliveryAddress());
            
            deliveryService.createDeliveryWithDetails(deliveryCreateDTO);
            log.info("配送记录创建成功，订单ID: {}", order.getId());
        } catch (Exception e) {
            log.error("配送记录创建失败，订单ID: {}, 错误信息: {}", order.getId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "配送记录创建失败: " + e.getMessage());
        }

        log.info("订单创建成功，订单号: {}, 订单ID: {}", orderNo, order.getId());
        return order;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long orderId) {
        log.info("查询订单详情，订单ID: {}", orderId);

        // 查询订单基本信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }

        // 查询订单项
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);

        // 构建返回对象
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setId(order.getId());
        orderDetailVO.setOrderNo(order.getOrderNo());
        orderDetailVO.setCustomerId(order.getCustomerId());
        orderDetailVO.setOrderTime(order.getOrderTime());
        orderDetailVO.setTotalAmount(order.getTotalAmount());
        orderDetailVO.setStatus(order.getStatus().name());
        orderDetailVO.setCreateTime(order.getCreateTime());
        orderDetailVO.setUpdateTime(order.getUpdateTime());

        // 设置订单项信息
        List<OrderDetailVO.OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItem item : orderItems) {
            Product product = productMapper.selectById(item.getProductId());

            OrderDetailVO.OrderItemVO itemVO = new OrderDetailVO.OrderItemVO();
            itemVO.setId(item.getId());
            itemVO.setProductId(item.getProductId());
            itemVO.setProductName(product != null ? product.getName() : "未知商品");
            itemVO.setQuantity(item.getQuantity());
            itemVO.setUnitPrice(item.getUnitPrice());
            itemVO.setItemAmount(item.getItemAmount());

            itemVOs.add(itemVO);
        }
        orderDetailVO.setItems(itemVOs);

        return orderDetailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        log.info("取消订单，订单ID: {}", orderId);

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_CANCELLED, "订单已取消");
        }

        // 更新订单状态
        int result = orderMapper.updateStatus(orderId, Order.OrderStatus.CANCELLED.name());
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单取消失败");
        }

        // 释放库存
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            try {
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                log.error("库存释放失败，商品ID: {}, 数量: {}", item.getProductId(), item.getQuantity(), e);
            }
        }

        log.info("订单取消成功，订单ID: {}", orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(Long orderId, String status) {
        log.info("更新订单状态，订单ID: {}, 状态: {}", orderId, status);

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }

        // 验证状态转换的合法性
        validateStatusTransition(order.getStatus(), Order.OrderStatus.valueOf(status));

        int result = orderMapper.updateStatus(orderId, status);
        if (result <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单状态更新失败");
        }

        log.info("订单状态更新成功，订单ID: {}, 新状态: {}", orderId, status);
    }

    @Override
    public PageResult<OrderDetailsView> searchOrders(OrderQueryDTO queryDTO) {
        log.info("分页查询订单列表，查询条件: {}", queryDTO);

        // 计算分页偏移量
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        queryDTO.setOffset(offset);

        List<OrderDetailsView> orders = orderMapper.searchOrders(queryDTO);

        // 查询总数
        Long total = orderMapper.countOrders(queryDTO);

        // 构建PageInfo对象
        PageInfo<OrderDetailsView> pageInfo = new PageInfo<>(orders);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(queryDTO.getPageNum());
        pageInfo.setPageSize(queryDTO.getPageSize());
        pageInfo.setPages((int) Math.ceil((double) total / queryDTO.getPageSize()));

        // 返回PageResult
        return PageResult.success(pageInfo);
    }

    @Override
    public OrderDetailVO.DeliveryVO getOrderDeliveryInfo(Long orderId) {
        log.info("查询订单配送信息，订单ID: {}", orderId);

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }

        // 查询配送信息
        Delivery delivery = deliveryService.getDeliveryByOrderId(orderId);
        
        OrderDetailVO.DeliveryVO deliveryVO = new OrderDetailVO.DeliveryVO();
        deliveryVO.setOrderId(orderId);
        
        if (delivery != null) {
            // 如果存在配送信息，填充详细数据
            deliveryVO.setTrackingNo(delivery.getTrackingNo());
            deliveryVO.setShipper(delivery.getShipper());
            deliveryVO.setStatus(delivery.getStatus() != null ? delivery.getStatus().name() : "UNKNOWN");
            deliveryVO.setShipTime(delivery.getShipTime());
            deliveryVO.setDeliveryTime(delivery.getDeliveryTime());
            
            // 设置配送地址和收货人信息
            if (delivery.getDeliveryAddress() != null) {
                deliveryVO.setDeliveryAddress(delivery.getDeliveryAddress());
            }
            if (delivery.getConsigneeName() != null) {
                deliveryVO.setConsigneeName(delivery.getConsigneeName());
            }
            if (delivery.getConsigneePhone() != null) {
                deliveryVO.setConsigneePhone(delivery.getConsigneePhone());
            }
            if (delivery.getEstimateTime() != null) {
                deliveryVO.setEstimatedDeliveryTime(delivery.getEstimateTime());
            }
            
            log.info("查询到配送信息，配送状态: {}", delivery.getStatus());
        } else {
            // 如果没有配送信息，使用订单中的基本信息
            deliveryVO.setStatus("SHIPPING");
            deliveryVO.setDeliveryAddress(order.getDeliveryAddress());
            // 注意：收货人信息应该在配送表中，如果没有配送记录则无法获取
            
            log.info("未找到配送信息，使用订单基本信息");
        }

        return deliveryVO;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + idGenerator.nextId();
    }

    /**
     * 验证订单状态转换的合法性
     */
    private void validateStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        // 简化的状态转换验证逻辑
        switch (currentStatus) {
            case CREATED:
                if (newStatus != Order.OrderStatus.PAID && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "订单状态转换不合法");
                }
                break;
            case PAID:
                if (newStatus != Order.OrderStatus.SHIPPING && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "订单状态转换不合法");
                }
                break;
            case SHIPPING:
                if (newStatus != Order.OrderStatus.COMPLETED && newStatus != Order.OrderStatus.CANCELLED) {
                    throw new BusinessException(ErrorCode.PARAM_ERROR, "订单状态转换不合法");
                }
                break;
            case COMPLETED:
            case CANCELLED:
                throw new BusinessException(ErrorCode.PARAM_ERROR, "订单已完成或已取消，无法修改状态");
        }
    }

    @Override
    public List<SimpleOrderVO> getSimpleOrders() {
        log.info("获取简单订单列表");
        
        // 查询所有订单
        List<Order> orders = orderMapper.selectAll();
        
        // 转换为SimpleOrderVO
        List<SimpleOrderVO> simpleOrders = new ArrayList<>();
        for (Order order : orders) {
            SimpleOrderVO vo = new SimpleOrderVO();
            vo.setId(order.getId());
            vo.setCustomerName(order.getCustomerName());
            vo.setCustomerPhone(order.getCustomerPhone());
            vo.setDeliveryAddress(order.getDeliveryAddress());
            vo.setTotalAmount(order.getTotalAmount());
            vo.setStatus(order.getStatus().getDesc());
            vo.setCreateTime(order.getCreateTime());
            simpleOrders.add(vo);
        }
        
        return simpleOrders;
    }
}