# 内容管理模块 API 文档

> **注意：本文档已过时，对应旧版数据模型。**  
> 请以 [`音频内容模块API文档.md`](./音频内容模块API文档.md) 为准。
>
> 主要变更：
> - 冥想内容从 `audio_item.category=meditation` 改为独立的 `meditation` 表 + `meditation_audio` 关联
> - 新增全局 `teacher` 老师库，`meditation_author` 表废弃
> - 音频和老师通过 `meditation_audio.author_id` 一次关联，无需两套接口
>
> 管理后台 — 内容数据目录下的三个子模块：音频库（含混音）、冥想内容、睡眠内容（含记录/日记）
>
> 基于重构后的 `audio_item` + `audio_item_bg` + `audio_mix` 表结构
>
> 最后更新: 2026-06-18

---

## 设计说明

音频文件统一通过 **文件管理模块** 上传和管理，内容模块只维护元数据。

```
文件管理                             内容管理
─────────                           ─────────
/file/record/upload                 /content/audio/item/create
  → 上传音频文件                       → 引用 fileId
  → 返回 {fileId, fileUrl}            → 后端自动回填 audioUrl
                                      → 补充 title/description/narrator 等元数据
  → sys_file_record                  → audio_item（file_id 外键）
```

**前端流程：**
1. 调 `/file/record/upload?businessType=audio` 上传音频文件 → 拿到 `fileId` + `fileUrl`
2. 调内容管理接口创建内容时传入 `fileId`
3. 后端自动根据 `fileId` 从文件系统查询 `fileUrl` 并回填到 `audioUrl`
4. 背景图通过 `/file/record/upload?businessType=image` 上传后传入 URL

---

## 目录结构

```
内容数据 (菜单)
├── 音频库管理  →  /content/audio
│   └── 混音组合  →  /content/audio/mix
├── 冥想内容    →  /content/meditation
└── 睡眠内容    →  /content/sleep
    ├── 睡眠音频  →  /content/sleep/audio
    ├── 睡眠记录  →  /content/sleep/record
    └── 睡眠日记  →  /content/sleep/diary
```

---

## 通用说明

### 分页

所有 `list` 接口自动支持 `pageNum` 和 `pageSize` 参数，由后端 PageHelper 拦截处理。

### 响应格式

```json
{
  "code": 200,
  "msg": "操作成功",
  "total": 17,        // 仅列表接口
  "rows": [...]       // 仅列表接口
}
```

### 删除说明

所有删除均为软删除（`is_deleted = 1`），数据不从数据库移除。

### 上架/下架

`status = 1` 上架 / `status = 0` 下架，下架内容在 APP 端不可见。

### 文件引用

创建/修改音频内容时传入 `fileId`（通过 `/file/record/upload` 获得），`audioUrl` 由后端自动回填。前端无需传入 `audioUrl`。

---

## 1. 音频库管理 — `/content/audio`

> 管理整个音频库（所有分类的音频素材）和混音预设。

### 1.1 音频库

#### 1.1.1 查询音频列表

```
GET /content/audio/item/list
```

**请求参数（Query）：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| category | String | 否 | 分类筛选：`meditation` / `sleep` / `white_noise` / `focus` / 空=全部 |
| keyword | String | 否 | 关键词搜索（标题或标签） |
| status | Integer | 否 | 状态：0=下架, 1=上架, 空=全部 |

**响应：**
```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 17,
  "rows": [
    {
      "id": 1,
      "fileId": 10,
      "title": "雨声轻敲",
      "description": "细雨敲打窗棂的自然白噪音，适合睡眠",
      "coverUrl": "https://...",
      "audioUrl": "https://...",
      "duration": 3600,
      "category": "white_noise",
      "subType": "nature",
      "narrator": "",
      "difficulty": "",
      "tags": ["雨", "自然", "睡眠"],
      "playCount": 1280,
      "status": 1,
      "sortOrder": 1,
      "createTime": "2026-06-01 10:00:00",
      "updateTime": "2026-06-10 14:00:00",
      "backgroundImages": [
        {
          "id": 1,
          "audioItemId": 1,
          "url": "https://...",
          "sortOrder": 0
        }
      ]
    }
  ]
}
```

> `backgroundImages` 为该音频关联的背景图列表，按 `sort_order` 升序排列。

#### 1.1.2 获取音频详情

```
GET /content/audio/item/{id}
```

**响应：** 格式同列表中的单条记录。

---

#### 1.1.3 新增音频

```
POST /content/audio/item/create
```

**请求体（JSON）：**
```json
{
  "fileId": 10,
  "title": "森林鸟鸣",
  "description": "清晨森林中的鸟鸣声，放松身心",
  "coverUrl": "https://...",
  "duration": 1800,
  "category": "white_noise",
  "subType": "nature",
  "narrator": "",
  "difficulty": "",
  "tags": ["森林","鸟","自然"],
  "status": 1,
  "sortOrder": 5
}
```

