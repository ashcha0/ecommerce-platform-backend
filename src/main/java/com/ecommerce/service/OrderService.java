package com.ecommerce.service;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.model.dto.OrderCreateDTO;
import com.ecommerce.model.dto.OrderQueryDTO;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.view.OrderDetailsView;
import com.ecommerce.model.vo.OrderDetailVO;

public interface OrderService {
    /**
     * 创建新订单
     */
    Order createOrder(OrderCreateDTO dto);

    /**
     * 获取订单详情
     */
    OrderDetailVO getOrderDetail(Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);

    /**
     * 更新订单状态
     */
    void updateOrderStatus(Long orderId, String status);

    /**
     * 分页查询订单列表
     */
    PageResult<OrderDetailsView> searchOrders(OrderQueryDTO queryDTO);

    /**
     * 获取订单的配送信息
     */
    OrderDetailVO.DeliveryVO getOrderDeliveryInfo(Long orderId);
}