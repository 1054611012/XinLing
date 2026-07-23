# AI 数字人升级指南：从 CSS 狐狸 → Kling 视频 → Live2D 模型

## 当前架构（已实现）

```
┌─────────────┐     SSE/HTTP      ┌──────────────────┐
│  前端 Vue3   │ ◄──────────────► │  后端 Spring Boot  │
│  ChatPage    │                  │                   │
│  ┌─────────┐ │                  │  ┌─────────────┐  │
│  │VideoAvatr│ │                  │  │XinLingAiSvc │  │
│  │.vue      │ │                  │  │(Qwen2.5 Oll)│  │
│  │ Kling    │ │     POST/TTS     │  └─────────────┘  │
│  │ 视频角色  │ │ ◄─────────────► │  ┌─────────────┐  │
│  │ +TTS播报 │ │                  │  │EdgeTtsSvc   │  │
│  └─────────┘ │                  │  │(Python CLI)  │  │
└─────────────┘                  │  └─────────────┘  │
                                  └──────────────────┘
```

## 升级路径三种方案对比

| 特性 | 当前（已实现） | 视频增强版 | Live2D专业版（未来） |
|------|---------------|-----------|-------------------|
| 角色形象 | Kling视频循环 | Kling视频+透明通道 | 可交互2D模型 |
| 口型同步 | CSS椭圆动画覆盖 | Wav2Lip GPU同步 | Live2D参数驱动 |
| 语音 | Edge-TTS (免费) | Edge-TTS / 阿里云TTS | 阿里云TTS |
| 动作 | 4段视频切换 | 视频+CSS状态叠加 | 物理引擎自然过渡 |
| 交互感 | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| 开发成本 | ✅ 已实现 | 需GPU+AI模型 | 需美术+模型师 |

---

## 升级方案一：视频增强版（推荐下一步）

在现有 Kling 视频基础上增强：

### 1.1 背景抠除（获得透明通道）

```bash
# 使用 rembg（Python）分离角色背景
pip install rembg
rembg i input.mp4 output.mp4

# 或使用 FFmpeg 手动绿幕（如果原视频有绿幕）
ffmpeg -i input.mp4 -filter_complex "chromakey=0x00FF00:0.1:0.2" -c:v png -an output.webm
```

### 1.2 Wav2Lip 口型同步（需要GPU）

```bash
# 克隆 Wav2Lip 项目
git clone https://github.com/Rudrabha/Wav2Lip
cd Wav2Lip

# 安装依赖
pip install -r requirements.txt

# 下载预训练模型
wget 'https://iiitaphyd-my.sharepoint.com/personal/radrabha_m_research_iiit_ac_in/_layouts/15/download.aspx?share=EdjI7bZlgApMqsVoEUUXpLsBxqXbn5z8VTmoxp55YNDcIA' -O 'wav2lip_gan.pth'

# 运行口型同步
python inference.py \
    --checkpoint_path wav2lip_gan.pth \
    --face "kling_idle.mp4" \
    --audio "response.mp3" \
    --outfile "output.mp4"
```

### 1.3 后端集成

```java
// 在 TtsService 中添加 Wav2Lip 调用
@Service
public class Wav2LipServiceImpl {
    public File generateTalkingVideo(File faceVideo, String text, TtsVoice voice) {
        // 1. Edge-TTS 生成语音
        File audio = ttsService.synthesize(text, voice);
        // 2. Wav2Lip 口型同步
        ProcessBuilder pb = new ProcessBuilder(
            "python3", "wav2lip/inference.py",
            "--face", faceVideo.getPath(),
            "--audio", audio.getPath(),
            "--outfile", outputVideo.getPath()
        );
        // ...
        return outputVideo;
    }
}
```

---

## 升级方案二：Live2D 模型（终极方案）

### 2.1 从 Kling 视频提取角色设计图

**工具：**
- **截图工具**：从 Kling 视频中截取角色的正面全身照（最佳帧）
- **Photoshop / Clip Studio Paint**：将角色拆分为 Live2D 部件

**拆分部件清单：**
```
角色目录: /live2d/fox/
├── fox.model3.json       ← Live2D 模型配置文件
├── fox.moc3              ← 模型数据（由 Cubism Editor 导出）
├── fox.physics3          ← 物理效果配置
├── fox.cdi3.json         ← 显示信息
├── fox.userdata3.json    ← 用户数据
├── textures/             ← 纹理图集
│   ├── texture_00.png    ← 身体纹理
│   └── texture_01.png    ← 表情纹理
├── motions/              ← 动作文件（可选）
│   ├── Idle.motion3.json
│   ├── Listen.motion3.json
│   ├── Think.motion3.json
│   └── Talk.motion3.json
└── expressions/          ← 表情文件（可选）
    ├── Normal.exp3.json
    ├── Attentive.exp3.json
    ├── Thinking.exp3.json
    └── Happy.exp3.json
```

