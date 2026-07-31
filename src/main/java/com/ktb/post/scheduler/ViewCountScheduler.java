package com.ktb.post.scheduler;

import com.ktb.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewCountScheduler {

    private final StringRedisTemplate template;

    private final PostRepository postRepository;

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void synchronizeViewCount() {
        ScanOptions options = ScanOptions.scanOptions()
                .match("view:post:*")
                .count(1000)
                .build();

        try (Cursor<String> cursor = template.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();

                String value = template.opsForValue().getAndDelete(key);

                if (value == null) {
                    continue;
                }

                Long postId = Long.parseLong(key.split(":")[2]);
                Long delta = Long.parseLong(value);

                postRepository.incrementViewCount(postId, delta);
            }
        }
    }
}
