# xinling-admin — APP 管理后台接口文档

> 管理后台中专用于管理 APP 端数据的接口，给前端团队开发对应页面使用。
>
> - 基准 URL：`{host}{context-path}`（当前 context-path = `/`）
> - 认证方式：`Authorization: Bearer {token}`
> - 统一响应格式见文末[通用说明](#通用说明)

---

## 模块总览

| # | 模块 | 基路径 | 后端类 |
|---|------|--------|--------|
| 1 | [APP用户管理](#1-app用户管理) | `/app/user` | `AppUserController` |
| 2 | [会员管理](#2-会员管理) | `/app/vip` | `VipController` |
| 3 | [会员赠送管理](#3-会员赠送管理) | `/app/vip/gift` | `VipGiftController` |
| 4 | [成长体系管理](#4-成长体系管理) | `/app/growth` | `GrowthController` |
| 5 | [活动管理](#5-活动管理) | `/app/activity` | `ActivityController` |
| 6 | [优惠券管理](#6-优惠券管理) | `/app/coupon` | `CouponController` |
| 7 | [订单管理](#7-订单管理) | `/app/order` | `OrderController` |
| 8 | [支付管理](#8-支付管理) | `/app/pay` | `PayController` |
| 9 | [分销管理](#9-分销管理) | `/app/distribution` | `DistributionController` |
| 10 | [动态管理](#10-动态管理) | `/app/moment` | `MomentController` |
| 11 | [专注管理](#11-专注管理) | `/app/focus` | `FocusController` |
| 12 | [睡眠管理](#12-睡眠管理) | `/app/sleep` | `SleepController` |
| 13 | [通知推送](#13-通知推送) | `/app/notification` | `NotificationController` |
| 14 | [白名单管理](#14-白名单管理) | `/app/whitelist` | `WhitelistController` |

---

## 1. APP用户管理

**基路径：** `/app/user` | **类名：** `AppUserController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/user/list` | `app:user:list` | 分页查询用户列表 |
| GET | `/app/user/{id}` | `app:user:query` | 获取用户详情 |
| GET | `/app/user/count` | `app:user:list` | 获取用户总数 |
| PUT | `/app/user` | `app:user:edit` | 修改用户信息（nickname、email、gender） |
| PUT | `/app/user/status` | `app:user:edit` | 修改用户状态（启用/禁用） |
| PUT | `/app/user/vip` | `app:user:edit` | 设置 VIP |
| POST | `/app/user/vip/extend` | `app:user:edit` | 延长 VIP |
| DELETE | `/app/user/{id}` | `app:user:remove` | 删除用户（软删除） |
| DELETE | `/app/user/batch/{ids}` | `app:user:remove` | 批量删除 |
| POST | `/app/user/export` | `app:user:export` | 导出用户 Excel |

**查询参数（list）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| nickname | String | 否 | 昵称模糊搜索 |
| phone | String | 否 | 手机号精确匹配 |
| status | Integer | 否 | 0=正常 1=禁用 |
| vipStatus | Integer | 否 | VIP状态 |
| beginTime | String | 否 | 开始时间 |
| endTime | String | 否 | 结束时间 |

**修改状态：** `PUT /app/user/status?userId=1&status=1`
**设置 VIP：** `PUT /app/user/vip` body: `{ "id":1, "vipStatus":1, "vipEndTime":"2026-12-31T23:59:59" }`
**延长 VIP：** `POST /app/user/vip/extend?userId=1&days=30`
**批量删除：** `DELETE /app/user/batch/1,2,3`

---

## 2. 会员管理

**基路径：** `/app/vip` | **类名：** `VipController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/vip/package/list` | `app:vip:list` | 分页查询套餐列表 |
| POST | `/app/vip/package/create` | `app:vip:add` | 新增套餐 |
| POST | `/app/vip/package/update` | `app:vip:edit` | 修改套餐 |
| POST | `/app/vip/package/status` | `app:vip:edit` | 套餐上下线 |
| GET | `/app/vip/user/list` | `app:vip:list` | 分页查询用户会员列表 |

**套餐上下线：** body: `{ "id":1, "status":0 }`（0=下架 1=上架）

---

## 3. 会员赠送管理

**基路径：** `/app/vip/gift` | **类名：** `VipGiftController`

### 赠送规则

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/vip/gift/rule/list` | `app:vip:gift:list` | 规则列表 |
| GET | `/app/vip/gift/rule/{id}` | `app:vip:gift:list` | 规则详情 |
| POST | `/app/vip/gift/rule/create` | `app:vip:gift:add` | 新增规则 |
| POST | `/app/vip/gift/rule/update` | `app:vip:gift:edit` | 修改规则 |
| POST | `/app/vip/gift/rule/status` | `app:vip:gift:edit` | 启用/禁用 |
| DELETE | `/app/vip/gift/rule/{id}` | `app:vip:gift:remove` | 删除规则 |

### 赠送记录 & 手动赠送

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/vip/gift/record/list` | `app:vip:gift:list` | 赠送记录列表 |
| POST | `/app/vip/gift/grant` | `app:vip:gift:grant` | 手动赠送会员 |
| GET | `/app/vip/gift/statistics` | `app:vip:gift:list` | 赠送统计 |

**手动赠送 body：**
```json
{ "userId":1, "vipDays":30, "reason":"活动奖励", "ruleId":1 }
```
> `vipDays=0` = 终身会员；`ruleId` 可选

**统计返回：** `totalRules`、`activeRules`、`totalGrants`、`autoGrants`、`manualGrants`

---

## 4. 成长体系管理

**基路径：** `/app/growth` | **类名：** `GrowthController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/growth/achievement/list` | `app:growth:achievement:list` | 成就列表 |
| POST | `/app/growth/achievement/create` | `app:growth:achievement:create` | 新增成就 |
| GET | `/app/growth/task/list` | `app:growth:task:list` | 每日任务列表 |
| POST | `/app/growth/task/create` | `app:growth:task:create` | 新增任务 |
| GET | `/app/growth/mall/list` | `app:growth:mall:list` | 积分商品列表 |
| POST | `/app/growth/mall/create` | `app:growth:mall:create` | 新增商品 |
| POST | `/app/growth/mall/update` | `app:growth:mall:update` | 修改商品 |

---

## 5. 活动管理

**基路径：** `/app/activity` | **类名：** `ActivityController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/activity/list` | `app:activity:list` | 活动列表（title/type/status 筛选） |
| POST | `/app/activity/create` | `app:activity:create` | 新增活动 |
| POST | `/app/activity/update/{id}` | `app:activity:update` | 修改活动 |
| POST | `/app/activity/online/{id}` | `app:activity:online` | 发布活动（status→1） |
| POST | `/app/activity/offline/{id}` | `app:activity:offline` | 下架活动（status→0） |
| GET | `/app/activity/statistics/{id}` | `app:activity:statistics` | 活动统计 |

---

## 6. 优惠券管理

**基路径：** `/app/coupon` | **类名：** `CouponController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/coupon/list` | `app:coupon:list` | 优惠券列表（status 筛选） |
| POST | `/app/coupon/create` | `app:coupon:create` | 新增优惠券 |
| POST | `/app/coupon/update/{id}` | `app:coupon:update` | 修改优惠券 |
| POST | `/app/coupon/grant/{id}` | `app:coupon:grant` | 发放给指定用户（TODO 占位） |
| GET | `/app/coupon/statistics/{id}` | `app:coupon:statistics` | 使用统计 |

**发放：** body: `[1001, 1002, 1003]`（用户ID列表）
**统计返回：** `{ coupon, totalCount, usedCount }`

---

## 7. 订单管理

**基路径：** `/app/order` | **类名：** `OrderController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/order/list` | `app:order:list` | 订单列表（PayOrder 字段筛选） |
| GET | `/app/order/detail/{orderNo}` | `app:order:query` | 订单详情 |
| POST | `/app/order/cancel/{orderNo}` | `app:order:edit` | 取消订单 |
| POST | `/app/order/auditRefund/{orderNo}` | `app:order:auditRefund` | 审核退款 |
| POST | `/app/order/export` | `app:order:export` | 导出 Excel |

**审核退款 body：**
```json
{ "refundStatus": 1, "auditRemark": "同意退款" }
```
> refundStatus: 1=通过 2=拒绝

---

## 8. 支付管理

**基路径：** `/app/pay` | **类名：** `PayController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/pay/config` | `app:pay:config:list` | 支付配置列表 |
| POST | `/app/pay/config/update` | `app:pay:config:update` | 更新配置 |
| GET | `/app/pay/transaction/list` | `app:pay:transaction:list` | 交易记录 |
| POST | `/app/pay/refund/audit` | `app:pay:refund:audit` | 退款审核 |

**退款审核 body：**
```json
{ "id":1, "refundStatus":1, "auditRemark":"同意退款" }
```

---

## 9. 分销管理

**基路径：** `/app/distribution` | **类名：** `DistributionController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/distribution/list` | `app:distribution:list` | 分销员列表 |
| GET | `/app/distribution/detail/{id}` | `app:distribution:query` | 分销员详情 |
| POST | `/app/distribution/audit/{id}` | `app:distribution:audit` | 审核分销员 |
| GET | `/app/distribution/order/list` | `app:distribution:list` | 分销订单 |
| GET | `/app/distribution/commission/list` | `app:distribution:list` | 佣金记录 |
| GET | `/app/distribution/withdraw/list` | `app:distribution:list` | 提现申请列表 |
| POST | `/app/distribution/withdraw/audit/{id}` | `app:distribution:audit` | 审核提现 |
| POST | `/app/distribution/settings/update` | `app:distribution:edit` | 更新分销设置 |

**审核 body：** 包含 `status`（审核状态）和 `auditRemark`（审核备注）

---

## 10. 动态管理

**基路径：** `/app/moment` | **类名：** `MomentController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/moment/list` | `app:moment:list` | 动态列表（userId/type 筛选） |
| GET | `/app/moment/detail/{id}` | `app:moment:detail` | 动态详情 |
| POST | `/app/moment/delete/{id}` | `app:moment:delete` | 隐藏（软删除） |

---

## 11. 专注管理

**基路径：** `/app/focus` | **类名：** `FocusController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/focus/list` | `app:focus:list` | 专注记录列表 |
| GET | `/app/focus/{id}` | `app:focus:query` | 记录详情 |

---

## 12. 睡眠管理

**基路径：** `/app/sleep` | **类名：** `SleepController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/sleep/list` | `app:sleep:list` | 睡眠记录列表 |
| GET | `/app/sleep/{id}` | `app:sleep:query` | 记录详情 |

---

## 13. 通知推送

**基路径：** `/app/notification` | **类名：** `NotificationController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| POST | `/app/notification/push` | `app:notification:push` | 创建推送任务 |
| GET | `/app/notification/task/list` | `app:notification:task:list` | 推送任务列表 |
| POST | `/app/notification/task/retry/{id}` | `app:notification:task:retry` | 重试失败任务 |

---

## 14. 白名单管理

**基路径：** `/app/whitelist` | **类名：** `WhitelistController`

| 方法 | 路径 | 权限标识 | 说明 |
|------|------|----------|------|
| GET | `/app/whitelist/list` | `app:whitelist:list` | 白名单列表 |
| POST | `/app/whitelist/add` | `app:whitelist:add` | 新增白名单 |
| POST | `/app/whitelist/delete/{id}` | `app:whitelist:remove` | 删除 |
| POST | `/app/whitelist/updateStatus/{id}` | `app:whitelist:edit` | 修改状态 |

---

## 通用说明

### 分页

所有 `TableDataInfo` 返回的接口支持：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| pageNum | Integer | 1 | 页码 |
| pageSize | Integer | 10 | 每页条数 |

### 响应格式

**成功：**
```json
{ "code": 200, "msg": "操作成功", "data": {} }
```

**分页：**
```json
{ "code": 200, "msg": "查询成功", "total": 100, "rows": [] }
```

**失败：**
```json
{ "code": 500, "msg": "错误信息" }
```

### 后端类所在包

```
com.xinling.admin.controller.app
├── AppUserController.java
├── ActivityController.java
├── CouponController.java
├── DistributionController.java
├── FocusController.java
├── GrowthController.java
├── MomentController.java
├── NotificationController.java
├── OrderController.java
├── PayController.java
├── SleepController.java
├── VipController.java
├── VipGiftController.java
└── WhitelistController.java
```