### 2.2 Live2D Cubism Editor 操作步骤

1. **导入设计图** (File → Import → Import Image)
2. **拆分部件**：
   - 身体 × 1
   - 头部 × 1
   - 头发（前/后）× 2
   - 耳朵（左/右）× 2
   - 眼睛（白/黑/高光）× 6
   - 眉毛（左/右）× 2
   - 嘴巴（张开/闭合/圆/微笑）× 3-5
   - 尾巴 × 1
   - 爪子（左/右）× 2
3. **设置变形器**：
   - 头部旋转：`ParamAngleX`, `ParamAngleY`, `ParamAngleZ`
   - 身体倾斜：`ParamBodyAngleX`, `ParamBodyAngleY`
   - 眼睛：`ParamEyeOpen`, `ParamEyeBallX`, `ParamEyeBallY`
   - 嘴巴：`ParamMouthOpenY`, `ParamMouthForm`
   - 眉毛：`ParamBrowAngleX`, `ParamBrowAngleY`
   - 呼吸：`ParamBreath`
4. **创建动作** (Animation)：
   - `Idle`：轻柔呼吸 + 偶尔眨眼（持续循环）
   - `Listen`：身体前倾 + 耳朵转向
   - `Think`：眼睛向上 + 歪头
   - `Talk`：配合口型 + 点头
5. **导出** (File → Export for Runtime → model3.json)

### 2.3 前端集成

```bash
# 1. 安装依赖
cd mobile-web
yarn add pixi.js@^7 pixi-live2d-display

# 2. 将模型文件放入 public/live2d/fox/
# 3. 在 ChatPage.vue 中替换组件：
```

```vue
<!-- ChatPage.vue -->
<template>
  <!-- 替换 VideoAvatar 为 Live2dAvatar -->
  <Live2dAvatar
    :mood="mood"
    :speaking="speaking"
    :audio-url="currentAudioUrl"
    @audio-end="onAudioEnd"
  />
</template>

<script setup>
// 移除 VideoAvatar 导入，添加：
import Live2dAvatar from '@/components/Live2dAvatar.vue'
</script>
```

### 2.4 口型同步建议

| 方法 | 延迟 | 精度 | 实现难度 |
|------|------|------|---------|
| 音频音量驱动 | 实时 | 中 | 低 |
| 音频频段分析 | 实时 | 高 | 中 |
| Viseme 序列（阿里云TTS） | ~500ms | 极高 | 高 |
| Wav2Lip（视频合成） | ~3s | 极高 | 高 |

**推荐**：先用「音频音量驱动」（已内置于 Live2dAvatar 组件），后续升级到「阿里云 TTS Viseme」。

---

## 实施路线图

```
Week 1: 部署当前视频方案（现在）
  ├── Edge-TTS 安装配置 (已完成)
  ├── Kling 视频切片 (已完成)
  └── ChatPage 重构 (已完成)

Week 2-3: 视频增强
  ├── 角色背景抠除（rembg）
  ├── 口型同步（Wav2Lip）
  └── 后端集成 Wav2Lip

Week 4-8: Live2D 模型制作
  ├── 角色设计图拆分（Photoshop）
  ├── Cubism Editor 绑定（需采购）
  ├── 动作/表情制作
  └── 模型导出

Week 9: Live2D 前端集成
  ├── 安装 pixi.js + pixi-live2d-display
  ├── 配置 Live2dAvatar 组件
  └── 集成 TTS + 口型驱动
```

---

## 本项目中已创建的数字人相关文件

### 后端
- `xinling-ai/.../tts/TtsService.java` - TTS服务接口 ✅
- `xinling-ai/.../tts/EdgeTtsServiceImpl.java` - Edge-TTS实现 ✅
- `xinling-ai/.../tts/TtsVoice.java` - 语音角色枚举 ✅
- `xinling-admin/.../AiController.java` - 添加TTS端点 ✅
- `xinling-app/.../AppChatController.java` - 聊天自动TTS ✅

### 前端
- `mobile-web/src/components/VideoAvatar.vue` - Kling视频角色组件 ✅
- `mobile-web/src/views/chat/ChatPage.vue` - 重构聊天页面 ✅
- `mobile-web/src/components/Live2dAvatar.vue` - Live2D角色组件（待模型） ✅
- `mobile-web/src/services/live2d/Live2dLoader.ts` - Live2D加载器 ✅
- `mobile-web/src/services/live2d/Live2dAnimationController.ts` - 动画控制器 ✅
- `mobile-web/public/avatar/` - 4个状态MP4视频片断 ✅

### 依赖
- `edge-tts` (Python) - 免费语音合成 ✅
- `ffmpeg` (Homebrew) - 视频处理 ✅
