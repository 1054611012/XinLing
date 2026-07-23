package com.xinling.ai.service.tts;

import lombok.Getter;

/**
 * Edge-TTS 支持的中文语音
 * 所有语音免费使用，无需API Key
 *
 * @author SuXia
 * @date 2026/06/01
 */
@Getter
public enum TtsVoice {

    /** 中文女声 - 晓晓（温柔亲切，推荐） */
    XIAOXIAO("zh-CN-XiaoxiaoNeural", "晓晓（女）", "温柔亲切"),
    /** 中文女声 - 晓萱（活泼可爱） */
    XIAOXUAN("zh-CN-XiaoxuanNeural", "晓萱（女）", "活泼可爱"),
    /** 中文男声 - 云希（阳光） */
    YUNXI("zh-CN-YunxiNeural", "云希（男）", "阳光开朗"),
    /** 中文男声 - 云扬（沉稳） */
    YUNYANG("zh-CN-YunyangNeural", "云扬（男）", "沉稳专业"),
    /** 中文女声 - 晓伊（情感丰富，推荐） */
    XIAOYI("zh-CN-XiaoyiNeural", "晓伊（女）", "情感丰富"),
    /** 粤语女声 */
    XIAO_XIAO_HK("zh-HK-HiuGaaiNeural", "晓佳（粤语女）", "粤语"),
    /** 中英混合 - 晓梦 */
    XIAOMENG("zh-CN-XiaomengNeural", "晓梦（女）", "可爱活泼");

    /** Edge-TTS 语音名称 */
    private final String voiceName;
    /** 显示名称 */
    private final String displayName;
    /** 风格描述 */
    private final String style;

    TtsVoice(String voiceName, String displayName, String style) {
        this.voiceName = voiceName;
        this.displayName = displayName;
        this.style = style;
    }

    /**
     * 获取默认语音（温柔女声）
     */
    public static TtsVoice getDefault() {
        return XIAOXIAO;
    }
}