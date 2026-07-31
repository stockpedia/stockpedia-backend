package com.ktb.postlike.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcPostLikeRepository implements PostLikeRepository {

    private final JdbcTemplate jdbcTemplate;


    @Override
    public void save(Long postId, Long currentMemberId) {
        String sql = "insert into post_like (post_id, member_id) values (?, ?)";

        jdbcTemplate.update(sql, postId, currentMemberId);

    }

    @Override
    public void delete(Long postId, Long currentMemberId) {
        String sql = "delete from post_like where post_id = ? and member_id = ?";

        jdbcTemplate.update(sql, postId, currentMemberId);
    }

    @Override
    public boolean existsByPostIdAndMemberId(Long postId, Long currentMemberId) {
        String sql = "select count(*) from post_like where post_id = ? and member_id = ?";

        Integer i = jdbcTemplate.queryForObject(sql, Integer.class, postId, currentMemberId);

        return i != null && i > 0;
    }
}
