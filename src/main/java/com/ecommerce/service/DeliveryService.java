package com.ecommerce.service;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.model.dto.DeliveryCreateDTO;
import com.ecommerce.model.dto.DeliveryQueryDTO;
import com.ecommerce.model.dto.DeliveryUpdateDTO;
import com.ecommerce.model.entity.Delivery;
import com.ecommerce.model.vo.DeliveryVO;

/**
 * 配送服务接口
 */
public interface DeliveryService {

    /**
     * 分页查询配送列表
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<DeliveryVO> getDeliveryList(DeliveryQueryDTO queryDTO);

    /**
     * 根据订单ID查询配送信息
     * @param orderId 订单ID
     * @return 配送信息
     */
    Delivery getDeliveryByOrderId(Long orderId);

    /**
     * 根据ID查询配送信息
     * @param id 配送ID
     * @return 配送信息
     */
    Delivery getDeliveryById(Long id);

    /**
     * 创建配送信息
     * @param orderId 订单ID
     * @return 配送信息
     */
    Delivery createDelivery(Long orderId);

    /**
     * 创建配送信息（包含详细信息）
     * @param createDTO 配送创建信息
     * @return 配送信息
     */
    Delivery createDeliveryWithDetails(DeliveryCreateDTO createDTO);

    /**
     * 更新配送信息
     * @param orderId 订单ID
     * @param updateDTO 更新信息
     * @return 是否成功
     */
    boolean updateDelivery(Long orderId, DeliveryUpdateDTO updateDTO);

    /**
     * 更新配送状态
     * @param orderId 订单ID
     * @param status 配送状态
     * @return 是否成功
     */
    boolean updateDeliveryStatus(Long orderId, String status);

    /**
     * 发货
     * @param orderId 订单ID
     * @param trackingNo 物流单号
     * @param shipper 物流公司
     * @return 是否成功
     */
    boolean shipOrder(Long orderId, String trackingNo, String shipper);

    /**
     * 确认收货
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean confirmDelivery(Long orderId);

    /**
     * 确认付款
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean confirmPayment(Long orderId);

    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId);

    /**
     * 申请售后
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean applyAfterSale(Long orderId);

    /**
     * 完成售后
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean completeAfterSale(Long orderId);
}