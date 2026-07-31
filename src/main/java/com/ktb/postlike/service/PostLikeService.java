package com.ktb.postlike.service;

import com.ktb.global.utils.exception.BusinessException;
import com.ktb.global.utils.exception.ErrorCode;
import com.ktb.member.domain.Member;
import com.ktb.member.repository.MemberRepository;
import com.ktb.post.domain.Post;
import com.ktb.post.repository.PostRepository;
import com.ktb.postlike.domain.PostLike;
import com.ktb.postlike.domain.PostLikeId;
import com.ktb.postlike.dto.PostLikeResponse;
import com.ktb.postlike.repository.PostLikeRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class PostLikeService {

    private final static Logger log = LoggerFactory.getLogger(PostLikeService.class);

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final PostLikeRepository postLikeRepository;


    @Transactional
    public PostLikeResponse createPostLike(Long postId, Long currentMemberId) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        Member member = memberRepository.findMemberById(currentMemberId);

        if (member == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        PostLikeId postLikeId = new PostLikeId(postId, currentMemberId);

        boolean isLike = postLikeRepository.existsById(postLikeId);


        if (isLike) {
            throw new BusinessException(ErrorCode.ALREADY_LIKED);
        }

        PostLike postLike = new PostLike(post, member);

        postLikeRepository.save(postLike);

        postRepository.incrementLikeCount(post.getId());

        Long likeCount = postRepository.findLikeCountById(post.getId());

        return new PostLikeResponse(postId,
                currentMemberId,
                likeCount,
                true);
    }

    @Transactional
    public PostLikeResponse deletePostLike(Long postId, Long currentMemberId) {

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId);

        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        Member member = memberRepository.findMemberById(currentMemberId);

        if (member == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        PostLikeId postLikeId = new PostLikeId(postId, currentMemberId);

        boolean isLike = postLikeRepository.existsById(postLikeId);

        if (!isLike) {
            throw new BusinessException(ErrorCode.NOT_LIKED_YET);
        }

        PostLike postLike = postLikeRepository.findPostLikeById(postLikeId);

        postLikeRepository.delete(postLike);
        postRepository.decrementLikeCount(post.getId());

        Long likeCount = postRepository.findLikeCountById(post.getId());

        return new PostLikeResponse(postId,
                currentMemberId,
                likeCount,
                false);
    }
}