> - `fileId` 必须通过 `/file/record/upload?businessType=audio` 上传后获得
> - `audioUrl` 由后端自动回填，前端无需传入
> - 如需新增背景图，先创建音频拿到 `id`，再通过专门的背景图接口管理

---

#### 1.1.4 修改音频

```
POST /content/audio/item/update/{id}
```

**请求体：** 同新增，可只传需要修改的字段。如需更换音频文件，传入新的 `fileId`。

---

#### 1.1.5 删除音频（软删除）

```
POST /content/audio/item/delete/{id}
```

---

#### 1.1.6 上架音频

```
POST /content/audio/item/online/{id}
```

#### 1.1.7 下架音频

```
POST /content/audio/item/offline/{id}
```

---

### 1.2 音频背景图

> 每个音频可以有多个背景图，通过 `audio_item_bg` 关联表存储。
>
> 查询音频详情时，`backgroundImages` 字段会自动通过 MyBatis 的 `<collection>` 映射附带返回。
> 如需在管理后台维护背景图，当前通过第三方文件管理模块上传后，直接 SQL 管理 `audio_item_bg` 表。

**查询时自动附带：**
```json
{
  "backgroundImages": [
    {"id": 1, "audioItemId": 1, "url": "https://...", "sortOrder": 0},
    {"id": 2, "audioItemId": 1, "url": "https://...", "sortOrder": 1}
  ]
}
```

---

### 1.3 混音组合

> 混音组合是多个音频的预设组合，只引用 `audio_item.id`，不关联文件系统。

#### 1.3.1 查询混音列表

```
GET /content/audio/mix/list
```

**请求参数：**

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 按名称搜索 |
| status | Integer | 否 | 0=停用, 1=启用, 空=全部 |

**响应：**
```json
{
  "code": 200,
  "rows": [
    {
      "id": 1,
      "name": "深度睡眠组合",
      "description": "雨声+篝火+白噪音，构建最佳睡眠环境",
      "coverUrl": "",
      "audioIds": "[1,4,6]",
      "isDefault": 1,
      "sortOrder": 1,
      "status": 1,
      "createTime": "2026-06-01 10:00:00",
      "updateTime": "2026-06-10 14:00:00"
    }
  ]
}
```

> `audioIds` 以 JSON 数组字符串存储，如 `"[1,4,6]"`，APP 端解析后查询对应音频详情。

#### 1.3.2 获取混音详情

```
GET /content/audio/mix/{id}
```

#### 1.3.3 新增混音

```
POST /content/audio/mix/create
```

**请求体：**
```json
{
  "name": "放松时刻",
  "description": "适合午休的组合",
  "coverUrl": "",
  "audioIds": "[2,3,7]",
  "isDefault": 0,
  "sortOrder": 3,
  "status": 1
}
```

#### 1.3.4 修改混音

```
POST /content/audio/mix/update/{id}
```

#### 1.3.5 删除混音

```
POST /content/audio/mix/delete/{id}
```

---

## 2. 冥想内容管理 — `/content/meditation`

> 管理冥想分类下的音频内容（`category` 固定为 `meditation`）。

#### 2.1 查询冥想音频列表

```
GET /content/meditation/list
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 关键词搜索 |
| status | Integer | 否 | 0=下架, 1=上架, 空=全部 |

**响应：**
```json
{
  "code": 200,
  "rows": [
    {
      "id": 7,
      "fileId": 12,
      "title": "正念呼吸入门",
      "description": "10分钟基础正念呼吸练习，适合冥想初学者",
      "category": "meditation",
      "subType": "breathing",
      "narrator": "李老师",
      "difficulty": "beginner",
      "duration": 600,
      "coverUrl": "https://...",
      "audioUrl": "https://...",
      "tags": ["呼吸","入门","正念"],
      "playCount": 520,
      "status": 1,
      "sortOrder": 1,
      "createTime": "2026-06-01 10:00:00",
      "updateTime": "2026-06-10 14:00:00",
      "backgroundImages": [
        {"id": 3, "url": "https://...", "sortOrder": 0}
      ]
    }
  ]
}
```

#### 2.2 获取冥想音频详情

```
GET /content/meditation/{id}
```

#### 2.3 新增冥想音频

```
POST /content/meditation/create
```

**请求体：**
```json
{
  "fileId": 12,
  "title": "身体扫描冥想",
  "description": "从头到脚逐步放松的身体扫描练习",
  "coverUrl": "https://...",
  "duration": 1800,
  "subType": "body_scan",
  "narrator": "李老师",
  "difficulty": "beginner",
  "tags": ["身体扫描","放松"],
  "sortOrder": 2,
  "status": 1
}
```

> `category` 由后端自动设为 `meditation`，前端无需传入。

#### 2.4 修改冥想音频

```
POST /content/meditation/update/{id}
```

#### 2.5 删除冥想音频

```
POST /content/meditation/delete/{id}
```

#### 2.6 上架冥想音频

```
POST /content/meditation/online/{id}
```

#### 2.7 下架冥想音频

```
POST /content/meditation/offline/{id}
```

---

## 3. 睡眠内容管理 — `/content/sleep`

> 管理睡眠音频内容（`category` 固定为 `sleep`）+ 睡眠记录查看 + 睡眠日记查看。

### 3.1 睡眠音频管理

#### 3.1.1 查询睡眠音频列表

```
GET /content/sleep/audio/list
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | String | 否 | 关键词搜索 |
| status | Integer | 否 | 0=下架, 1=上架, 空=全部 |

