package ai.ieum.ieumai_backend.repository;

import ai.ieum.ieumai_backend.domain.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long> {
    List<UserReview> findByTestVoiceId(Long testVoiceId);
}
