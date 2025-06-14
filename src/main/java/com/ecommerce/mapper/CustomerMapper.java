package com.ecommerce.mapper;

import com.ecommerce.model.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CustomerMapper {
    
    /**
     * 插入新客户
     * @param customer 客户信息
     * @return 影响行数
     */
    int insert(Customer customer);
    
    /**
     * 更新客户信息
     * @param customer 客户信息
     * @return 影响行数
     */
    int update(Customer customer);
    
    /**
     * 根据ID删除客户
     * @param id 客户ID
     * @return 影响行数
     */
    int deleteById(Long id);
    
    /**
     * 根据ID查询客户
     * @param id 客户ID
     * @return 客户信息
     */
    Customer selectById(Long id);
    
    /**
     * 根据用户名查询客户
     * @param username 用户名
     * @return 客户信息
     */
    Customer selectByUsername(String username);
    
    /**
     * 根据手机号查询客户
     * @param phone 手机号
     * @return 客户信息
     */
    Customer selectByPhone(String phone);
    
    /**
     * 根据邮箱查询客户
     * @param email 邮箱
     * @return 客户信息
     */
    Customer selectByEmail(String email);
    
    /**
     * 查询所有客户
     * @return 客户列表
     */
    List<Customer> selectAll();
    
    /**
     * 根据条件分页查询客户
     * @param username 用户名（模糊查询）
     * @param phone 手机号（模糊查询）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 客户列表
     */
    List<Customer> selectByCondition(@Param("username") String username, 
                                   @Param("phone") String phone,
                                   @Param("offset") Integer offset, 
                                   @Param("limit") Integer limit);
    
    /**
     * 统计客户总数
     * @return 客户总数
     */
    Long countAll();
    
    /**
     * 根据条件统计客户数量
     * @param username 用户名（模糊查询）
     * @param phone 手机号（模糊查询）
     * @return 客户数量
     */
    Long countByCondition(@Param("username") String username, 
                         @Param("phone") String phone);
}