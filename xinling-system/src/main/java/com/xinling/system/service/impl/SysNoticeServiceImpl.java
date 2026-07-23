package com.xinling.system.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xinling.common.constant.CacheConstants;
import com.xinling.common.core.redis.RedisCache;
import com.xinling.system.domain.SysNotice;
import com.xinling.system.mapper.SysNoticeMapper;
import com.xinling.system.service.ISysNoticeService;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 公告 服务层实现
 *
 * @author xinling
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService {

    @Autowired
    private SysNoticeMapper noticeMapper;

    @Autowired
    private RedisCache redisCache; // 使用RedisCache替代RedisTemplate

    private static final String NOTICE_LIST_KEY = CacheConstants.SYS_NOTICE_KEY + "list:limit5"; // Redis键名

    /**
     * 查询公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId) {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice) {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice) {
        int result = noticeMapper.insertNotice(notice);
        if (result > 0) {
            refreshNoticeCache(); // 操作成功后刷新缓存

        }
        return result;
    }

    /**
     * 修改公告
     *
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice) {
        int result = noticeMapper.updateNotice(notice);
        if (result > 0) {
            refreshNoticeCache(); // 操作成功后刷新缓存
        }
        return result;
    }

    /**
     * 删除公告对象
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId) {
        int result = noticeMapper.deleteNoticeById(noticeId);
        if (result > 0) {
            refreshNoticeCache(); // 操作成功后刷新缓存
        }
        return result;
    }

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds) {
        int result = noticeMapper.deleteNoticeByIds(noticeIds);
        if (result > 0) {
            refreshNoticeCache(); // 操作成功后刷新缓存
        }
        return result;
    }

    /**
     * 获取最新的5条公告并更新Redis缓存
     */
    private void refreshNoticeCache() {
        List<SysNotice> notices = noticeMapper.selectNoticeListLimit5();
        redisCache.setCacheObject(NOTICE_LIST_KEY, notices, 60, TimeUnit.MINUTES); // 缓存60分钟
    }

    @Override
    public List<SysNotice> selectNoticeListLimit5() {
        // 先从Redis中获取
        List<SysNotice> cachedNotices = redisCache.getCacheObject(NOTICE_LIST_KEY);
        if (cachedNotices != null) {
            return cachedNotices;
        }

        // 如果Redis中没有，则查询数据库并存入Redis
        List<SysNotice> notices = noticeMapper.selectNoticeListLimit5();
        redisCache.setCacheObject(NOTICE_LIST_KEY, notices, 60, TimeUnit.MINUTES);
        return notices;
    }


    @Override
    public Mono<List<SysNotice>> getTop5Notices() {
        return Mono.fromCallable(() -> noticeMapper.selectNoticeListLimit5())
                .subscribeOn(Schedulers.boundedElastic());
    }
}
