package org.ieumai.ieumai_backend.repository;

import org.ieumai.ieumai_backend.domain.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
    List<UserReview> findByTestVoice_Id(Long testVoiceId);
}
