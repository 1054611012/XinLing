package com.xinling.app.service;

import com.xinling.app.domain.entity.MomentComment;
import com.xinling.app.domain.entity.PrivateMessage;
import com.xinling.app.domain.entity.UserFollow;
import com.xinling.app.domain.model.MomentVO;

import java.util.List;

/**
 * 动态社区服务
 */
public interface ICommunityService {

    /**
     * 发布动态
     */
    MomentVO publishMoment(Long userId, String content, String images, String type,
                           String source, Integer isAnonymous, Integer visibility);

    /**
     * 获取动态列表（信息流）
     */
    List<MomentVO> listMoments(Long userId, String type, int page, int size);

    /**
     * 获取动态详情
     */
    MomentVO getMomentDetail(Long id, Long userId);

    /**
     * 获取指定动态的所有评论
     */
    List<MomentComment> getCommentsByMomentId(Long momentId);

    /**
     * 删除动态
     */
    void deleteMoment(Long userId, Long id);

    /**
     * 点赞动态
     */
    void likeMoment(Long userId, Long momentId);

    /**
     * 取消点赞
     */
    void unlikeMoment(Long userId, Long momentId);

    /**
     * 评论动态
     */
    MomentComment commentMoment(Long userId, Long momentId, String content);

    /**
     * 删除评论
     */
    void deleteComment(Long userId, Long commentId);

    /**
     * 分享动态
     */
    void shareMoment(Long userId, Long momentId);

    /**
     * 收藏动态
     */
    void collectMoment(Long userId, Long momentId);

    /**
     * 关注用户
     */
    void followUser(Long followerId, Long followingId);

    /**
     * 取消关注
     */
    void unfollowUser(Long followerId, Long followingId);

    /**
     * 获取粉丝列表
     */
    List<UserFollow> getFollowers(Long userId);

    /**
     * 获取关注列表
     */
    List<UserFollow> getFollowing(Long userId);

    /**
     * 发送私信
     */
    PrivateMessage sendMessage(Long fromUserId, Long toUserId, String content);

    /**
     * 获取私信列表
     */
    List<PrivateMessage> getMessageList(Long userId);
}
