package com.ktb.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PostViewCountService {

    private final StringRedisTemplate template;

    public Long increaseView(Long postId, Long memberId) {
        String countKey = String.format("view:post:%d", postId);
        String duplicationKey = String.format("view:dedup:post:%d:member:%d", postId, memberId);

        Boolean isFirst = template.opsForValue().setIfAbsent(duplicationKey, "1", Duration.ofMinutes(30));

        if (Boolean.TRUE.equals(isFirst)) {
            template.opsForValue().increment(countKey);
        }

        return getViewCount(postId);
    }

    public Long getViewCount(Long postId) {
        String countKey = String.format("view:post:%d", postId);
        String viewCount = template.opsForValue().get(countKey);

        if (viewCount != null) {
            return Long.parseLong(viewCount);
        }
        return 0L;
    }
}
