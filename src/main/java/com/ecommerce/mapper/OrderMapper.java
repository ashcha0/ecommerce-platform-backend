package com.ecommerce.mapper;

import com.ecommerce.model.dto.OrderQueryDTO;
import com.ecommerce.model.entity.Order;
import com.ecommerce.model.view.OrderDetailsView;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(Order order);

    int updateStatus(Long id, String status);

    Order selectById(Long id);

    Order selectByOrderNo(String orderNo);

    List<Order> selectByCustomer(Long customerId);

    List<OrderDetailsView> searchOrders(OrderQueryDTO queryDTO);

    OrderDetailsView selectOrderDetails(Long orderId);
}