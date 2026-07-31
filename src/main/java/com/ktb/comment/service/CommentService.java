package com.ktb.comment.service;

import com.ktb.comment.domain.Comment;
import com.ktb.comment.dto.CommentResponse;
import com.ktb.comment.repository.CommentRepository;
import com.ktb.global.utils.exception.BusinessException;
import com.ktb.global.utils.exception.ErrorCode;
import com.ktb.member.domain.Member;
import com.ktb.member.repository.MemberRepository;
import com.ktb.post.domain.Post;
import com.ktb.post.dto.PostResponse;
import com.ktb.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;


    @Transactional
    public CommentResponse.CreateCommentResponse createComment(
            Long postId, Long currentMemberId, String content) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        Member member = memberRepository.findMemberById(currentMemberId);

        if (member == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        Comment comment = new Comment(
                post,
                member,
                content
        );

        postRepository.incrementCommentCount(post.getId());

        Comment result = commentRepository.save(comment);

        return commentRepository.findCommentById(result.getId(), result.getMember().getId());
    }

    @Transactional(readOnly = true)
    public CommentResponse.CommentPageResponse getComments(Long postId, Long memberId, Long cursor, Integer limit) {

        if (limit == null) {
            limit = 10;
        }
        if (limit < 1 || limit > 30) {
            throw new BusinessException(ErrorCode.INVALID_PAGE_SIZE);
        }

        List<CommentResponse.CommentResult> comments =
                commentRepository.findCommentResultByCursor(postId, memberId, cursor, limit + 1);

        // size보다 많이 왔으면 다음 페이지 있음
        boolean hasNext = comments.size() > limit;

        if (hasNext) {
            comments = comments.subList(0, limit);
        }

        // 다음 cursor = 마지막 글 id (없으면 null)
        Long nextCursor = hasNext ? comments.get(comments.size() - 1).getCommentId() : null;

        return new CommentResponse.CommentPageResponse(comments, nextCursor, hasNext);
    }

    @Transactional
    public CommentResponse.UpdateCommentResponse updateComment(
            Long postId, Long currentMemberId, Long commentId, String content) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId);

        if (comment == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }

        if (!comment.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.COMMENT_UPDATE_ACCESS_FORBIDDEN);
        }

        comment.update(content);

        return new CommentResponse.UpdateCommentResponse(
                comment.getId(),
                comment.getContent());
    }

    @Transactional
    public void deleteComment(Long postId, Long currentMemberId, Long commentId) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        Comment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId);

        if (comment == null || !comment.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }

        if (!comment.getMember().getId().equals(currentMemberId)) {
            throw new BusinessException(ErrorCode.COMMENT_DELETE_ACCESS_FORBIDDEN);
        }

        comment.softDelete();
        postRepository.decrementCommentCount(post.getId());
    }
}
