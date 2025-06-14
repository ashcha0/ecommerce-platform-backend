package com.ecommerce.service.impl;

import com.ecommerce.model.dto.CustomerRegisterDTO;
import com.ecommerce.model.dto.CustomerLoginDTO;
import com.ecommerce.model.dto.CustomerUpdateDTO;
import com.ecommerce.model.dto.CustomerQueryDTO;
import com.ecommerce.model.vo.CustomerVO;
import com.ecommerce.model.entity.Customer;
import com.ecommerce.mapper.CustomerMapper;
import com.ecommerce.service.CustomerService;
import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;
import com.ecommerce.common.exception.BusinessException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Override
    @Transactional
    public Result<CustomerVO> register(CustomerRegisterDTO registerDTO) {
        log.info("客户注册，用户名: {}", registerDTO.getUsername());
        
        // 参数校验
        if (registerDTO == null) {
            throw new BusinessException(400, "注册信息不能为空");
        }
        
        // 验证确认密码
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException(400, "两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (isUsernameExists(registerDTO.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (isPhoneExists(registerDTO.getPhone())) {
            throw new BusinessException(400, "手机号已被注册");
        }
        
        // 检查邮箱是否已存在（如果提供了邮箱）
        if (registerDTO.getEmail() != null && !registerDTO.getEmail().trim().isEmpty() 
            && isEmailExists(registerDTO.getEmail())) {
            throw new BusinessException(400, "邮箱已被注册");
        }
        
        // 创建客户实体
        Customer customer = new Customer();
        BeanUtils.copyProperties(registerDTO, customer);
        
        // 密码加密
        customer.setPassword(encryptPassword(registerDTO.getPassword()));
        customer.setCreateTime(LocalDateTime.now());
        customer.setUpdateTime(LocalDateTime.now());
        
        // 插入数据库
        int result = customerMapper.insert(customer);
        if (result <= 0) {
            throw new BusinessException(500, "注册失败");
        }
        
        log.info("客户注册成功，ID: {}, 用户名: {}", customer.getId(), customer.getUsername());
        
        return Result.success(new CustomerVO(customer), "注册成功");
    }
    
    @Override
    public Result<CustomerVO> login(CustomerLoginDTO loginDTO) {
        log.info("客户登录，用户名: {}", loginDTO.getUsername());
        
        // 参数校验
        if (loginDTO == null) {
            throw new BusinessException(400, "登录信息不能为空");
        }
        
        // 根据用户名查询客户
        Customer customer = customerMapper.selectByUsername(loginDTO.getUsername());
        if (customer == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        // 验证密码
        String encryptedPassword = encryptPassword(loginDTO.getPassword());
        if (!encryptedPassword.equals(customer.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        
        log.info("客户登录成功，ID: {}, 用户名: {}", customer.getId(), customer.getUsername());
        
        return Result.success(new CustomerVO(customer), "登录成功");
    }
    
    @Override
    public CustomerVO getCustomerById(Long id) {
        if (id == null) {
            throw new BusinessException(400, "客户ID不能为空");
        }
        
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        
        return new CustomerVO(customer);
    }
    
    @Override
    @Transactional
    public Result<CustomerVO> updateCustomer(CustomerUpdateDTO updateDTO) {
        log.info("更新客户信息，ID: {}", updateDTO.getId());
        
        // 参数校验
        if (updateDTO == null || updateDTO.getId() == null) {
            throw new BusinessException(400, "更新信息不能为空");
        }
        
        // 检查客户是否存在
        Customer existingCustomer = customerMapper.selectById(updateDTO.getId());
        if (existingCustomer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        
        // 检查手机号是否被其他用户使用
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().equals(existingCustomer.getPhone())) {
            Customer phoneCustomer = customerMapper.selectByPhone(updateDTO.getPhone());
            if (phoneCustomer != null && !phoneCustomer.getId().equals(updateDTO.getId())) {
                throw new BusinessException(400, "手机号已被其他用户使用");
            }
        }
        
        // 检查邮箱是否被其他用户使用
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().trim().isEmpty() 
            && !updateDTO.getEmail().equals(existingCustomer.getEmail())) {
            Customer emailCustomer = customerMapper.selectByEmail(updateDTO.getEmail());
            if (emailCustomer != null && !emailCustomer.getId().equals(updateDTO.getId())) {
                throw new BusinessException(400, "邮箱已被其他用户使用");
            }
        }
        
        // 构建更新对象
        Customer customer = new Customer();
        customer.setId(updateDTO.getId());
        customer.setUpdateTime(LocalDateTime.now());
        
        // 只更新非空字段
        if (updateDTO.getRealName() != null) {
            customer.setRealName(updateDTO.getRealName());
        }
        if (updateDTO.getPhone() != null) {
            customer.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getEmail() != null) {
            customer.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getAddress() != null) {
            customer.setAddress(updateDTO.getAddress());
        }
        
        // 更新数据库
        int result = customerMapper.update(customer);
        if (result <= 0) {
            throw new BusinessException(500, "更新失败");
        }
        
        // 查询更新后的客户信息
        Customer updatedCustomer = customerMapper.selectById(updateDTO.getId());
        
        log.info("客户信息更新成功，ID: {}", updateDTO.getId());
        
        return Result.success(new CustomerVO(updatedCustomer), "更新成功");
    }
    
    @Override
    @Transactional
    public Result<String> deleteCustomer(Long id) {
        log.info("删除客户，ID: {}", id);
        
        if (id == null) {
            throw new BusinessException(400, "客户ID不能为空");
        }
        
        // 检查客户是否存在
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(404, "客户不存在");
        }
        
        // 删除客户
        int result = customerMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(500, "删除失败");
        }
        
        log.info("客户删除成功，ID: {}", id);
        
        return Result.success("删除成功");
    }
    
    @Override
    public PageResult<CustomerVO> searchCustomers(CustomerQueryDTO queryDTO) {
        // 配置分页参数
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        
        // 执行查询
        List<Customer> customers = customerMapper.selectByCondition(
            queryDTO.getUsername(), 
            queryDTO.getPhone(), 
            queryDTO.getOffset(), 
            queryDTO.getSize()
        );
        
        // 转换为VO对象
        List<CustomerVO> customerVOs = customers.stream()
            .map(CustomerVO::new)
            .collect(Collectors.toList());
        
        // 转换为分页结果对象
        PageInfo<Customer> pageInfo = new PageInfo<>(customers);
        PageInfo<CustomerVO> voPageInfo = new PageInfo<>(customerVOs);
        voPageInfo.setTotal(pageInfo.getTotal());
        voPageInfo.setPages(pageInfo.getPages());
        voPageInfo.setPageNum(pageInfo.getPageNum());
        voPageInfo.setPageSize(pageInfo.getPageSize());
        
        return PageResult.success(voPageInfo);
    }
    
    @Override
    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        Customer customer = customerMapper.selectByUsername(username);
        return customer != null;
    }
    
    @Override
    public boolean isPhoneExists(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        Customer customer = customerMapper.selectByPhone(phone);
        return customer != null;
    }
    
    @Override
    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Customer customer = customerMapper.selectByEmail(email);
        return customer != null;
    }
    
    /**
     * 密码加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    private String encryptPassword(String password) {
        // 使用MD5加密，实际项目中建议使用更安全的加密方式如BCrypt
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }
}