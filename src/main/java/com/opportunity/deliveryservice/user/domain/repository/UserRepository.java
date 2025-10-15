package com.opportunity.deliveryservice.user.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.opportunity.deliveryservice.user.domain.entity.User;
import com.opportunity.deliveryservice.user.domain.entity.UserRoleEnum;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	Optional<User> findByUsername(String username);

	// 관리자 전용 조회(Soft Delete 포함)
	// keyword로 username 또는 email 검색

	// @Query(value = """
	// 		SELECT u
	// 		FROM User u
	// 		WHERE
	// 			(u.deletedAt IS NULL OR u.deletedAt IS NOT NULL)
	// 			AND (:keyword IS NULL OR u.username LIKE %:keyword% OR u.email LIKE %:keyword%)
	// 			AND (:role IS NULL OR u.role = :role)
	// 		ORDER BY u.createdAt DESC
	// 	""")
	// Page<User> findUsersByAdminCriteria(
	// 	@Param("role") UserRoleEnum role,
	// 	@Param("keyword") String keyword,
	// 	Pageable pageable
	// );

	// 관리자 전용 조회(Soft Delete 포함) - JPQL로 명시적 구현
	@Query(value = """
		SELECT u 
		FROM User u 
		WHERE 
		    (u.deletedAt IS NULL OR u.deletedAt IS NOT NULL)  /* <--- deletedAt 조건 명시적으로 해제 */
		    AND (:keyword IS NULL OR u.username LIKE :keyword OR u.email LIKE :keyword)
		    AND (:role IS NULL OR u.role = :role)
		ORDER BY u.createdAt DESC
		""")
	Page<User> findUsersByAdminCriteria(
		@Param("role") UserRoleEnum role,
		@Param("keyword") String keyword, // ServiceV1에서 이미 %를 붙였으므로 여기서는 :keyword로만 사용
		Pageable pageable
	);
}
