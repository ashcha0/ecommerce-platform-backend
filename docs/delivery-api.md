# 配送管理API接口文档

## 接口概述

配送管理模块提供了完整的配送流程管理功能，包括配送信息的创建、查询、更新、状态管理等操作。

## 基础信息

- **基础URL**: `http://localhost:8080/ecommerce-api`
- **接口前缀**: `/api/delivery`
- **数据格式**: JSON
- **字符编码**: UTF-8

## 配送状态说明

| 状态值 | 状态名称 | 描述 |
|--------|----------|------|
| PENDING | 待发货 | 配送信息已创建，等待发货 |
| SHIPPED | 已发货 | 商品已发货，正在配送中 |
| DELIVERED | 已送达 | 商品已送达目的地 |
| CONFIRMED | 已确认 | 客户已确认收货 |
| CANCELLED | 已取消 | 配送已取消 |

## 接口列表

### 1. 分页查询配送列表

**接口地址**: `GET /api/delivery`

**接口描述**: 分页查询配送信息列表，支持多种条件筛选

**请求参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| orderId | Long | 否 | 订单ID | 1001 |
| trackingNo | String | 否 | 物流单号 | SF1234567890 |
| shipper | String | 否 | 物流公司 | 顺丰速运 |
| status | String | 否 | 配送状态 | SHIPPED |
| shipTimeStart | String | 否 | 发货时间开始 | 2024-01-01T00:00:00 |
| shipTimeEnd | String | 否 | 发货时间结束 | 2024-01-31T23:59:59 |
| createTimeStart | String | 否 | 创建时间开始 | 2024-01-01T00:00:00 |
| createTimeEnd | String | 否 | 创建时间结束 | 2024-01-31T23:59:59 |
| pageNum | Integer | 否 | 页码，默认1 | 1 |
| pageSize | Integer | 否 | 每页大小，默认10 | 10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 1,
        "orderId": 1001,
        "trackingNo": "SF1234567890",
        "shipper": "顺丰速运",
        "status": "SHIPPED",
        "estimateTime": "2024-01-15T18:00:00",
        "shipTime": "2024-01-10T10:30:00",
        "deliveryTime": null,
        "confirmTime": null,
        "createTime": "2024-01-10T09:00:00",
        "updateTime": "2024-01-10T10:30:00"
      }
    ],
    "total": 1,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 2. 根据订单ID查询配送信息

**接口地址**: `GET /api/delivery/order/{orderId}`