**响应：** 同冥想列表格式，`category` 为 `sleep`。

#### 3.1.2 获取睡眠音频详情

```
GET /content/sleep/audio/{id}
```

#### 3.1.3 新增睡眠音频

```
POST /content/sleep/audio/create
```

**请求体：**
```json
{
  "fileId": 15,
  "title": "睡前故事：星月童话",
  "description": "温馨的睡前童话故事，伴你入眠",
  "coverUrl": "https://...",
  "duration": 1200,
  "subType": "story",
  "narrator": "小鹿姐姐",
  "difficulty": "beginner",
  "tags": ["故事","童话","睡前"],
  "sortOrder": 1,
  "status": 1
}
```

> `category` 由后端自动设为 `sleep`。

#### 3.1.4 修改睡眠音频

```
POST /content/sleep/audio/update/{id}
```

#### 3.1.5 删除睡眠音频

```
POST /content/sleep/audio/delete/{id}
```

#### 3.1.6 上架睡眠音频

```
POST /content/sleep/audio/online/{id}
```

#### 3.1.7 下架睡眠音频

```
POST /content/sleep/audio/offline/{id}
```

---

### 3.2 睡眠记录查看

#### 3.2.1 查询睡眠记录列表

```
GET /content/sleep/record/list
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 否 | 按用户筛选 |
| beginTime | String | 否 | 开始时间 `yyyy-MM-dd` |
| endTime | String | 否 | 结束时间 `yyyy-MM-dd` |

**响应：**
```json
{
  "code": 200,
  "rows": [
    {
      "id": 1,
      "userId": 1001,
      "startTime": "2026-06-14 22:30:00",
      "endTime": "2026-06-15 07:00:00",
      "duration": 510,
      "sleepScore": 85,
      "deepSleepMinutes": 120,
      "lightSleepMinutes": 300,
      "remSleepMinutes": 90,
      "interruptCount": 2,
      "snoringCount": 0,
      "audioMixId": 1,
      "createTime": "2026-06-15 07:00:00",
      "updateTime": "2026-06-15 07:00:00"
    }
  ]
}
```

> - `duration` 总睡眠时长（分钟）
> - `deepSleepMinutes` / `lightSleepMinutes` / `remSleepMinutes` 深/浅/REM 各阶段时长
> - `interruptCount` 夜间醒来次数 / `snoringCount` 打鼾次数
> - `audioMixId` 关联的混音预设 ID

#### 3.2.2 获取睡眠记录详情

```
GET /content/sleep/record/{id}
```

---

### 3.3 睡眠日记查看

#### 3.3.1 查询睡眠日记列表

```
GET /content/sleep/diary/list
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 否 | 按用户筛选 |

**响应：**
```json
{
  "code": 200,
  "rows": [
    {
      "id": 1,
      "userId": 1001,
      "date": "2026-06-14",
      "bedtimeActivity": "阅读",
      "caffeineIntake": 0,
      "exercise": 30,
      "emotion": "放松",
      "note": "今天睡得不错",
      "createTime": "2026-06-14 22:00:00"
    }
  ]
}
```

> - `bedtimeActivity` 睡前活动
> - `caffeineIntake` 咖啡因摄入量（mg）
> - `exercise` 当日运动时长（分钟）
> - `emotion` 睡前情绪

#### 3.3.2 获取睡眠日记详情

```
GET /content/sleep/diary/{id}
```

---

## 4. APP 端接口说明

> APP 端通过 `/api/app/audio` 等路径获取展示用的音频内容，不在本文档范围内，此处仅列出供参考。

| 路径 | 说明 |
|------|------|
| `GET /api/app/audio/list?category=...` | 按分类获取音频列表 |
| `GET /api/app/audio/{id}` | 音频详情（含 backgroundImages） |
| `GET /api/app/audio/search?keyword=...` | 搜索音频 |
| `GET /api/app/audio/history` | 播放历史 |
| `GET /api/app/home` | 首页推荐数据（含音频、冥想、混音等） |

