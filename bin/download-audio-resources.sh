#!/bin/bash
# =====================================================
# 下载白噪音音频和场景素材
#
# 音频来源: Internet Archive (CC0 / Public Domain)
#   "Relaxing Sounds" Collection
#   https://archive.org/details/relaxingsounds
#
# 图片来源: Unsplash (免费商用)
# =====================================================
#
# 使用方法:
#   bash bin/download-audio-resources.sh
#
# =====================================================

set -e

BASE_DIR="/Volumes/Suxia/IdeaProjects/XinLing"
AUDIO_DIR="$BASE_DIR/upload/audio/white-noise"
COVER_DIR="$BASE_DIR/upload/audio/covers"

mkdir -p "$AUDIO_DIR" "$COVER_DIR"

echo "========================================"
echo "  心聆 - 白噪音资源下载"
echo "========================================"
echo ""

# =====================================================
# 1. 下载音频文件 (Internet Archive CDN)
# =====================================================
echo "[1/2] 下载音频文件..."

IA_BASE="https://archive.org/download/relaxingsounds"

download_audio() {
    local filename="$1"
    local output_name="$2"
    local output="$AUDIO_DIR/${output_name}"

    if [ -f "$output" ]; then
        echo "   ✅ $output_name 已存在"
        return 0
    fi

    local url="${IA_BASE}/${filename}"
    echo "   正在下载: ${output_name} ..."

    if curl -sL --retry 3 -o "$output" "$url"; then
        local size=$(du -h "$output" | cut -f1)
        echo "   ✅ ${output_name} (${size})"
    else
        echo "   ⚠ ${output_name} 下载失败，跳过"
        rm -f "$output"
    fi
}

# 雨声 (Rain 1 ~11h)
download_audio "Rain%201%20%28Heavy%29.mp3" "rain.mp3"

# 海浪 (Waves 1 ~10h)
download_audio "Waves%201%20Beach-Sunset%20into%20Night.mp3" "ocean.mp3"

# 森林 (Rainforest with waterfall ~5h)
download_audio "Rainforest%20with%20Spilling%20Waterfall%2C%20Rain%2C%20Thunder%2C%20Nature%20Sounds.mp3" "forest.mp3"

# 夜晚/虫鸣 (Crickets & Frogs ~6h)
download_audio "Crickets%20%26%20Frogs.mp3" "crickets.mp3"

# 篝火 (FIRE 1 ~9h)
download_audio "FIRE%201.mp3" "fire.mp3"

# 微风 (Wind 1)
download_audio "WIND%201.mp3" "wind.mp3"

# 溪水 (Waterfalls ~9h)
download_audio "Falls%201.mp3" "stream.mp3"

# 钢琴/轻音乐来源 (使用Internet Archive的其他资源)
# 用white noise作为meditation的替代
download_audio "White%20Noise%201.m4a" "meditation.mp3"

echo ""
echo "已下载的音频文件:"
ls -lh "$AUDIO_DIR/" 2>/dev/null
echo ""

# =====================================================
# 2. 下载场景封面图片 (Unsplash CC0)
# =====================================================
echo "[2/2] 下载场景封面图片..."

UNSPLASH_BASE="https://images.unsplash.com"

download_cover() {
    local name="$1"
    local photo_id="$2"
    local output="$COVER_DIR/${name}.jpg"

    if [ -f "$output" ]; then
        echo "   ✅ $name 已存在"
        return 0
    fi

    echo "   正在下载: ${name} ..."
    if curl -sL -o "$output" "${UNSPLASH_BASE}/photo-${photo_id}?w=800&q=80"; then
        echo "   ✅ $name"
    else
        echo "   ⚠ $name 下载失败"
    fi
}

# 场景图片（只下载未存在的）
download_cover "rain"       "1501691223387-dd0500403074"
download_cover "forest"     "1511497584788-876760111969"
download_cover "ocean"      "1505118380757-91f5f5632de0"
download_cover "night"      "1470813740244-df37b8c1edcb"
download_cover "meditation" "1506126613408-eca07ce68773"
download_cover "cafe"       "1501339847302-ac426a4a7cbb"
download_cover "piano"      "1520523839897-bd0b52f945a0"
download_cover "fire"       "1478737270239-2f02b77fc618"
download_cover "stream"     "1470071459604-3b5ec3a7fe05"
download_cover "birds"      "144775287b9c25bafd97ab3"

echo ""
echo "已下载的封面图片:"
ls -lh "$COVER_DIR/" 2>/dev/null

echo ""
echo "========================================"
echo "  下载完成！"
echo ""
echo "  资源目录:"
echo "    音频: $AUDIO_DIR"
echo "    封面: $COVER_DIR"
echo ""
echo "  访问URL:"
echo "    音频: /uploads/audio/white-noise/xxx.mp3"
echo "    封面: /uploads/audio/covers/xxx.jpg"
echo ""
echo "  SQL更新脚本: sql/update_audio_resources.sql"
echo "========================================"
