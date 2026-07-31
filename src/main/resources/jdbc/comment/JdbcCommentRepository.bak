package com.ktb.comment.repository;

import com.ktb.comment.domain.Comment;
import com.ktb.comment.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class JdbcCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public CommentResponse.CreateCommentResponse save(Comment comment) {
        String sql = "insert into comment (post_id, member_id, content) values (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psmt.setLong(1, comment.getPostId());
            psmt.setLong(2, comment.getMemberId());
            psmt.setString(3, comment.getContent());

            return psmt;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();

        return findResponseForCreatedComment(id);
    }

    @Override
    public CommentResponse.CreateCommentResponse findResponseForCreatedComment(Long commentId) {
        String sql = """
                select
                    comment.id,
                    comment.post_id,
                    comment.content,
                    comment.created_at,
                    member.id AS member_id,
                    member.nickname,
                    post.comment_count
                from comment
                join member ON comment.member_id = member.id
                join post on comment.post_id = post.id
                where comment.id = ?
                """;

        List<CommentResponse.CreateCommentResponse> result = jdbcTemplate.query(sql, new RowMapper<CommentResponse.CreateCommentResponse>() {
            @Override
            public CommentResponse.CreateCommentResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CommentResponse.CreateCommentResponse(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("content"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getLong("comment_count"),
                        new CommentResponse.AuthorResponse(
                                rs.getLong("member_id"),
                                rs.getString("nickname")
                        )
                );
            }
        }, commentId);

        if (result.isEmpty()) {
            throw new NoSuchElementException();
        }

        return result.get(0);
    }


    @Override
    public Comment findById(Long commentId) {
        String sql = """
                select
                id,
                post_id,
                member_id,
                content,
                created_at,
                updated_at,
                deleted_at
                from comment
                where id = ?
                """;

        List<Comment> result = jdbcTemplate.query(sql, (rs, rowNum) -> new Comment(
                rs.getLong("id"),
                rs.getLong("post_id"),
                rs.getLong("member_id"),
                rs.getString("content"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class),
                rs.getObject("deleted_at", LocalDateTime.class)
        ), commentId);

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public void updateById(Long commentId, String content) {
        String sql = "update comment set content = ?, updated_at = NOW() where id = ?";

        jdbcTemplate.update(sql, content, commentId);
    }

    @Override
    public void deleteById(Long commentId) {
        String sql = "delete from comment where id = ?";

        jdbcTemplate.update(sql, commentId);
    }
}
