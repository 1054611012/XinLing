package com.xinling.app.service.impl;

import com.xinling.app.domain.entity.*;
import com.xinling.app.mapper.*;
import com.xinling.app.domain.model.MomentVO;
import com.xinling.app.service.ICommunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态社区服务实现
 */
@Service
public class CommunityServiceImpl implements ICommunityService {

    private static final Logger log = LoggerFactory.getLogger(CommunityServiceImpl.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final MomentMapper momentMapper;
    private final MomentLikeMapper momentLikeMapper;
    private final MomentCommentMapper momentCommentMapper;
    private final UserFollowMapper userFollowMapper;
    private final PrivateMessageMapper privateMessageMapper;
    private final AppUserMapper appUserMapper;

    public CommunityServiceImpl(MomentMapper momentMapper,
                                MomentLikeMapper momentLikeMapper,
                                MomentCommentMapper momentCommentMapper,
                                UserFollowMapper userFollowMapper,
                                PrivateMessageMapper privateMessageMapper,
                                AppUserMapper appUserMapper) {
        this.momentMapper = momentMapper;
        this.momentLikeMapper = momentLikeMapper;
        this.momentCommentMapper = momentCommentMapper;
        this.userFollowMapper = userFollowMapper;
        this.privateMessageMapper = privateMessageMapper;
        this.appUserMapper = appUserMapper;
    }

    @Override
    @Transactional
    public MomentVO publishMoment(Long userId, String content, String images, String type,
                                   String source, Integer isAnonymous, Integer visibility) {
        Moment moment = new Moment();
        moment.setUserId(userId);
        moment.setContent(content);
        moment.setImages(images);
        moment.setType(type != null ? type : "manual");
        moment.setSource(source);
        moment.setSourceId(null);
        moment.setIsAnonymous(isAnonymous != null ? isAnonymous : 0);
        moment.setVisibility(visibility != null ? visibility : 0);
        moment.setLikeCount(0);
        moment.setCommentCount(0);
        moment.setShareCount(0);
        momentMapper.insert(moment);
        return toMomentVO(moment, userId);
    }

    @Override
    public List<MomentVO> listMoments(Long userId, String type, int page, int size) {
        int offset = (page - 1) * size;
        List<Moment> moments = momentMapper.selectFeed(type, userId, offset, size);
        List<MomentVO> result = new ArrayList<>();
        for (Moment m : moments) {
            result.add(toMomentVO(m, userId));
        }
        return result;
    }

    @Override
    public MomentVO getMomentDetail(Long id, Long userId) {
        Moment moment = momentMapper.selectById(id);
        if (moment == null) {
            throw new RuntimeException("动态不存在");
        }
        return toMomentVO(moment, userId);
    }

    @Override
    public List<MomentComment> getCommentsByMomentId(Long momentId) {
        return momentCommentMapper.selectByMomentId(momentId);
    }

    @Override
    @Transactional
    public void deleteMoment(Long userId, Long id) {
        Moment moment = momentMapper.selectById(id);
        if (moment == null) {
            throw new RuntimeException("动态不存在");
        }
        if (!moment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该动态");
        }
        momentMapper.deleteById(id);
        log.info("用户删除动态: userId={}, momentId={}", userId, id);
    }

    @Override
    @Transactional
    public void likeMoment(Long userId, Long momentId) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("动态不存在");
        }
        MomentLike existing = momentLikeMapper.selectByUserAndMoment(userId, momentId);
        if (existing != null) {
            throw new RuntimeException("已点赞过该动态");
        }
        MomentLike like = new MomentLike();
        like.setUserId(userId);
        like.setMomentId(momentId);
        momentLikeMapper.insert(like);
        momentMapper.incrementLikeCount(momentId);
    }

    @Override
    @Transactional
    public void unlikeMoment(Long userId, Long momentId) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("动态不存在");
        }
        MomentLike existing = momentLikeMapper.selectByUserAndMoment(userId, momentId);
        if (existing == null) {
            throw new RuntimeException("尚未点赞");
        }
        momentLikeMapper.deleteByUserAndMoment(userId, momentId);
        momentMapper.decrementLikeCount(momentId);
    }

    @Override
    @Transactional
    public MomentComment commentMoment(Long userId, Long momentId, String content) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("动态不存在");
        }
        MomentComment comment = new MomentComment();
        comment.setUserId(userId);
        comment.setMomentId(momentId);
        comment.setParentId(0L);
        comment.setContent(content);
        comment.setLikeCount(0);
        momentCommentMapper.insert(comment);
        momentMapper.incrementCommentCount(momentId);
        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        MomentComment comment = momentCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        // 只有评论作者或动态作者可以删除
        Moment moment = momentMapper.selectById(comment.getMomentId());
        if (!comment.getUserId().equals(userId) && (moment == null || !moment.getUserId().equals(userId))) {
            throw new RuntimeException("无权删除该评论");
        }
        momentCommentMapper.deleteById(commentId);
        momentMapper.decrementCommentCount(comment.getMomentId());
    }

    @Override
    @Transactional
    public void shareMoment(Long userId, Long momentId) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("动态不存在");
        }
        momentMapper.incrementShareCount(momentId);
        log.info("用户分享动态: userId={}, momentId={}", userId, momentId);
    }

    @Override
    @Transactional
    public void collectMoment(Long userId, Long momentId) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("动态不存在");
        }
        // 使用点赞表实现收藏（可扩展为独立收藏表）
        MomentLike existing = momentLikeMapper.selectByUserAndMoment(userId, momentId);
        if (existing == null) {
            MomentLike like = new MomentLike();
            like.setUserId(userId);
            like.setMomentId(momentId);
            momentLikeMapper.insert(like);
            momentMapper.incrementLikeCount(momentId);
        }
        log.info("用户收藏动态: userId={}, momentId={}", userId, momentId);
    }

    @Override
    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }
        UserFollow existing = userFollowMapper.selectByFollowerAndFollowing(followerId, followingId);
        if (existing != null) {
            throw new RuntimeException("已关注该用户");
        }
        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        userFollowMapper.insert(follow);
        log.info("用户关注: followerId={}, followingId={}", followerId, followingId);
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        UserFollow existing = userFollowMapper.selectByFollowerAndFollowing(followerId, followingId);
        if (existing == null) {
            throw new RuntimeException("尚未关注该用户");
        }
        userFollowMapper.deleteByFollowerAndFollowing(followerId, followingId);
    }

    @Override
    public List<UserFollow> getFollowers(Long userId) {
        return userFollowMapper.selectFollowers(userId);
    }

    @Override
    public List<UserFollow> getFollowing(Long userId) {
        return userFollowMapper.selectFollowing(userId);
    }

    @Override
    @Transactional
    public PrivateMessage sendMessage(Long fromUserId, Long toUserId, String content) {
        if (fromUserId.equals(toUserId)) {
            throw new RuntimeException("不能给自己发消息");
        }
        PrivateMessage message = new PrivateMessage();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setContent(content);
        message.setIsRead(0);
        privateMessageMapper.insert(message);
        log.info("用户发送私信: fromUserId={}, toUserId={}", fromUserId, toUserId);
        return message;
    }

    @Override
    public List<PrivateMessage> getMessageList(Long userId) {
        return privateMessageMapper.selectByUserId(userId);
    }

    // ========== 私有方法 ==========

    private MomentVO toMomentVO(Moment moment, Long currentUserId) {
        MomentVO vo = new MomentVO();
        vo.setId(moment.getId());
        vo.setUserId(moment.getUserId());
        vo.setContent(moment.getContent());
        vo.setImages(parseImages(moment.getImages()));
        vo.setType(moment.getType());
        vo.setSource(moment.getSource());
        vo.setSourceId(moment.getSourceId());
        vo.setIsAnonymous(moment.getIsAnonymous());
        vo.setVisibility(moment.getVisibility());
        vo.setLikeCount(moment.getLikeCount());
        vo.setCommentCount(moment.getCommentCount());
        vo.setShareCount(moment.getShareCount());
        vo.setCreateTime(moment.getCreateTime());

        // 用户信息（非匿名时展示）
        if (moment.getIsAnonymous() == null || moment.getIsAnonymous() == 0) {
            AppUser user = appUserMapper.selectById(moment.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
        }

        // 当前登录用户是否已点赞
        if (currentUserId != null) {
            MomentLike like = momentLikeMapper.selectByUserAndMoment(currentUserId, moment.getId());
            vo.setIsLiked(like != null);
        } else {
            vo.setIsLiked(false);
        }

        return vo;
    }

    private List<String> parseImages(String imagesJson) {
        if (imagesJson == null || imagesJson.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(imagesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
