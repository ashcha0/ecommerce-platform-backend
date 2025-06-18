package com.ecommerce.mapper;

import com.ecommerce.model.dto.OrderQueryDTO;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.view.OrderDetailsView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(Order order);

    int updateStatus(@Param("id") Long id, @Param("status") String status);

    Order selectById(Long id);

    Order selectByOrderNo(String orderNo);

    List<Order> selectByCustomer(Long customerId);

    /**
     * 查询订单列表
     * 
     * @param queryDTO 查询条件
     * @return 订单详情视图列表
     */
    List<OrderDetailsView> searchOrders(OrderQueryDTO queryDTO);

    /**
     * 查询订单总数
     * 
     * @param queryDTO 查询条件
     * @return 订单总数
     */
    Long countOrders(OrderQueryDTO queryDTO);

    /**
     * 查询订单详情
     * 
     * @param orderId 订单ID
     * @return 订单详情视图
     */
    OrderDetailsView selectOrderDetails(Long orderId);

    /**
     * 查询所有订单
     * 
     * @return 所有订单列表
     */
    List<Order> selectAll();
}