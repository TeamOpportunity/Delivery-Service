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
import com.opportunity.deliveryservice.store.domain.repository.StoreRepository;
import com.opportunity.deliveryservice.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReplyService {

	private final ReviewRepository reviewRepository;

	@Transactional
	public void createReply(CreateReplyRequest request, UUID reviewId, User currentUser) {

		Review review = getReviewId(reviewId);
		validate(review, currentUser);

		Reply newReply = Reply.builder().content(request.content()).review(review).user(currentUser).build();

		review.setReply(newReply);

		reviewRepository.save(review);
	}

	@Transactional
	public void updateReply(UUID reviewId, UpdateReplyRequest request, User currentUser) {
		Review review = getReviewId(reviewId);
		validate(review, currentUser);

		Reply updateReply = review.getReply();

		if (updateReply == null) {
			throw new OpptyException(ClientErrorCode.REPLY_NOT_FOUND);
		}
		updateReply.updateReply(request.content());
	}

	@Transactional
	public void deleteReply(UUID reviewId, User currentUser) {
		Review review = getReviewId(reviewId);
		Reply reply = review.getReply();

		if (reply == null) {
			throw new OpptyException(ClientErrorCode.REPLY_NOT_FOUND);
		}
		validate(review, currentUser);

		reply.softDelete(currentUser.getId());
	}

	@Transactional(readOnly = true)
	public Reply getReply(UUID reviewId) {
		Reply reply = getReviewId(reviewId).getReply();
		if (reply == null) {
			throw new OpptyException(ClientErrorCode.REPLY_NOT_FOUND);
		}
		return reply;
	}
	// 답글 단독 조회도 필요할까?

	//관리자 or 사장님인지
	private void validate(Review review, User currentUser) {
		String role = currentUser.getRole().toString();
		// 관리자라면 허용 , 사장님이라면 자신 가게에서만 허용
		if ("MASTER".equals(role) || "MANAGER".equals(role))
			return;
		if (review.getStore() != null && review.getStore().getUserId().equals(currentUser.getId()))
			return;
		throw new OpptyException(ClientErrorCode.FORBIDDEN);
	}

	private Review getReviewId(UUID reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new OpptyException(ClientErrorCode.REVIEW_NOT_FOUND));
	}

}
