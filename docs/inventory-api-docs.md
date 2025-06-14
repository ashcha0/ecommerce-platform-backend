# 库存管理功能接口文档

## 概述

本文档详细描述了电商系统中库存管理模块的所有API接口，包括库存查询、更新、创建、删除以及统计等功能。

**基础信息：**
- 服务器地址：`http://localhost:8080/ecommerce-api`
- API版本：1.0
- 数据格式：JSON
- 字符编码：UTF-8

## 通用响应格式

所有接口都遵循统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

**响应状态码说明：**
- `200`：操作成功
- `400`：参数错误
- `404`：资源不存在
- `500`：服务器内部错误

## 接口列表

### 1. 创建商品库存记录

**接口描述：** 为新商品创建库存记录

**请求信息：**
- **URL：** `POST /inventory/create`
- **Content-Type：** `application/json`

**请求参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| productId | Long | 是 | 商品ID | 1 |
| initialStock | Integer | 是 | 初始库存数量 | 100 |
| lowStockThreshold | Integer | 否 | 低库存阈值 | 10 |

**请求示例：**
```bash
curl -X POST "http://localhost:8080/ecommerce-api/inventory/create" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "initialStock": 100,
    "lowStockThreshold": 10
  }'
```

**响应示例：**
```json
{
  "code": 200,
  "message": "库存记录创建成功",
  "data": null
}
```

**错误响应：**
- `400`：参数错误或商品已存在库存记录
- `404`：商品不存在

---

### 2. 获取商品库存信息

**接口描述：** 根据商品ID获取库存详细信息

**请求信息：**
- **URL：** `GET /inventory/product/{productId}`
- **方法：** GET

**路径参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| productId | Long | 是 | 商品ID | 1 |

**请求示例：**
```bash
curl -X GET "http://localhost:8080/ecommerce-api/inventory/product/1"
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "productId": 1,
    "stock": 100,
    "lowStockThreshold": 10,
    "updateTime": "2024-01-01T10:00:00"
  }
}
```

**错误响应：**
- `400`：参数错误
- `404`：库存信息不存在

---

### 3. 更新商品库存

**接口描述：** 更新指定商品的库存数量，支持增加或减少

**请求信息：**
- **URL：** `PUT /inventory/product/{productId}/stock`
- **方法：** PUT

**路径参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| productId | Long | 是 | 商品ID | 1 |

**查询参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| stockChange | Integer | 是 | 库存变化量，正数为增加，负数为减少 | 10 |

**请求示例：**
```bash
curl -X PUT "http://localhost:8080/ecommerce-api/inventory/product/1/stock?stockChange=10"
```

**响应示例：**
```json
{
  "code": 200,
  "message": "库存更新成功",
  "data": null
}
```

**错误响应：**
- `400`：参数错误或库存不足
- `404`：商品不存在

---

### 4. 更新低库存阈值

**接口描述：** 设置指定商品的低库存预警阈值

**请求信息：**
- **URL：** `PUT /inventory/product/{productId}/threshold`
- **方法：** PUT

**路径参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| productId | Long | 是 | 商品ID | 1 |

**查询参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| threshold | Integer | 是 | 低库存阈值（≥0） | 10 |

**请求示例：**
```bash
curl -X PUT "http://localhost:8080/ecommerce-api/inventory/product/1/threshold?threshold=10"
```

**响应示例：**
```json
{
  "code": 200,
  "message": "低库存阈值更新成功",
  "data": null
}
```

**错误响应：**
- `400`：参数错误
- `404`：商品不存在

---

### 5. 批量更新库存

**接口描述：** 批量更新多个商品的库存数量

**请求信息：**
- **URL：** `PUT /inventory/batch-update`
- **方法：** PUT

**查询参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| productIds | String | 是 | 商品ID列表，用逗号分隔 | "1,2,3" |
| stockChanges | String | 是 | 对应的库存变化量列表，用逗号分隔 | "10,-5,20" |

**请求示例：**
```bash
curl -X PUT "http://localhost:8080/ecommerce-api/inventory/batch-update?productIds=1,2,3&stockChanges=10,-5,20"
```

**响应示例：**
```json
{
  "code": 200,
  "message": "批量库存更新成功",
  "data": null
}
```

**错误响应：**
- `400`：参数错误

---

### 6. 删除商品库存记录

**接口描述：** 删除指定商品的库存记录

**请求信息：**
- **URL：** `DELETE /inventory/product/{productId}`
- **方法：** DELETE

**路径参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| productId | Long | 是 | 商品ID | 1 |

**请求示例：**
```bash
curl -X DELETE "http://localhost:8080/ecommerce-api/inventory/product/1"
```

**响应示例：**
```json
{
  "code": 200,
  "message": "库存记录删除成功",
  "data": null
}
```

**错误响应：**
- `400`：参数错误
- `404`：库存记录不存在

---

### 7. 条件查询库存列表

**接口描述：** 根据条件查询库存信息，支持多种筛选条件

