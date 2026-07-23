# 音频内容模块 API 文档

> 更新日期：2026-06-23（重构：新增 teacher 全局老师库，meditation_audio 合并老师关联，废弃 meditation_author）

---

## 数据模型关系

```
┌────────────────────────────────────┐
│        sys_file_record             │
│    文件存储（音频/图片统一管理）      │
└──────────┬─────────────────────────┘
           │ fileId
           ▼
┌────────────────────────────────────┐
│          audio_item                │
│     公共素材库（纯文件元数据）       │
│  - fileId / title / audioUrl       │
│  - duration / fileType / narrator  │
│  - tags / playCount / status       │
└──────┬──────────────────────┬──────┘
       │ 素材ID               │ 素材ID
       ▼                      ▼
┌────────────────┐   ┌────────────────┐
│ meditation_audio   │   teacher       │
│ 冥想音频素材    │   │ 全局老师库     │
│ (关联具体老师)  │   │               │
│  · audioItemId  │──→│  · name/avatar │
│  · authorId ────│←──┘               │
│  · sortOrder    │                   │
└───────┬────────┘                   │
        │ meditationId               │
        ▼                            │
┌────────────────────┐               │
│    meditation      │               │
│    冥想内容        │               │
│  · title/cover     │               │
│  · status/sortOrder│               │
└────────────────────┘               │
                                      │
┌──────────────────────────────────┐  │
│      content_bg                 │  │
│  公用背景图表(meditation/sleep/white_noise)
└──────────────────────────────────┘  │
                                       │
┌──────────────────────────────────┐  │
│   meditation_author → 废弃       │  │
│   (功能合并到 meditation_audio   │  │
│     + teacher 表)               │  │
└──────────────────────────────────┘  │


---

## 一、音频素材库 API

基础路径：`/content/audio`

管理后台上传/管理音频视频素材。

### 查询素材列表
```
GET /content/audio/item/list
```
参数：`fileType`(audio/video)、`keyword`、`status`

### 获取素材详情
```
GET /content/audio/item/{id}
```

### 新增素材
```
POST /content/audio/item/create
```

### 修改素材
```
POST /content/audio/item/update/{id}
```

### 删除素材
```
POST /content/audio/item/delete/{id}
```

### 上架/下架
```
POST /content/audio/item/online/{id}
POST /content/audio/item/offline/{id}
```

### AudioItem 字段

```json
{
  "id": 1,
  "fileId": 100,
  "title": "森林鸟鸣.mp3",
  "audioUrl": "https://oss.xxx.com/audio/forest.mp3",
  "duration": 300,
  "fileType": "audio",
  "narrator": "张老师",
  "playCount": 1280,
  "tags": ["自然", "鸟鸣", "森林"],
  "status": 1,
  "sortOrder": 1,
  "createTime": "2026-06-22 10:00:00",
  "updateTime": "2026-06-22 14:00:00"
}
```

---

## 二、冥想内容管理 API

基础路径：`/content/meditation`

**说明：** 冥想内容由多条音频素材有序组成，每条音频可关联具体的老师（来自全局老师库）。

查询详情时返回完整 Meditation 对象，含 `audioItems`（内嵌 `audioItem` + `teacher` 信息）和 `backgroundImages`。

### 2.1 查询列表
```
GET /content/meditation/list
```
参数：`keyword`、`status`

返回冥想基础列表（含关联的音频与老师信息）。

### 2.2 获取详情
```
GET /content/meditation/{id}
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "title": "晨间冥想",
    "description": "一段清新的晨间冥想引导",
    "coverUrl": "https://oss.xxx.com/covers/morning.jpg",
    "status": 1,
    "sortOrder": 1,
    "audioItems": [
      {
        "audioItemId": 1,
        "authorId": 1,
        "sortOrder": 0,
        "audioItem": { "id": 1, "title": "引导语.mp3", "duration": 300, "audioUrl": "..." },
        "teacher":   { "id": 1, "name": "张老师", "avatar": "https://..." }
      },
      {
        "audioItemId": 5,
        "authorId": null,
        "sortOrder": 1,
        "audioItem": { "id": 5, "title": "背景轻音乐.mp3", "duration": 600, "audioUrl": "..." },
        "teacher": null
      }
    ],
    "backgroundImages": [
      { "url": "https://oss.xxx.com/bg/1.jpg", "sortOrder": 0 }
    ]
  }
}
```

> `audioItems[].teacher` = null 表示该音频为纯背景音乐，无指定老师。
> `audioItems[].audioItem` 为音频素材详情（来自公共素材库）。

### 2.3 新增
```
POST /content/meditation/create
```
```json
{
  "title": "晨间冥想",
  "description": "一段清新的晨间冥想引导",
  "coverUrl": "https://oss.xxx.com/covers/morning.jpg",
  "status": 1,
  "sortOrder": 1
}
```

### 2.4 修改
```
POST /content/meditation/update/{id}
```

### 2.5 删除
```
POST /content/meditation/delete/{id}
```
> 级联删除关联的音频、背景图。

### 2.6 上架/下架
```
POST /content/meditation/online/{id}
POST /content/meditation/offline/{id}
```

### 2.7 批量设置音频素材（含老师关联）
```
POST /content/meditation/{id}/audio-items
```
```json
[
  { "audioItemId": 1, "authorId": 1, "sortOrder": 0 },
  { "audioItemId": 5, "authorId": null, "sortOrder": 1 }
]
```

> **全量替换：** 每次调用先删除再插入，请传入完整的音频列表。
>
> **字段说明：**
> - `audioItemId` — 音频素材ID（来自 `/content/audio/item/list`），**必填**
> - `authorId` — 全局老师ID（来自 `/content/teacher/list`），**可选**；传 null 表示该音频为纯背景音乐
> - `sortOrder` — 排序号，决定播放顺序
>
> **旧接口兼容：** `POST /{id}/audio/batch` 保留但标记为废弃，内部委托给本接口。

### 2.8 批量设置背景图
```
POST /content/meditation/{id}/bg/batch
```
```json
["https://oss.xxx.com/bg/1.jpg", "https://oss.xxx.com/bg/2.jpg"]
```

---

## 新增：老师管理 API

基础路径：`/content/teacher`

> 全局老师库，老师可跨冥想复用。创建冥想音频时通过 `authorId` 引用。

### 查询老师列表
```
GET /content/teacher/list
```
参数：`keyword`（按姓名搜索）

### 获取老师详情
```
GET /content/teacher/{id}
```

### 新增老师
```
POST /content/teacher/create
```
```json
{
  "name": "张老师",
  "avatar": "https://oss.xxx.com/avatar/zhang.jpg",
  "sortOrder": 1
}
```

### 修改老师
```
POST /content/teacher/update/{id}
```

### 删除老师
```
POST /content/teacher/delete/{id}
```

---

## 三、睡眠内容管理 API

基础路径：`/content/sleep`

### 3.1 查询列表
```
GET /content/sleep/list
```
参数：`keyword`、`status`

返回完整 SleepItem 对象（含 `audioItem` / `backgroundImages`）。

### 3.2 获取详情
```
GET /content/sleep/{id}
```

### 3.3 新增
```
POST /content/sleep/create
```
```json
{
  "title": "雨声助眠",
  "description": "轻柔的雨声伴你入睡",
  "coverUrl": "https://oss.xxx.com/covers/rain.jpg",
  "audioItemId": 5,
  "status": 1,
  "sortOrder": 1
}
```

### 3.4 修改
```
POST /content/sleep/update/{id}
```

### 3.5 删除
```
POST /content/sleep/delete/{id}
```

### 3.6 上架/下架
```
POST /content/sleep/online/{id}
POST /content/sleep/offline/{id}
```

### 3.7 批量设置背景图
```
POST /content/sleep/{id}/bg/batch
```
```json
["https://oss.xxx.com/bg/sleep1.jpg", "https://oss.xxx.com/bg/sleep2.jpg"]
```

### 3.8 睡眠记录（查看）
```
GET /content/sleep/record/list?userId=&beginTime=&endTime=
GET /content/sleep/record/{id}
```

### 3.9 睡眠日记（查看）
```
GET /content/sleep/diary/list?userId=
GET /content/sleep/diary/{id}
```

---

## 四、白噪音内容管理 API

基础路径：`/content/white-noise`

### 4.1 查询列表
```
GET /content/white-noise/list
```
参数：`keyword`、`status`

### 4.2 获取详情
```
GET /content/white-noise/{id}
```

### 4.3 新增
```
POST /content/white-noise/create
```
```json
{
  "title": "篝火声",
  "description": "温暖篝火燃烧的白噪音",
  "coverUrl": "https://oss.xxx.com/covers/fire.jpg",
  "audioItemId": 10,
  "status": 1,
  "sortOrder": 1
}
```

### 4.4 修改
```
POST /content/white-noise/update/{id}
```

### 4.5 删除
```
POST /content/white-noise/delete/{id}
```

### 4.6 上架/下架
```
POST /content/white-noise/online/{id}
POST /content/white-noise/offline/{id}
```

### 4.7 批量设置背景图
```
POST /content/white-noise/{id}/bg/batch
```
```json
["https://oss.xxx.com/bg/fire1.jpg", "https://oss.xxx.com/bg/fire2.jpg"]
```

---

## 五、权限矩阵

| 模块 | 权限标识 |
|------|---------|
| 素材库-列表 | `content:audio:list` |
| 素材库-查询 | `content:audio:query` |
| 素材库-新增 | `content:audio:create` |
| 素材库-修改 | `content:audio:update` |
| 素材库-删除 | `content:audio:delete` |
| 素材库-混音 | `content:audio:mix:list/query/create/update/delete` |
| 冥想-列表 | `content:meditation:list` |
| 冥想-查询 | `content:meditation:query` |
| 冥想-新增 | `content:meditation:create` |
| 冥想-修改 | `content:meditation:update` |
| 冥想-删除 | `content:meditation:delete` |
| 老师-列表 | `content:teacher:list` |
| 老师-新增 | `content:teacher:create` |
| 老师-修改 | `content:teacher:update` |
| 老师-删除 | `content:teacher:delete` |
| 睡眠-列表 | `content:sleep:list` |
| 睡眠-查询 | `content:sleep:query` |
| 睡眠-新增 | `content:sleep:create` |
| 睡眠-修改 | `content:sleep:update` |
| 睡眠-删除 | `content:sleep:delete` |
| 睡眠-记录 | `content:sleep:record:list/query` |
| 睡眠-日记 | `content:sleep:diary:list/query` |
| 白噪音-列表 | `content:white-noise:list` |
| 白噪音-查询 | `content:white-noise:query` |
| 白噪音-新增 | `content:white-noise:create` |
| 白噪音-修改 | `content:white-noise:update` |
| 白噪音-删除 | `content:white-noise:delete` |

---

## 六、前端使用指引

### 创建内容流程

```
1. 文件管理 → /file/record/upload → 上传音频文件 → 获得 fileId
2. 素材库 → POST /content/audio/item/create → 传入 fileId → 获得 audioItem.id
3. 对应内容模块 → POST /content/{module}/create → 传入 audioItemId
```

### 各模块需要展示的字段

| 模块 | 封面 | 背景图 | 老师 | 音频 |
|------|------|--------|------|------|
| 素材库列表 | ❌ | ❌ | ❌ | 显示标题/时长/文件类型 |
| 冥想列表 | ✅ coverUrl | ❌ 列表不展示 | ✅ 通过 audioItems[].teacher 展示 | 多条 |
| 睡眠列表 | ✅ coverUrl | ❌ 列表不展示 | ❌ | 单条 |
| 白噪音列表 | ✅ coverUrl | ❌ 列表不展示 | ❌ | 单条 |
| 冥想详情 | ✅ | ✅ backgroundImages | ✅ audioItems[].teacher | ✅ audioItems |
| 睡眠详情 | ✅ | ✅ backgroundImages | ❌ | ✅ audioItem |
| 白噪音详情 | ✅ | ✅ backgroundImages | ❌ | ✅ audioItem |

### 文件筛选

素材库支持按 `fileType` 筛选：
```
GET /content/audio/item/list?fileType=audio → 只看音频
GET /content/audio/item/list?fileType=video → 只看视频
```

### APP 端查询素材列表

```
GET /api/app/audio/list?fileType=audio
GET /api/app/audio/search?keyword=
```

