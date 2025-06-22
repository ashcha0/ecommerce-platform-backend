package com.ecommerce.mapper;

import com.ecommerce.model.dto.DeliveryQueryDTO;
import com.ecommerce.model.entity.Delivery;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeliveryMapper {

    /**
     * 分页查询配送列表
     */
    List<Delivery> selectDeliveryList(DeliveryQueryDTO queryDTO);

    /**
     * 查询配送总数
     */
    long countDeliveryList(DeliveryQueryDTO queryDTO);

    /**
     * 根据订单ID查询配送信息
     */
    @Select("SELECT * FROM delivery WHERE order_id = #{orderId}")
    Delivery selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据ID查询配送信息
     */
    @Select("SELECT * FROM delivery WHERE id = #{id}")
    Delivery selectById(@Param("id") Long id);

    /**
     * 插入配送信息
     */
    @Insert("INSERT INTO delivery (order_id, tracking_no, shipper, status, ship_time, estimate_time, delivery_time, create_time, consignee_name, consignee_phone, delivery_address) " +
            "VALUES (#{orderId}, #{trackingNo}, #{shipper}, #{status}, #{shipTime}, #{estimateTime}, #{deliveryTime}, #{createTime}, #{consigneeName}, #{consigneePhone}, #{deliveryAddress})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Delivery delivery);

    /**
     * 更新配送信息
     */
    @Update("UPDATE delivery SET tracking_no = #{trackingNo}, shipper = #{shipper}, status = #{status}, " +
            "ship_time = #{shipTime}, estimate_time = #{estimateTime}, delivery_time = #{deliveryTime}, " +
            "consignee_name = #{consigneeName}, consignee_phone = #{consigneePhone}, delivery_address = #{deliveryAddress} " +
            "WHERE id = #{id}")
    int updateById(Delivery delivery);

    /**
     * 更新配送状态
     */
    @Update("UPDATE delivery SET status = #{status} WHERE order_id = #{orderId}")
    int updateStatusByOrderId(@Param("orderId") Long orderId, @Param("status") String status);

    /**
     * 根据订单ID删除配送信息
     */
    @Delete("DELETE FROM delivery WHERE order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") Long orderId);

    /**
     * 统计各配送状态的订单数量
     */
    @Select("SELECT status, COUNT(*) as count FROM delivery GROUP BY status")
    @Results({
            @Result(column = "status", property = "status"),
            @Result(column = "count", property = "count")
    })
    List<DeliveryStatusCount> getDeliveryStatusStats();

    /**
     * 配送状态统计结果类
     */
    class DeliveryStatusCount {
        private String status;
        private Long count;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}