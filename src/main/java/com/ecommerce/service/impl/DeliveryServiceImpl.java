package com.ecommerce.service.impl;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.mapper.DeliveryMapper;
import com.ecommerce.model.dto.DeliveryCreateDTO;
import com.ecommerce.model.dto.DeliveryQueryDTO;
import com.ecommerce.model.dto.DeliveryUpdateDTO;
import com.ecommerce.model.entity.Delivery;
import com.ecommerce.model.vo.DeliveryVO;
import com.ecommerce.service.DeliveryService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 配送服务实现类
 */
@Slf4j
@Service
public class DeliveryServiceImpl implements DeliveryService {

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Override
    public PageResult<DeliveryVO> getDeliveryList(DeliveryQueryDTO queryDTO) {
        log.info("分页查询配送列表，查询条件: {}", queryDTO);
        
        // 设置分页参数
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) {
            queryDTO.setPageNum(1);
        }
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) {
            queryDTO.setPageSize(10);
        }
        
        // 计算偏移量
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        
        // 查询总数
        long total = deliveryMapper.countDeliveryList(queryDTO);
        
        // 查询数据
        List<Delivery> deliveryList = deliveryMapper.selectDeliveryList(queryDTO);
        
        // 转换为VO
        List<DeliveryVO> deliveryVOList = deliveryList.stream()
                .map(DeliveryVO::from)
                .collect(Collectors.toList());
        
        // 构建PageInfo对象
        PageInfo<DeliveryVO> pageInfo = new PageInfo<>(deliveryVOList);
        pageInfo.setTotal(total);
        pageInfo.setPageNum(queryDTO.getPageNum());
        pageInfo.setPageSize(queryDTO.getPageSize());
        pageInfo.setPages((int) Math.ceil((double) total / queryDTO.getPageSize()));
        
        log.info("分页查询配送列表完成，总数: {}, 当前页: {}, 页大小: {}", total, queryDTO.getPageNum(), queryDTO.getPageSize());
        
        // 返回PageResult
        return PageResult.success(pageInfo);
    }

    @Override
    public Delivery getDeliveryByOrderId(Long orderId) {
        log.info("查询订单配送信息，订单ID: {}", orderId);
        return deliveryMapper.selectByOrderId(orderId);
    }

    @Override
    public Delivery getDeliveryById(Long id) {
        log.info("查询配送信息，配送ID: {}", id);
        return deliveryMapper.selectById(id);
    }

    @Override
    @Transactional
    public Delivery createDelivery(Long orderId) {
        log.info("创建配送信息，订单ID: {}", orderId);
        
        // 检查是否已存在配送信息
        Delivery existingDelivery = deliveryMapper.selectByOrderId(orderId);
        if (existingDelivery != null) {
            log.warn("订单已存在配送信息，订单ID: {}", orderId);
            return existingDelivery;
        }
        
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(Delivery.DeliveryStatus.PAYING);
        delivery.setCreateTime(LocalDateTime.now());
        
        deliveryMapper.insert(delivery);
        log.info("配送信息创建成功，配送ID: {}", delivery.getId());
        
        return delivery;
    }

    @Override
    @Transactional
    public Delivery createDeliveryWithDetails(DeliveryCreateDTO createDTO) {
        log.info("创建配送信息（包含详细信息），请求数据: {}", createDTO);
        
        // 检查是否已存在配送信息
        Delivery existingDelivery = deliveryMapper.selectByOrderId(createDTO.getOrderId());
        if (existingDelivery != null) {
            log.warn("订单已存在配送信息，订单ID: {}", createDTO.getOrderId());
            throw new RuntimeException("该订单已存在配送信息");
        }
        
        Delivery delivery = new Delivery();
        delivery.setOrderId(createDTO.getOrderId());
        delivery.setTrackingNo(createDTO.getTrackingNo());
        delivery.setShipper(createDTO.getShipper());
        delivery.setConsigneeName(createDTO.getConsigneeName());
        delivery.setConsigneePhone(createDTO.getConsigneePhone());
        delivery.setDeliveryAddress(createDTO.getDeliveryAddress());
        delivery.setEstimateTime(createDTO.getEstimateTime());
        delivery.setRemark(createDTO.getRemark());
        delivery.setStatus(Delivery.DeliveryStatus.PAYING);
        delivery.setCreateTime(LocalDateTime.now());
        
        deliveryMapper.insert(delivery);
        log.info("配送信息创建成功，配送ID: {}, 订单ID: {}", delivery.getId(), createDTO.getOrderId());
        
        return delivery;
    }

    @Override
    @Transactional
    public boolean updateDelivery(Long orderId, DeliveryUpdateDTO updateDTO) {
        log.info("更新配送信息，订单ID: {}", orderId);
        log.info("接收到的更新数据 - trackingNo: {}, shipper: {}, estimateTime: {}, status: {}, shipTime: {}", 
                updateDTO.getTrackingNo(), updateDTO.getShipper(), updateDTO.getEstimateTime(), 
                updateDTO.getStatus(), updateDTO.getShipTime());
        
        Delivery delivery = deliveryMapper.selectByOrderId(orderId);
        if (delivery == null) {
            log.warn("配送信息不存在，订单ID: {}", orderId);
            return false;
        }
        
        // 更新配送信息
        delivery.setTrackingNo(updateDTO.getTrackingNo());
        delivery.setShipper(updateDTO.getShipper());
        if (updateDTO.getStatus() != null) {
            delivery.setStatus(Delivery.DeliveryStatus.valueOf(updateDTO.getStatus().name()));
        }
        if (updateDTO.getShipTime() != null) {
            delivery.setShipTime(updateDTO.getShipTime());
        }
        if (updateDTO.getEstimateTime() != null) {
            log.info("更新预计送达时间: {} -> {}", delivery.getEstimateTime(), updateDTO.getEstimateTime());
            delivery.setEstimateTime(updateDTO.getEstimateTime());
        } else {
            log.info("预计送达时间为空，不更新");
        }
        
        int result = deliveryMapper.updateById(delivery);
        log.info("配送信息更新结果: {}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    @Transactional
    public boolean updateDeliveryStatus(Long orderId, String status) {
        log.info("更新配送状态，订单ID: {}, 状态: {}", orderId, status);
        
        int result = deliveryMapper.updateStatusByOrderId(orderId, status);
        log.info("配送状态更新结果: {}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    @Transactional
    public boolean shipOrder(Long orderId, String trackingNo, String shipper, String estimateTime) {
        log.info("=== 发货服务开始处理 ===");
        log.info("发货处理参数 - 订单ID: {}, 物流单号: {}, 物流公司: {}, 预计送达时间: {}", orderId, trackingNo, shipper, estimateTime);
        
        try {
            log.info("开始查询配送信息，订单ID: {}", orderId);
            Delivery delivery = deliveryMapper.selectByOrderId(orderId);
            
            if (delivery == null) {
                log.warn("配送信息不存在，订单ID: {}", orderId);
                return false;
            }
            
            log.info("查询到配送信息 - ID: {}, 当前状态: {}", delivery.getId(), delivery.getStatus());
            
            // 管理系统强制发货，跳过状态验证
            log.info("管理系统发货操作，跳过状态验证，当前状态: {}", delivery.getStatus());
            
            log.info("开始更新发货信息");
            // 更新发货信息
            delivery.setTrackingNo(trackingNo);
            delivery.setShipper(shipper);
            delivery.setStatus(Delivery.DeliveryStatus.RECEIPTING);
            delivery.setShipTime(LocalDateTime.now());
            
            // 设置预计送达时间
            if (estimateTime != null && !estimateTime.trim().isEmpty()) {
                try {
                    log.info("开始解析预计送达时间: {}", estimateTime);
                    LocalDateTime estimateDateTime;
                    
                    // 尝试多种日期格式解析
                    if (estimateTime.contains("T")) {
                        // ISO格式: 2024-01-01T10:30:00
                        estimateDateTime = LocalDateTime.parse(estimateTime);
                    } else if (estimateTime.length() == 19) {
                        // 标准格式: 2024-01-01 10:30:00
                        estimateDateTime = LocalDateTime.parse(estimateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    } else if (estimateTime.length() == 10) {
                        // 日期格式: 2024-01-01
                        estimateDateTime = LocalDate.parse(estimateTime).atStartOfDay();
                    } else {
                        // 其他格式尝试
                        estimateDateTime = LocalDateTime.parse(estimateTime.replace(" ", "T"));
                    }
                    
                    delivery.setEstimateTime(estimateDateTime);
                    log.info("成功设置预计送达时间: {}", estimateDateTime);
                } catch (Exception e) {
                    log.error("预计送达时间格式解析失败，原始值: {}, 错误: {}", estimateTime, e.getMessage());
                    // 不设置预计送达时间，但不影响发货流程
                }
            } else {
                log.info("预计送达时间为空，跳过设置");
            }
            
            log.info("准备更新数据库，配送ID: {}", delivery.getId());
            int result = deliveryMapper.updateById(delivery);
            log.info("数据库更新结果: {}, 影响行数: {}", result > 0 ? "成功" : "失败", result);
            
            if (result > 0) {
                log.info("发货处理成功完成");
            } else {
                log.error("发货处理失败，数据库更新失败");
            }
            
            return result > 0;
        } catch (Exception e) {
            log.error("发货处理异常: {}", e.getMessage(), e);
            throw e;
        } finally {
            log.info("=== 发货服务处理结束 ===");
        }
    }

    @Override
    @Transactional
    public boolean confirmDelivery(Long orderId) {
        log.info("确认收货，订单ID: {}", orderId);
        
        Delivery delivery = deliveryMapper.selectByOrderId(orderId);
        if (delivery == null) {
            log.warn("配送信息不存在，订单ID: {}", orderId);
            return false;
        }
        
        // 更新为已完成状态
        delivery.setStatus(Delivery.DeliveryStatus.COMPLETED);
        delivery.setDeliveryTime(LocalDateTime.now());
        
        int result = deliveryMapper.updateById(delivery);
        log.info("确认收货结果: {}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    public boolean confirmPayment(Long orderId) {
        log.info("确认付款，订单ID: {}", orderId);
        
        Delivery delivery = deliveryMapper.selectByOrderId(orderId);
        if (delivery == null) {
            log.warn("配送信息不存在，订单ID: {}", orderId);
            return false;
        }
        
        // 更新为待发货状态
        delivery.setStatus(Delivery.DeliveryStatus.SHIPPING);
        
        int result = deliveryMapper.updateById(delivery);
        log.info("确认付款结果: {}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    public boolean cancelOrder(Long orderId) {
        log.info("取消订单，订单ID: {}", orderId);
        
        Delivery delivery = deliveryMapper.selectByOrderId(orderId);
        if (delivery == null) {
            log.warn("配送信息不存在，订单ID: {}", orderId);
            return false;
        }
        
        // 更新为已取消状态
        delivery.setStatus(Delivery.DeliveryStatus.CANCELLED);
        
        int result = deliveryMapper.updateById(delivery);
        log.info("取消订单结果: {}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    public boolean applyAfterSale(Long orderId) {
        log.info("申请售后，订单ID: {}", orderId);
        
        Delivery delivery = deliveryMapper.selectByOrderId(orderId);
        if (delivery == null) {
            log.warn("配送信息不存在，订单ID: {}", orderId);
            return false;
        }
        
        // 更新为售后处理中状态
        delivery.setStatus(Delivery.DeliveryStatus.PROCESSING);
        
        int result = deliveryMapper.updateById(delivery);
        log.info("申请售后结果: {}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    public boolean completeAfterSale(Long orderId) {
        log.info("完成售后，订单ID: {}", orderId);
        
        Delivery delivery = deliveryMapper.selectByOrderId(orderId);
        if (delivery == null) {
            log.warn("配送信息不存在，订单ID: {}", orderId);
            return false;
        }
        
        // 更新为售后处理完成状态
        delivery.setStatus(Delivery.DeliveryStatus.PROCESSED);
        
        int result = deliveryMapper.updateById(delivery);
        log.info("完成售后结果: {}", result > 0 ? "成功" : "失败");
        
        return result > 0;
    }

    @Override
    public Map<String, Object> getDeliveryStats() {
        log.info("获取配送统计信息");
        
        try {
            // 获取配送状态统计
            List<DeliveryMapper.DeliveryStatusCount> statusStats = deliveryMapper.getDeliveryStatusStats();
            
            Map<String, Object> stats = new HashMap<>();
            
            // 初始化统计数据
            long total = 0;
            long pending = 0;  // 待发货 (PAYING + SHIPPING)
            long shipped = 0;  // 已发货 (RECEIPTING)
            long delivered = 0; // 已送达 (COMPLETED)
            long cancelled = 0; // 已取消
            long processing = 0; // 售后处理中
            long processed = 0; // 售后处理完成
            
            // 统计各状态数量
            for (DeliveryMapper.DeliveryStatusCount stat : statusStats) {
                String status = stat.getStatus();
                Long count = stat.getCount();
                total += count;
                
                switch (status) {
                    case "PAYING":
                    case "SHIPPING":
                        pending += count;
                        break;
                    case "RECEIPTING":
                        shipped += count;
                        break;
                    case "COMPLETED":
                        delivered += count;
                        break;
                    case "CANCELLED":
                        cancelled += count;
                        break;
                    case "PROCESSING":
                        processing += count;
                        break;
                    case "PROCESSED":
                        processed += count;
                        break;
                }
            }
            
            // 构建返回数据
            stats.put("total", total);
            stats.put("pending", pending);
            stats.put("shipped", shipped);
            stats.put("delivered", delivered);
            stats.put("cancelled", cancelled);
            stats.put("processing", processing);
            stats.put("processed", processed);
            
            // 详细状态统计
            Map<String, Long> statusDetail = new HashMap<>();
            for (DeliveryMapper.DeliveryStatusCount stat : statusStats) {
                statusDetail.put(stat.getStatus(), stat.getCount());
            }
            stats.put("statusDetail", statusDetail);
            
            log.info("配送统计信息获取成功: {}", stats);
            return stats;
            
        } catch (Exception e) {
            log.error("获取配送统计信息失败", e);
            throw new RuntimeException("获取配送统计信息失败", e);
        }
    }
}