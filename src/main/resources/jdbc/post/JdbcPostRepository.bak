package com.ktb.post.repository;

import com.ktb.post.domain.Post;
import com.ktb.post.dto.PostResponse;
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
public class JdbcPostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Post save(Post post) {

        /**
         * viewCount, likeCount, commentCount default 0
         */
        String sql = "insert into post (member_id, title, content) values (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psmt.setLong(1, post.getMemberId());
            psmt.setString(2, post.getTitle());
            psmt.setString(3, post.getContent());

            return psmt;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        return findByPostId(id);
    }

    @Override
    public Post findByPostId(Long postId) {

        String sql = """
                select id,
                        member_id,
                        title,
                        content,
                        like_count,
                        view_count,
                        comment_count,
                        created_at,
                        updated_at,
                        deleted_at
                from post
                where id = ?
                """;

        List<Post> result = jdbcTemplate.query(sql, new RowMapper<Post>() {
            @Override
            public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Post(
                        rs.getLong("id"),
                        rs.getLong("member_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getLong("view_count"),
                        rs.getLong("like_count"),
                        rs.getLong("comment_count"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class),
                        rs.getObject("deleted_at", LocalDateTime.class)
                );
            }
        }, postId);

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public Long findAuthorIdByPostId(Long postId) {
        String sql = "select member_id from post where id = ?";

        List<Long> result = jdbcTemplate.query(sql, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("member_id");
            }
        }, postId);

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public PostResponse.DetailPostResponse findPostDetailByPostId(Long postId, Long currentMemberId) {
        String sql = """
                    select
                        post.id as post_id,
                        post.title,
                        post.content,
                        post.view_count,
                        post.like_count,
                        post.comment_count,
                        post.created_at,
                        post.updated_at,
                        case
                            when post.member_id = ?
                            then true
                            else false
                        end as is_mine,
                        member.id as member_id,
                        member.nickname
                    from post
                    join member
                        on post.member_id = member.id
                    where post.id = ?
                    and post.deleted_at is null
                """;

        List<PostResponse.DetailPostResponse> result = jdbcTemplate.query(sql, new RowMapper<PostResponse.DetailPostResponse>() {
            @Override
            public PostResponse.DetailPostResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new PostResponse.DetailPostResponse(
                        rs.getLong("post_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("view_count"),
                        rs.getInt("like_count"),
                        rs.getInt("comment_count"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class),
                        rs.getBoolean("is_mine"),
                        new PostResponse.AuthorResponse(
                                rs.getLong("member_id"),
                                rs.getString("nickname")
                        )
                );
            }
        }, currentMemberId, postId);

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public void updateById(Long postId, String title, String content) {
        String sql = "update post set title = ?, content = ?, updated_at = NOW() where id = ?";

        jdbcTemplate.update(sql, title, content, postId);
    }

    @Override
    public void deleteById(Long postId) {
        String sql = "delete from post where id = ?";

        jdbcTemplate.update(sql, postId);
    }

    @Override
    public void incrementViewCount(Long postId) {
        String sql = "update post set view_count = view_count + 1 where id = ?";

        jdbcTemplate.update(sql, postId);
    }

    @Override
    public void incrementLikeCount(Long postId) {
        String sql = "update post set like_count = like_count + 1 where id = ?";

        jdbcTemplate.update(sql, postId);
    }

    @Override
    public void decrementLikeCount(Long postId) {
        String sql = "update post set like_count = like_count - 1 where id = ?";

        jdbcTemplate.update(sql, postId);
    }

    @Override
    public void incrementCommentCount(Long postId) {
        String sql = "update post set comment_count = comment_count + 1 where id = ?";

        jdbcTemplate.update(sql, postId);
    }

    @Override
    public void decrementCommentCount(Long postId) {
        String sql = "update post set comment_count = comment_count - 1 where id = ?";

        jdbcTemplate.update(sql, postId);
    }

    @Override
    public Long findLikeCountByPostId(Long postId) {
        String sql = "select like_count from post where id = ?";

        return jdbcTemplate.queryForObject(sql, Long.class, postId);
    }
}
