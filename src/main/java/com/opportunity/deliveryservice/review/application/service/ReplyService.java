package com.opportunity.deliveryservice.review.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opportunity.deliveryservice.global.common.code.ClientErrorCode;
import com.opportunity.deliveryservice.global.common.exception.OpptyException;
import com.opportunity.deliveryservice.review.domain.entity.Reply;
import com.opportunity.deliveryservice.review.domain.entity.Review;
import com.opportunity.deliveryservice.review.domain.repository.ReplyRepository;
import com.opportunity.deliveryservice.review.domain.repository.ReviewRepository;
import com.opportunity.deliveryservice.review.presentation.dto.request.CreateReplyRequest;
import com.opportunity.deliveryservice.review.presentation.dto.request.UpdateReplyRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyService {

	private final ReviewRepository reviewRepository;
	private final ReplyRepository replyRepository;

	@Transactional
	public void createReply(CreateReplyRequest request, UUID reviewId) {

		Review review = getReviewId(reviewId);

		validate();

		Reply newReply = Reply.builder()
			.content(request.content())
			.review(review)
			.build();

		review.setReply(newReply);

		reviewRepository.save(review);
	}

	@Transactional
	public void updateReply(UUID reviewId, UpdateReplyRequest request) {
		validate();
		Review review = getReviewId(reviewId);
		Reply updateReply = review.getReply();

		if(updateReply == null){
			throw new OpptyException(ClientErrorCode.REPLY_NOT_FOUND);
		}
		updateReply.updateReply(request.content());
	}

	@Transactional
	public void deleteReply(UUID reviewId, Long userId){
		validate();

		Reply deletedReply = getReviewId(reviewId).getReply();
		if (deletedReply == null) {
			throw new OpptyException(ClientErrorCode.REPLY_NOT_FOUND);
		}
		deletedReply.softDelete(userId);
	}

	@Transactional(readOnly = true)
	public Reply getReply(UUID reviewId) {
		validate();
		Reply reply = getReviewId(reviewId).getReply();
		if (reply == null) {
			throw new OpptyException(ClientErrorCode.REPLY_NOT_FOUND);
		}
		return reply;
	}// 답글 단독 조회도 필요할까?

	private void validate(){
		// 사용자 검증 코드 구현 (추후)
	}

	private Review getReviewId(UUID reviewId){
		return reviewRepository.findById(reviewId).orElseThrow(
			() -> new OpptyException(ClientErrorCode.REVIEW_NOT_FOUND)
		);
	}

	private Reply getReplyId(UUID replyId){
		return replyRepository.findById(replyId).orElseThrow(
			() -> new OpptyException((ClientErrorCode.REPLY_NOT_FOUND))
		);
	}
}
