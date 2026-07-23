# 文件记录模块 API 文档

> 更新日期：2026-06-22

---

## 目录

1. [数据模型](#1-数据模型)
2. [基础字段说明](#2-基础字段说明)
3. [文件记录 API](#3-文件记录-api)
   - 3.1 [分页查询文件列表](#31-分页查询文件列表)
   - 3.2 [获取文件详情](#32-获取文件详情)
   - 3.3 [上传文件](#33-上传文件)
   - 3.4 [批量上传文件](#34-批量上传文件)
   - 3.5 [下载文件](#35-下载文件)
   - 3.6 [修改文件记录](#36-修改文件记录)
   - 3.7 [删除文件记录](#37-删除文件记录)
   - 3.8 [按业务查询文件列表](#38-按业务查询文件列表)
   - 3.9 [获取文件统计概览](#39-获取文件统计概览)
   - 3.10 [导出文件记录](#310-导出文件记录)
4. [文件统计 API](#4-文件统计-api)
   - 4.1 [统计概览](#41-统计概览)
   - 4.2 [存储类型分布](#42-存储类型分布)
   - 4.3 [文件类型分布](#43-文件类型分布)
   - 4.4 [上传趋势](#44-上传趋势)
   - 4.5 [存储趋势](#45-存储趋势)

---

## 1. 数据模型

### FileRecord 完整字段

```json
{
  "fileId": 1,
  "fileUuid": "a1b2c3d4e5f6g7h8",
  "configId": 1,
  "fileName": "报告.docx",
  "storedName": "a1b2c3d4e5f6g7h8.docx",
  "filePath": "uploads/2026/06/22/a1b2c3d4e5f6g7h8.docx",
  "fileUrl": "https://oss.xxx.com/uploads/2026/06/22/a1b2c3d4e5f6g7h8.docx",
  "fileSize": 1048576,
  "fileType": "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  "fileExtension": "docx",
  "fileHash": "d41d8cd98f00b204e9800998ecf8427e",
  "storageType": "minio",
  "bucketName": "xinling-files",
  "objectKey": "uploads/2026/06/22/a1b2c3d4e5f6g7h8.docx",
  "thumbnailUrl": "https://oss.xxx.com/thumbnails/a1b2c3.jpg",
  "imageWidth": 1920,
  "imageHeight": 1080,
  "duration": null,
  "businessType": "avatar",
  "businessId": 100,
  "businessTable": "sys_user",
  "businessField": "avatar",
  "sourceType": "UPLOAD",
  "isPublic": 0,
  "accessLevel": "PRIVATE",
  "uploaderId": 1,
  "uploaderName": "admin",
  "uploaderIp": "192.168.1.100",
  "downloadCount": 5,
  "referenceCount": 2,
  "status": "0",
  "deleteBy": null,
  "deleteTime": null,
  "expireTime": null,
  "tenantId": 0,
  "remark": "备注信息",
  "createTime": "2026-06-22 10:30:00",
  "updateTime": "2026-06-22 14:00:00"
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| fileId | Long | 是 | 文件ID（自增主键） |
| fileUuid | String | 是 | 文件唯一标识，上传时自动生成 |
| configId | Long | 否 | 存储配置ID |
| fileName | String | 是 | 原始文件名，如 `报告.docx` |
| storedName | String | 否 | 存储文件名，格式 `{uuid}.{ext}` |
| filePath | String | 否 | 文件存储相对路径 |
| fileUrl | String | 否 | 文件访问URL（可直接打开） |
| fileSize | Long | 是 | 文件大小（字节） |
| fileType | String | 否 | MIME 类型，如 `image/png` |
| fileExtension | String | 否 | 文件扩展名，如 `png` |
| fileHash | String | 否 | 文件 MD5 哈希值（自动计算，可用于去重） |
| storageType | String | 是 | 存储类型：`local` / `minio` / `oss` / `s3` |
| bucketName | String | 否 | 对象存储桶名称 |
| objectKey | String | 否 | 对象存储 Key |
| thumbnailUrl | String | 否 | 缩略图URL（图片文件适用） |
| imageWidth | Integer | 否 | 图片宽度（像素） |
| imageHeight | Integer | 否 | 图片高度（像素） |
| duration | Integer | 否 | 媒体时长（秒，音视频文件适用） |
| businessType | String | 否 | 业务类型，如：`avatar` / `document` / `attachment` |
| businessId | Long | 否 | 业务关联ID |
| businessTable | String | 否 | 业务表名，如 `sys_user` |
| businessField | String | 否 | 业务字段，如 `avatar` |
| sourceType | String | 是 | 来源：`UPLOAD` / `IMPORT` / `EXPORT` / `AI` / `SYSTEM` |
| isPublic | Integer | 否 | 是否公开访问：`0`=否 `1`=是 |
| accessLevel | String | 是 | 权限级别：`PUBLIC` / `PRIVATE` / `ROLE` / `DEPT` |
| uploaderId | Long | 否 | 上传用户ID |
| uploaderName | String | 否 | 上传用户名 |
| uploaderIp | String | 否 | 上传者IP地址 |
| downloadCount | Integer | 否 | 下载次数 |
| referenceCount | Integer | 否 | 引用次数 |
| status | String | 是 | 状态：`0`=正常 `1`=已删除 |
| deleteBy | Long | 否 | 删除人ID |
| deleteTime | Date | 否 | 删除时间 |
| expireTime | Date | 否 | 过期时间（临时文件用到） |
| tenantId | Long | 否 | 租户ID（多租户场景） |
| remark | String | 否 | 备注 |
| createTime | Date | 是 | 创建时间 |
| updateTime | Date | 是 | 更新时间 |

### sourceType 枚举

| 值 | 说明 |
|----|------|
| UPLOAD | 用户上传（默认） |
| IMPORT | 导入 |
| EXPORT | 导出生成 |
| AI | AI 生成 |
| SYSTEM | 系统生成 |

### accessLevel 枚举

| 值 | 说明 |
|----|------|
| PUBLIC | 公开访问，无需鉴权 |
| PRIVATE | 私有（默认），仅本人可访问 |
| ROLE | 按角色控制访问 |
| DEPT | 按部门控制访问 |

### 状态说明

- `0` **正常**：文件可用，正常展示
- `1` **已删除**：文件被软删除（仅标记删除，物理文件可能保留）

> ⚠ **删除行为变更**：删除操作现为 **软删除**（标记 `status='1'`），不再物理删除数据库记录。后端会同时清理存储服务器上的物理文件。

---

## 2. 基础字段说明

### （空——前端的权限字段、分页参数等可在后续补充）

---

## 3. 文件记录 API

基础路径：`/file/record`

所有列表接口支持分页，需要传 `pageNum`（页码）和 `pageSize`（每页条数）。

---

### 3.1 分页查询文件列表

**GET** `/file/record/list`

**权限**：`file:record:list`

**请求参数（Query）**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |
| fileName | String | 否 | 原始文件名（模糊搜索） |
| storageType | String | 否 | 存储类型：`local`/`minio`/`oss`/`s3` |
| businessType | String | 否 | 业务类型 |
| businessId | Long | 否 | 业务ID（精确匹配） |
| businessTable | String | 否 | 业务表名 |
| businessField | String | 否 | 业务字段 |
| sourceType | String | 否 | 来源：`UPLOAD`/`IMPORT`/`EXPORT`/`AI`/`SYSTEM` |
| isPublic | Integer | 否 | 是否公开：`0`=否 `1`=是 |
| accessLevel | String | 否 | 权限级别 |
| fileType | String | 否 | MIME 类型 |
| uploaderName | String | 否 | 上传者用户名（模糊搜索） |
| status | String | 否 | 状态：`0`=正常 `1`=已删除 |
| tenantId | Long | 否 | 租户ID |
| params[beginTime] | String | 否 | 开始时间 `yyyy-MM-dd` |
| params[endTime] | String | 否 | 结束时间 `yyyy-MM-dd` |

**响应示例**：

```json
{
  "code": 200,
  "msg": "操作成功",
  "total": 100,
  "rows": [
    {
      "fileId": 1,
      "fileUuid": "a1b2c3d4",
      "fileName": "报告.docx",
      "fileUrl": "https://...",
      "fileSize": 1048576,
      "fileExtension": "docx",
      "storageType": "minio",
      "sourceType": "UPLOAD",
      "isPublic": 0,
      "uploaderName": "admin",
      "status": "0",
      "createTime": "2026-06-22 10:30:00",
      "updateTime": "2026-06-22 14:00:00"
    }
  ]
}
```

---

### 3.2 获取文件详情

**GET** `/file/record/{fileId}`

**权限**：`file:record:query`

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| fileId | Long | 是 | 文件ID |

**响应**：返回完整的 FileRecord 对象。

---

### 3.3 上传文件

**POST** `/file/record/upload`

**权限**：`file:record:add`

**请求格式**：`multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 上传文件 |
| businessType | String | 否 | 业务类型 |
| businessId | Long | 否 | 业务ID |

**系统自动填充的字段**：

| 字段 | 值 |
|------|----|
| fileUuid | 自动生成 UUID |
| storedName | `{uuid}.{ext}` |
| fileHash | 自动计算 MD5 |
| sourceType | `UPLOAD` |
| isPublic | `0`（私有） |
| accessLevel | `PRIVATE` |
| downloadCount | `0` |
| referenceCount | `0` |
| status | `0`（正常） |
| tenantId | `0` |

**响应**：返回完整的 FileRecord 对象。

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "fileId": 1,
    "fileUuid": "a1b2c3d4",
    "fileName": "photo.jpg",
    "fileUrl": "https://oss.xxx.com/uploads/2026/06/22/a1b2c3d4.jpg",
    "sourceType": "UPLOAD",
    "isPublic": 0,
    "accessLevel": "PRIVATE",
    "status": "0",
    "createTime": "2026-06-22 10:30:00"
  }
}
```

---

### 3.4 批量上传文件

**POST** `/file/record/uploadBatch`

**权限**：`file:record:add`

**请求格式**：`multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| files | File[] | 是 | 多个文件 |
| businessType | String | 否 | 业务类型 |
| businessId | Long | 否 | 业务ID |

**响应**：返回 FileRecord 对象数组。

---

### 3.5 下载文件

**GET** `/file/record/download/{fileId}`

**权限**：`file:record:download`

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| fileId | Long | 是 | 文件ID |

**说明**：该接口直接返回文件二进制流（`Content-Disposition: attachment`），浏览器会触发下载。后端会自动增加该文件的 `downloadCount`。

---

### 3.6 修改文件记录

**PUT** `/file/record`

**权限**：`file:record:edit`

**请求体（JSON）**：

```json
{
  "fileId": 1,
  "fileName": "新文件名.docx",
  "businessType": "document",
  "businessId": 200,
  "businessTable": "sys_document",
  "businessField": "file_id",
  "sourceType": "IMPORT",
  "isPublic": 1,
  "accessLevel": "PUBLIC",
  "expireTime": "2026-07-22 10:30:00",
  "remark": "更新备注"
}
```

**说明**：只传需要修改的字段即可。以下字段 **不可修改**（系统自动维护）：`fileUuid`、`configId`、`storedName`、`filePath`、`fileUrl`、`fileSize`、`fileType`、`fileExtension`、`fileHash`、`storageType`、`bucketName`、`objectKey`、`uploaderId`、`uploaderName`、`uploaderIp`、`downloadCount`、`referenceCount`。

---

### 3.7 删除文件记录

**DELETE** `/file/record/{fileIds}`

**权限**：`file:record:remove`

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| fileIds | Long[] | 是 | 文件ID数组，逗号分隔 |

**说明**：
- 采用 **软删除**，`status` 标记为 `1`，同时记录 `deleteBy` 和 `deleteTime`
- 后端会自动清理存储服务器上的物理文件
- 已删除状态的记录不会在列表中展示（默认查询 `status='0'`）

**响应**：

```json
{
  "code": 200,
  "msg": "操作成功"
}
```

---

### 3.8 按业务查询文件列表

**GET** `/file/record/business/{businessType}/{businessId}`

**权限**：`file:record:list`

**路径参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| businessType | String | 是 | 业务类型 |
| businessId | Long | 是 | 业务ID |

**说明**：查询指定业务关联的所有正常文件（`status='0'`），按创建时间倒序。

**响应**：支持分页，数据格式同 3.1。

---

### 3.9 获取文件统计概览

**GET** `/file/record/statistics`

**权限**：`file:record:list`

**响应**：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "totalCount": 1000,
    "totalSize": 1073741824,
    "totalDownloads": 500,
    "todayUploads": 12,
    "details": [
      {
        "storageType": "minio",
        "fileCount": 800,
        "totalSize": 536870912,
        "totalDownloads": 300
      },
      {
        "storageType": "local",
        "fileCount": 200,
        "totalSize": 536870912,
        "totalDownloads": 200
      }
    ]
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| totalCount | Integer | 总文件数 |
| totalSize | Long | 总字节数 |
| totalDownloads | Integer | 总下载次数 |
| todayUploads | Integer | 今日上传数 |
| details | Array | 按存储类型分组的详情 |

---

### 3.10 导出文件记录

**POST** `/file/record/export`

**权限**：`file:record:export`

**请求参数**：同 3.1 分页查询的参数。

**说明**：导出 Excel 文件，支持的导出字段包括所有带 `@Excel` 注解的字段（如 fileName、fileSize、storageType、sourceType、isPublic、status 等）。

---

## 4. 文件统计 API

基础路径：`/file/stat`

---

### 4.1 统计概览

**GET** `/file/stat/overview`

**权限**：`file:stat:overview`

**响应**：同 3.9 `GET /file/record/statistics`。

---

### 4.2 存储类型分布

**GET** `/file/stat/storageType`

**权限**：`file:stat:storageType`

**响应**：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "distribution": [
      { "name": "minio", "count": 800, "size": 536870912 },
      { "name": "local", "count": 200, "size": 536870912 }
    ]
  }
}
```

---

### 4.3 文件类型分布

**GET** `/file/stat/fileType`

**权限**：`file:stat:fileType`

按文件扩展名统计，取前10个最常见的类型。

**响应**：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "distribution": [
      { "name": "jpg", "count": 400, "size": 104857600 },
      { "name": "png", "count": 200, "size": 52428800 },
      { "name": "pdf", "count": 100, "size": 39321600 }
    ]
  }
}
```

---

### 4.4 上传趋势

**GET** `/file/stat/uploadTrend`

**权限**：`file:stat:uploadTrend`

**请求参数（Query）**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| days | Integer | 否 | 7 | 最近天数（与 startDate/endDate 互斥） |
| startDate | String | 否 | - | 开始日期 `yyyy-MM-dd` |
| endDate | String | 否 | - | 结束日期 `yyyy-MM-dd` |

**响应**：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "dates": ["2026-06-16", "2026-06-17", "2026-06-18"],
    "uploadCounts": [10, 15, 8],
    "storageSizes": [1048576, 2097152, 524288]
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| dates | String[] | 日期列表 |
| uploadCounts | Long[] | 每日上传文件数 |
| storageSizes | Long[] | 每日新增存储量（字节） |

---

### 4.5 存储趋势

**GET** `/file/stat/storageTrend`

**权限**：`file:stat:storageTrend`

**请求参数（Query）**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| days | Integer | 否 | 7 | 最近天数（与 startDate/endDate 互斥） |
| startDate | String | 否 | - | 开始日期 `yyyy-MM-dd` |
| endDate | String | 否 | - | 结束日期 `yyyy-MM-dd` |

**响应**：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "dates": ["2026-06-16", "2026-06-17", "2026-06-18"],
    "cumulativeSizes": [1048576, 3145728, 3670016]
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| dates | String[] | 日期列表 |
| cumulativeSizes | Long[] | 累计存储量（字节），每日递进累加 |

---

## 5. 权限矩阵

| 接口 | 权限标识 |
|------|----------|
| 文件记录列表 | `file:record:list` |
| 文件记录详情 | `file:record:query` |
| 上传文件 | `file:record:add` |
| 修改文件记录 | `file:record:edit` |
| 删除文件记录 | `file:record:remove` |
| 下载文件 | `file:record:download` |
| 导出文件记录 | `file:record:export` |
| 统计概览 | `file:stat:overview` |
| 存储类型分布 | `file:stat:storageType` |
| 文件类型分布 | `file:stat:fileType` |
| 上传趋势 | `file:stat:uploadTrend` |
| 存储趋势 | `file:stat:storageTrend` |

---

## 6. 前端注意事项

1. **分页列表中的字段**：列表接口返回完整的 `FileRecord` 对象。前端展示时可根据需要选取字段，建议包含：`fileId`、`fileName`、`fileUrl`（可展示预览缩略图/图标）、`fileSize`（需格式化为 MB/KB）、`fileExtension`、`sourceType`、`isPublic`、`status`、`uploaderName`、`createTime`。

2. **文件预览**：`fileUrl` 可直接用于 `<img>` / `<video>` / `<audio>` 标签或 `window.open()`。`thumbnailUrl` 适用于图片列表模式下的缩略图。

3. **媒体文件判断**：`imageWidth` / `imageHeight` 非空即为图片；`duration` 非空即为音视频文件。

4. **删除操作**：现在是软删除，删除后前端列表默认不展示。如需恢复可调用修改接口将 `status` 改回 `0`（后续预留恢复功能）。

5. **上传成功后的字段**：上传接口返回完整的 FileRecord，前端应保存 `fileId` 和 `fileUrl` 用于业务关联。
