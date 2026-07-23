/**
 * Live2D 模型加载器（未来升级）
 *
 * 功能：加载 .model3.json 文件及纹理、moc3 资源
 * 使用 pixi-live2d-display 封装 Cubism SDK
 *
 * 使用方法：
 *   yarn add pixi-live2d-display pixi.js@^7
 *
 * 需要：
 *   1. 将 Live2D 模型文件放入 public/live2d/ 目录
 *   2. 模型结构：
 *      public/live2d/fox/
 *        ├── fox.model3.json
 *        ├── fox.moc3
 *        ├── texture_00.png
 *        └── ...
 *
 * @author SuXia
 * @date 2026/06/01
 */

// TODO: 当准备好 Live2D 模型后，取消以下注释并使用

/*
import * as PIXI from 'pixi.js'
import { Live2DModel } from 'pixi-live2d-display'

// 注册 Cubism 核心库
window.Live2DCubismCore = window.Live2DCubismCore || await import('@/services/live2d/CubismCore')

export async function loadLive2dModel(modelPath: string): Promise<Live2DModel> {
  // 示例: loadLive2dModel('/live2d/fox/fox.model3.json')
  const model = await Live2DModel.from(modelPath)
  return model
}

export function setupModelInteraction(model: Live2DModel) {
  // 自动呼吸
  model.internalModel.motionManager.startRandomMotion('Idle')
}
*/

export {}
