/**
 * Live2D 动画控制器（未来升级）
 *
 * 管理数字人的情绪状态 → Live2D 动作/表情的映射关系
 * 用户可在 Live2D Cubism Editor 中调整这些参数名匹配模型
 *
 * @author SuXia
 * @date 2026/06/01
 */

/** 数字人情绪状态 */
export type AvatarMood = 'idle' | 'listen' | 'think' | 'answer'

/** Live2D 参数映射配置 */
export interface Live2dExpression {
  /** 表情名称（对应 Cubism Editor 中的 Expression） */
  expression?: string
  /** 动作名称（对应 Cubism Editor 中的 Motion） */
  motion?: string
  /** 手动微调参数 */
  parameters?: Record<string, number>
}

/**
 * 情绪 → Live2D 动画映射表
 *
 * 在 Live2D Cubism Editor 中创建对应的 Motion 和 Expression 后，
 * 按此表命名即可自动驱动。
 */
export const MOOD_TO_LIVE2D: Record<AvatarMood, Live2dExpression> = {
  idle: {
    motion: 'Idle',
    expression: 'Normal',
    parameters: {
      ParamBreath: 1.0,       // 呼吸强度
      ParamEyeBlink: 1.0,     // 眨眼
    },
  },
  listen: {
    motion: 'Listen',
    expression: 'Attentive',
    parameters: {
      ParamBodyAngleX: 5,     // 身体前倾
      ParamEyeOpen: 1.2,      // 眼睛睁大（专注）
      ParamMouthForm: -0.3,   // 嘴巴微张
    },
  },
  think: {
    motion: 'Think',
    expression: 'Thinking',
    parameters: {
      ParamEyeLookUp: 0.5,    // 眼睛向上看
      ParamAngleX: 8,         // 歪头
      ParamEyeBallX: 0.3,     // 眼珠转动
    },
  },
  answer: {
    motion: 'Talk',
    expression: 'Happy',
    parameters: {
      ParamMouthOpenY: 0.5,   // 嘴巴张开
      ParamBodyAngleY: 3,     // 身体微微摇晃
      ParamBreath: 1.3,       // 呼吸加快
    },
  },
}

/** 口型同步参数 */
export interface LipSyncParams {
  /** 嘴巴张开程度 (0.0 - 1.0) */
  mouthOpen: number
  /** 嘴唇宽度 */
  mouthWidth: number
  /** 嘴巴形状 (0=闭, 1=圆, 2=微笑, 3=张大) */
  mouthForm: number
}

/**
 * 根据音频频段能量计算口型参数
 * 用于替代 AI 音频分析的简单方案
 */
export function computeLipSyncFromAudio(
  analyser: AnalyserNode | null,
  smoothFactor: number = 0.3
): LipSyncParams {
  if (!analyser) return { mouthOpen: 0, mouthWidth: 0, mouthForm: 0 }

  const data = new Uint8Array(analyser.frequencyBinCount)
  analyser.getByteFrequencyData(data)

  // 低频（0-300Hz 辅音能量）
  const lowFreq = Array.from(data.slice(0, 4)).reduce((a, b) => a + b, 0) / 1024
  // 中频（300-1000Hz 元音能量）
  const midFreq = Array.from(data.slice(4, 12)).reduce((a, b) => a + b, 0) / 2048

  const mouthOpen = Math.min(1, (lowFreq + midFreq) * 0.8)
  const mouthWidth = midFreq * 0.5

  let mouthForm = 0
  if (mouthOpen > 0.7) mouthForm = 3   // 张大
  else if (mouthOpen > 0.4) mouthForm = 1  // 圆
  else if (midFreq > 0.3) mouthForm = 2    // 微笑

  return { mouthOpen, mouthWidth, mouthForm }
}
