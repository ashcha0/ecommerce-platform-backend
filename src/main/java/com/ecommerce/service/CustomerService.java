package com.ecommerce.service;

import com.ecommerce.model.dto.CustomerRegisterDTO;
import com.ecommerce.model.dto.CustomerLoginDTO;
import com.ecommerce.model.dto.CustomerUpdateDTO;
import com.ecommerce.model.dto.CustomerQueryDTO;
import com.ecommerce.model.vo.CustomerVO;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;

public interface CustomerService {
    
    /**
     * 客户注册
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    Result<CustomerVO> register(CustomerRegisterDTO registerDTO);
    
    /**
     * 客户登录
     * @param loginDTO 登录信息
     * @return 登录结果
     */
    Result<CustomerVO> login(CustomerLoginDTO loginDTO);
    
    /**
     * 根据ID获取客户信息
     * @param id 客户ID
     * @return 客户信息
     */
    CustomerVO getCustomerById(Long id);
    
    /**
     * 更新客户信息
     * @param updateDTO 更新信息
     * @return 更新结果
     */
    Result<CustomerVO> updateCustomer(CustomerUpdateDTO updateDTO);
    
    /**
     * 删除客户
     * @param id 客户ID
     * @return 删除结果
     */
    Result<String> deleteCustomer(Long id);
    
    /**
     * 分页查询客户
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<CustomerVO> searchCustomers(CustomerQueryDTO queryDTO);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 检查手机号是否存在
     * @param phone 手机号
     * @return 是否存在
     */
    boolean isPhoneExists(String phone);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean isEmailExists(String email);
}