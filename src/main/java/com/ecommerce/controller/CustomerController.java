package com.ecommerce.controller;

import com.ecommerce.common.result.PageResult;
import com.ecommerce.common.result.Result;
import com.ecommerce.model.dto.CustomerRegisterDTO;
import com.ecommerce.model.dto.CustomerLoginDTO;
import com.ecommerce.model.dto.CustomerUpdateDTO;
import com.ecommerce.model.dto.CustomerQueryDTO;
import com.ecommerce.model.vo.CustomerVO;
import com.ecommerce.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/customers")
@Slf4j
@Validated
@Tag(name = "客户管理", description = "客户相关的API接口，包括客户注册、登录、信息管理等功能")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @Operation(summary = "客户注册", description = "新客户注册账号")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功", 
            content = @Content(schema = @Schema(implementation = Result.class), 
            examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"注册成功\", \"data\": {}}"))),
        @ApiResponse(responseCode = "400", description = "参数验证失败或用户名/手机号已存在", 
            content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PostMapping("/register")
    public Result<CustomerVO> register(@Valid @RequestBody CustomerRegisterDTO registerDTO) {
        try {
            return customerService.register(registerDTO);
        } catch (Exception e) {
            log.error("客户注册时发生异常", e);
            return Result.fail(500, "注册失败，请稍后重试");
        }
    }
    
    @Operation(summary = "客户登录", description = "客户账号登录")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功", 
            content = @Content(schema = @Schema(implementation = Result.class), 
            examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"登录成功\", \"data\": {}}"))),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误", 
            content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PostMapping("/login")
    public Result<CustomerVO> login(@Valid @RequestBody CustomerLoginDTO loginDTO) {
        try {
            return customerService.login(loginDTO);
        } catch (Exception e) {
            log.error("客户登录时发生异常", e);
            return Result.fail(500, "登录失败，请稍后重试");
        }
    }
    
    @Operation(summary = "获取客户信息", description = "根据客户ID获取客户详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功", 
            content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "404", description = "客户不存在", 
            content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @GetMapping("/{id}")
    public Result<CustomerVO> getCustomer(
        @Parameter(description = "客户ID") 
        @PathVariable("id") @NotNull @Positive Long id) {
        try {
            CustomerVO customer = customerService.getCustomerById(id);
            return Result.success(customer);
        } catch (Exception e) {
            log.error("获取客户信息时发生异常，客户ID: {}", id, e);
            return Result.fail(500, "获取客户信息失败，请稍后重试");
        }
    }
    
    @Operation(summary = "更新客户信息", description = "更新客户的个人信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功", 
            content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "400", description = "参数验证失败", 
            content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "404", description = "客户不存在", 
            content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @PutMapping("/{id}")
    public Result<CustomerVO> updateCustomer(@PathVariable("id") @NotNull @Positive Long id,
                                           @Parameter(description = "客户ID", required = true, example = "1")
                                           @Valid @RequestBody CustomerUpdateDTO updateDTO) {
        try {
            // 验证ID有效性
            if (id <= 0) {
                return Result.fail(400, "客户ID必须大于0");
            }
            updateDTO.setId(id);
            return customerService.updateCustomer(updateDTO);
        } catch (Exception e) {
            log.error("更新客户信息时发生异常，客户ID: {}", id, e);
            return Result.fail(500, "更新客户信息失败，请稍后重试");
        }
    }
    
    @Operation(summary = "删除客户", description = "根据客户ID删除客户账号")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功", 
            content = @Content(schema = @Schema(implementation = Result.class))),
        @ApiResponse(responseCode = "404", description = "客户不存在", 
            content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @DeleteMapping("/{id}")
    public Result<String> deleteCustomer(
        @Parameter(description = "客户ID") 
        @PathVariable("id") @NotNull @Positive Long id) {
        try {
            return customerService.deleteCustomer(id);
        } catch (Exception e) {
            log.error("删除客户时发生异常，客户ID: {}", id, e);
            return Result.fail(500, "删除客户失败，请稍后重试");
        }
    }
    
    @Operation(summary = "搜索客户", description = "根据条件搜索客户，支持按用户名、手机号等条件进行筛选，支持分页查询")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "搜索成功", 
            content = @Content(schema = @Schema(implementation = PageResult.class), 
            examples = @ExampleObject(value = "{\"code\": 200, \"message\": \"操作成功\", \"data\": {\"records\": [], \"total\": 0, \"pageNum\": 1, \"pageSize\": 10}}"))),
        @ApiResponse(responseCode = "400", description = "参数验证失败", 
            content = @Content(schema = @Schema(implementation = PageResult.class)))
    })
    @GetMapping("/search")
    public PageResult<CustomerVO> searchCustomers(@Valid CustomerQueryDTO queryDTO) {
        try {
            return customerService.searchCustomers(queryDTO);
        } catch (Exception e) {
            log.error("搜索客户时发生异常", e);
            return PageResult.error(500, "搜索客户失败，请稍后重试");
        }
    }
    
    @Operation(summary = "获取客户列表", description = "获取所有客户列表，支持分页")
    @GetMapping
    public PageResult<CustomerVO> getCustomers(
        @Parameter(description = "页码，默认为1") 
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @Parameter(description = "每页大小，默认为10") 
        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        try {
            // 预处理分页参数，确保参数有效
            if (page <= 0) {
                page = 1;
            }
            if (size <= 0 || size > 100) {
                size = 10;
            }
            
            // 创建查询DTO，只设置分页参数
            CustomerQueryDTO queryDTO = new CustomerQueryDTO();
            queryDTO.setPage(page);
            queryDTO.setSize(size);
            
            return customerService.searchCustomers(queryDTO);
        } catch (Exception e) {
            log.error("获取客户列表时发生异常", e);
            return PageResult.error(500, "获取客户列表失败，请稍后重试");
        }
    }
    
    @Operation(summary = "检查用户名是否存在", description = "检查指定用户名是否已被注册")
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(
        @Parameter(description = "用户名") 
        @RequestParam String username) {
        try {
            boolean exists = customerService.isUsernameExists(username);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查用户名时发生异常，用户名: {}", username, e);
            return Result.fail(500, "检查用户名失败，请稍后重试");
        }
    }
    
    @Operation(summary = "检查手机号是否存在", description = "检查指定手机号是否已被注册")
    @GetMapping("/check-phone")
    public Result<Boolean> checkPhone(
        @Parameter(description = "手机号") 
        @RequestParam String phone) {
        try {
            boolean exists = customerService.isPhoneExists(phone);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查手机号时发生异常，手机号: {}", phone, e);
            return Result.fail(500, "检查手机号失败，请稍后重试");
        }
    }
    
    @Operation(summary = "检查邮箱是否存在", description = "检查指定邮箱是否已被注册")
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(
        @Parameter(description = "邮箱") 
        @RequestParam String email) {
        try {
            boolean exists = customerService.isEmailExists(email);
            return Result.success(exists);
        } catch (Exception e) {
            log.error("检查邮箱时发生异常，邮箱: {}", email, e);
            return Result.fail(500, "检查邮箱失败，请稍后重试");
        }
    }
}