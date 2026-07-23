package com.xinling.app.domain.model;

import java.util.List;

/**
 * 首页信息VO
 */
public class HomeInfoVO {

    private String greeting;
    private Integer todayFocusMinutes;
    private Integer todaySleepMinutes;
    private Integer continuousDays;
    private List<RecommendedAudio> recommendedAudio;
    private AiTopic aiTopic;
    private List<HotMoment> hotMoments;

    public String getGreeting() { return greeting; }
    public void setGreeting(String greeting) { this.greeting = greeting; }

    public Integer getTodayFocusMinutes() { return todayFocusMinutes; }
    public void setTodayFocusMinutes(Integer todayFocusMinutes) { this.todayFocusMinutes = todayFocusMinutes; }

    public Integer getTodaySleepMinutes() { return todaySleepMinutes; }
    public void setTodaySleepMinutes(Integer todaySleepMinutes) { this.todaySleepMinutes = todaySleepMinutes; }

    public Integer getContinuousDays() { return continuousDays; }
    public void setContinuousDays(Integer continuousDays) { this.continuousDays = continuousDays; }

    public List<RecommendedAudio> getRecommendedAudio() { return recommendedAudio; }
    public void setRecommendedAudio(List<RecommendedAudio> recommendedAudio) { this.recommendedAudio = recommendedAudio; }

    public AiTopic getAiTopic() { return aiTopic; }
    public void setAiTopic(AiTopic aiTopic) { this.aiTopic = aiTopic; }

    public List<HotMoment> getHotMoments() { return hotMoments; }
    public void setHotMoments(List<HotMoment> hotMoments) { this.hotMoments = hotMoments; }

    /**
     * 推荐音频
     */
    public static class RecommendedAudio {
        private Long id;
        private String title;
        private String cover;
        private String author;
        private Integer duration;
        private Integer playCount;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getCover() { return cover; }
        public void setCover(String cover) { this.cover = cover; }

        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }

        public Integer getDuration() { return duration; }
        public void setDuration(Integer duration) { this.duration = duration; }

        public Integer getPlayCount() { return playCount; }
        public void setPlayCount(Integer playCount) { this.playCount = playCount; }
    }

    /**
     * AI话题
     */
    public static class AiTopic {
        private String title;
        private String content;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    /**
     * 热门动态
     */
    public static class HotMoment {
        private Long id;
        private Long userId;
        private String nickname;
        private String avatar;
        private String content;
        private Integer likeCount;
        private Integer commentCount;
        private String createTime;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }

        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Integer getLikeCount() { return likeCount; }
        public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

        public Integer getCommentCount() { return commentCount; }
        public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }
    }
}