**请求信息：**
- **URL：** `POST /inventory/query`
- **Content-Type：** `application/json`

**请求参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| productId | Long | 否 | 商品ID | 1 |
| minStock | Integer | 否 | 最小库存数量 | 10 |
| maxStock | Integer | 否 | 最大库存数量 | 1000 |
| isLowStock | Boolean | 否 | 是否为低库存商品 | true |
| pageNum | Integer | 否 | 页码（≥1），默认1 | 1 |
| pageSize | Integer | 否 | 每页大小（1-100），默认10 | 10 |
| sortBy | String | 否 | 排序字段 | "stock" |
| sortOrder | String | 否 | 排序方向（ASC/DESC） | "DESC" |

**请求示例：**
```bash
curl -X POST "http://localhost:8080/ecommerce-api/inventory/query" \
  -H "Content-Type: application/json" \
  -d '{
    "isLowStock": true,
    "pageNum": 1,
    "pageSize": 10,
    "sortBy": "stock",
    "sortOrder": "ASC"
  }'
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [],
    "total": 0,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

**错误响应：**
- `400`：参数验证失败

---

### 8. 获取库存统计信息

**接口描述：** 获取库存的统计数据，包括总商品数、总库存、低库存商品数等

**请求信息：**
- **URL：** `GET /inventory/stats`
- **方法：** GET

**请求示例：**
```bash
curl -X GET "http://localhost:8080/ecommerce-api/inventory/stats"
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalProducts": 100,
    "totalStock": 5000,
    "lowStockCount": 5,
    "avgStock": 50.0
  }
}
```

---

### 9. 获取低库存商品列表

**接口描述：** 分页查询当前库存低于阈值的商品列表

**请求信息：**
- **URL：** `GET /inventory/low-stock`
- **方法：** GET

**查询参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| page | Integer | 否 | 页码，默认为1 | 1 |
| size | Integer | 否 | 每页大小，默认为10，最大100 | 10 |

**请求示例：**
```bash
curl -X GET "http://localhost:8080/ecommerce-api/inventory/low-stock?page=1&size=10"
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [],
    "total": 0,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

**错误响应：**
- `400`：参数验证失败

---

### 10. 检查商品库存记录

**接口描述：** 检查指定商品是否已有库存记录

**请求信息：**
- **URL：** `GET /inventory/exists/{productId}`
- **方法：** GET

**路径参数：**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| productId | Long | 是 | 商品ID | 1 |

**请求示例：**
```bash
curl -X GET "http://localhost:8080/ecommerce-api/inventory/exists/1"
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

## 数据模型

### InventoryCreateDTO

创建库存记录的请求参数模型：

```json
{
  "productId": 1,
  "initialStock": 100,
  "lowStockThreshold": 10
}
```

### InventoryQueryDTO

查询库存的请求参数模型：

```json
{
  "productId": 1,
  "minStock": 10,
  "maxStock": 1000,
  "isLowStock": true,
  "pageNum": 1,
  "pageSize": 10,
  "sortBy": "stock",
  "sortOrder": "DESC"
}
```

### 库存信息响应模型

```json
{
  "id": 1,
  "productId": 1,
  "stock": 100,
  "lowStockThreshold": 10,
  "updateTime": "2024-01-01T10:00:00"
}
```

### 库存统计响应模型

```json
{
  "totalProducts": 100,
  "totalStock": 5000,
  "lowStockCount": 5,
  "avgStock": 50.0
}
```

## 使用场景

### 1. 新商品上架流程
1. 创建商品信息
2. 调用 `POST /inventory/create` 创建库存记录
3. 设置合适的低库存阈值

### 2. 库存管理流程
1. 使用 `GET /inventory/product/{productId}` 查看当前库存
2. 使用 `PUT /inventory/product/{productId}/stock` 更新库存
3. 使用 `GET /inventory/low-stock` 监控低库存商品

### 3. 批量库存操作
1. 使用 `POST /inventory/query` 查询需要更新的商品
2. 使用 `PUT /inventory/batch-update` 批量更新库存

### 4. 库存统计分析
1. 使用 `GET /inventory/stats` 获取整体统计数据
2. 使用 `GET /inventory/low-stock` 获取预警商品列表

## 注意事项

1. **库存扣减：** 使用负数进行库存扣减时，系统会检查库存是否充足
2. **并发控制：** 高并发场景下建议使用乐观锁或分布式锁
3. **数据一致性：** 库存变更操作建议在事务中执行
4. **性能优化：** 大批量操作建议使用批量更新接口
5. **监控预警：** 建议定期调用低库存查询接口进行预警

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 400 | 参数错误 | 检查请求参数格式和取值范围 |
| 404 | 资源不存在 | 确认商品ID或库存记录是否存在 |
| 500 | 服务器内部错误 | 联系技术支持 |

---

**文档版本：** 1.0  
**最后更新：** 2024年12月  
**维护人员：** 开发团队