**接口描述**: 根据订单ID查询对应的配送信息

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| orderId | Long | 是 | 订单ID | 1001 |

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "orderId": 1001,
    "trackingNo": "SF1234567890",
    "shipper": "顺丰速运",
    "status": "SHIPPED",
    "estimateTime": "2024-01-15T18:00:00",
    "shipTime": "2024-01-10T10:30:00",
    "deliveryTime": null,
    "confirmTime": null,
    "createTime": "2024-01-10T09:00:00",
    "updateTime": "2024-01-10T10:30:00"
  }
}
```

**错误响应**:
```json
{
  "code": 404,
  "message": "未找到该订单的配送信息",
  "data": null
}
```

### 3. 根据配送ID查询配送信息

**接口地址**: `GET /api/delivery/{deliveryId}`

**接口描述**: 根据配送ID查询配送详细信息

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| deliveryId | Long | 是 | 配送ID | 1 |

**响应示例**: 同上

### 4. 创建配送信息

**接口地址**: `POST /api/delivery/order/{orderId}`

**接口描述**: 为指定订单创建配送信息

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| orderId | Long | 是 | 订单ID | 1001 |

**响应示例**:
```json
{
  "code": 200,
  "message": "配送信息创建成功",
  "data": {
    "id": 1,
    "orderId": 1001,
    "trackingNo": null,
    "shipper": null,
    "status": "PENDING",
    "estimateTime": null,
    "shipTime": null,
    "deliveryTime": null,
    "confirmTime": null,
    "createTime": "2024-01-10T09:00:00",
    "updateTime": "2024-01-10T09:00:00"
  }
}
```

**错误响应**:
```json
{
  "code": 500,
  "message": "创建配送信息失败: 订单不存在",
  "data": null
}
```

### 5. 更新配送信息

**接口地址**: `PUT /api/delivery/order/{orderId}`

**接口描述**: 更新指定订单的配送信息

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| orderId | Long | 是 | 订单ID | 1001 |

**请求体参数**:
```json
{
  "trackingNo": "SF1234567890",
  "shipper": "顺丰速运",
  "estimateTime": "2024-01-15T18:00:00"
}
```

| 字段名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| trackingNo | String | 否 | 物流单号 | SF1234567890 |
| shipper | String | 否 | 物流公司 | 顺丰速运 |
| estimateTime | String | 否 | 预计送达时间 | 2024-01-15T18:00:00 |

**响应示例**:
```json
{
  "code": 200,
  "message": "配送信息更新成功",
  "data": null
}
```

### 6. 更新配送状态

**接口地址**: `PUT /api/delivery/order/{orderId}/status`

**接口描述**: 更新指定订单的配送状态

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| orderId | Long | 是 | 订单ID | 1001 |

**查询参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| status | String | 是 | 配送状态 | SHIPPED |

**响应示例**:
```json
{
  "code": 200,
  "message": "配送状态更新成功",
  "data": null
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "无效的配送状态: INVALID_STATUS",
  "data": null
}
```

### 7. 发货

**接口地址**: `POST /api/delivery/order/{orderId}/ship`

**接口描述**: 为指定订单进行发货操作，更新物流信息并将状态设置为已发货

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| orderId | Long | 是 | 订单ID | 1001 |

**查询参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| trackingNo | String | 是 | 物流单号 | SF1234567890 |
| shipper | String | 是 | 物流公司 | 顺丰速运 |

**响应示例**:
```json
{
  "code": 200,
  "message": "发货成功",
  "data": null
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "发货失败",
  "data": null
}
```

### 8. 确认收货

**接口地址**: `POST /api/delivery/order/{orderId}/confirm`

**接口描述**: 确认收货，将配送状态设置为已确认

**路径参数**:

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| orderId | Long | 是 | 订单ID | 1001 |

**响应示例**:
```json
{
  "code": 200,
  "message": "确认收货成功",
  "data": null
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "确认收货失败",
  "data": null
}
```

## 错误码说明

| 错误码 | 描述 | 常见原因 |
|--------|------|----------|
| 200 | 操作成功 | - |
| 400 | 请求参数错误 | 参数验证失败、状态无效等 |
| 404 | 资源不存在 | 配送信息不存在、订单不存在等 |
| 500 | 服务器内部错误 | 系统异常、数据库错误等 |

## 使用示例

### 完整的配送流程示例

1. **创建配送信息**
```bash
curl -X POST "http://localhost:8080/ecommerce-api/api/delivery/order/1001"
```

2. **发货**
```bash
curl -X POST "http://localhost:8080/ecommerce-api/api/delivery/order/1001/ship?trackingNo=SF1234567890&shipper=顺丰速运"
```

3. **查询配送信息**
```bash
curl -X GET "http://localhost:8080/ecommerce-api/api/delivery/order/1001"
```

4. **确认收货**
```bash
curl -X POST "http://localhost:8080/ecommerce-api/api/delivery/order/1001/confirm"
```

## 注意事项

1. 所有时间字段均采用ISO 8601格式：`yyyy-MM-ddTHH:mm:ss`
2. 订单ID和配送ID必须为正整数
3. 配送状态更新需要遵循业务流程，不能随意跳转
4. 物流单号和物流公司在发货时为必填项
5. 分页查询支持多种条件组合筛选
6. 所有接口都会返回统一的响应格式