---

## 5. 权限标识对照

| 模块 | 接口 | 权限标识 |
|------|------|---------|
| 音频库 | 列表 | `content:audio:list` |
| 音频库 | 详情 | `content:audio:query` |
| 音频库 | 新增 | `content:audio:create` |
| 音频库 | 修改 | `content:audio:update` |
| 音频库 | 删除 | `content:audio:delete` |
| 音频库 | 上架/下架 | `content:audio:update` |
| 混音组合 | 列表 | `content:audio:mix:list` |
| 混音组合 | 详情 | `content:audio:mix:query` |
| 混音组合 | 新增 | `content:audio:mix:create` |
| 混音组合 | 修改 | `content:audio:mix:update` |
| 混音组合 | 删除 | `content:audio:mix:delete` |
| 冥想 | 列表 | `content:meditation:list` |
| 冥想 | 详情 | `content:meditation:query` |
| 冥想 | 新增 | `content:meditation:create` |
| 冥想 | 修改 | `content:meditation:update` |
| 冥想 | 删除 | `content:meditation:delete` |
| 冥想 | 上架/下架 | `content:meditation:update` |
| 睡眠音频 | 列表 | `content:sleep:list` |
| 睡眠音频 | 详情 | `content:sleep:query` |
| 睡眠音频 | 新增 | `content:sleep:create` |
| 睡眠音频 | 修改 | `content:sleep:update` |
| 睡眠音频 | 删除 | `content:sleep:delete` |
| 睡眠音频 | 上架/下架 | `content:sleep:update` |
| 睡眠记录 | 列表 | `content:sleep:record:list` |
| 睡眠记录 | 详情 | `content:sleep:record:query` |
| 睡眠日记 | 列表 | `content:sleep:diary:list` |
| 睡眠日记 | 详情 | `content:sleep:diary:query` |

---

## 6. 分类与难度约定

### `category` + `sub_type`

| 内容域 | category | sub_type 可选值 |
|--------|----------|----------------|
| 冥想 | `meditation` | `guided` 引导 / `breathing` 呼吸 / `body_scan` 身体扫描 / `mindfulness` 正念 |
| 睡眠 | `sleep` | `story` 故事 / `asmr` / `meditation` 冥想 / `white_noise` 白噪音 |
| 白噪音 | `white_noise` | `nature` 自然 / `city` 城市 / `instrument` 乐器 |
| 专注 | `focus` | `nature` 自然 / `instrument` 乐器 |

### `difficulty` 难度

| 值 | 说明 |
|----|------|
| `beginner` | 初级 |
| `intermediate` | 中级 |
| `advanced` | 高级 |

---

## 7. 表结构

### `audio_item` — 音频内容表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| file_id | bigint | 关联文件记录ID（`sys_file_record`） |
| title | varchar(100) | 音频标题 |
| description | text | 音频描述 |
| cover_url | varchar(500) | 封面图 |
| audio_url | varchar(500) | 音频文件地址（从 `file_id` 自动回填） |
| duration | int | 时长（秒） |
| category | varchar(20) | 分类：`meditation` / `sleep` / `white_noise` / `focus` |
| sub_type | varchar(50) | 子分类 |
| narrator | varchar(100) | 讲述者 |
| difficulty | varchar(20) | 难度 |
| tags | varchar(200) | 标签（JSON数组） |
| play_count | int | 播放次数 |
| sort_order | int | 排序 |
| status | tinyint | 0-下架 1-上架 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| is_deleted | tinyint | 软删除标记 |

### `audio_item_bg` — 音频背景图关联表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| audio_item_id | bigint | 关联音频ID |
| url | varchar(500) | 背景图URL |
| sort_order | int | 排序 |
| create_time | datetime | 创建时间 |

### `audio_mix` — 混音组合表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID |
| name | varchar(100) | 组合名称 |
| description | text | 描述 |
| cover_url | varchar(500) | 封面 |
| audio_ids | varchar(500) | 音频ID列表（JSON数组字符串） |
| is_default | tinyint | 是否默认 |
| sort_order | int | 排序 |
| status | tinyint | 0-禁用 1-启用 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| is_deleted | tinyint | 软删除标记 |

---

## 8. AudioMix 数据示例（来自 SQL 初始化）

| id | name | audio_ids | is_default | status |
|----|------|-----------|:----------:|:------:|
| 1 | 深度睡眠组合 | `[1,4,6]` | 1 | 1 |
| 2 | 专注工作组合 | `[5,6]` | 0 | 1 |
| 3 | 冥想放松组合 | `[2,3,7]` | 0 | 1 |
| 4 | 清晨唤醒组合 | `[2,11]` | 0 | 0 |
