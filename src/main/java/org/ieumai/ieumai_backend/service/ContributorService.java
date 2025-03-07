package org.ieumai.ieumai_backend.service;

import org.ieumai.ieumai_backend.domain.Contribution;
import org.ieumai.ieumai_backend.domain.Contributor;
import org.ieumai.ieumai_backend.domain.enums.City;
import org.ieumai.ieumai_backend.dto.ContributorProfile;
import org.ieumai.ieumai_backend.dto.ContributorUpdateRequest;
import org.ieumai.ieumai_backend.dto.UserSignUpRequest;
import org.ieumai.ieumai_backend.repository.ContributionRepository;
import org.ieumai.ieumai_backend.repository.ContributorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorRepository contributorRepository;
    private final ContributionRepository contributionRepository;

    @Transactional
    public Contributor getOrCreateUser(UserSignUpRequest request) {
        Optional<Contributor> existingUser = contributorRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        Contributor newContributor = Contributor.builder()
                .email(request.getEmail())
                .name(request.getName())
                .gender(request.getGender())
                .birthyear(request.getBirthyear())
                .state(request.getState())
                .city(City.valueOf(request.getCity()))
                .build();

        return contributorRepository.save(newContributor);
    }

    @Transactional(readOnly = true)
    public ContributorProfile getContributorProfile(String email) {
        Contributor contributor = contributorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 기여자를 찾을 수 없습니다: " + email));

        return convertToProfile(contributor);
    }

    @Transactional
    public ContributorProfile updateContributor(String email, ContributorUpdateRequest request) {
        Contributor contributor = contributorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 기여자를 찾을 수 없습니다: " + email));

        // 기존 정보 업데이트
        contributor.setName(request.getName());
        contributor.setGender(request.getGender());
        contributor.setBirthyear(request.getBirthyear());
        contributor.setState(request.getState());
        contributor.setCity(City.valueOf(request.getCity()));

        Contributor updatedContributor = contributorRepository.save(contributor);
        return convertToProfile(updatedContributor);
    }

    public ContributorProfile convertToProfile(Contributor contributor) {
        // 기여 횟수 조회
        List<Contribution> contributions = contributionRepository.findByContributor_ContributorId(contributor.getContributorId());
        int totalContributions = contributions.size();

        return ContributorProfile.builder()
                .contributorId(contributor.getContributorId())
                .name(contributor.getName())
                .email(contributor.getEmail())
                .gender(contributor.getGender())
                .birthyear(contributor.getBirthyear())
                .state(contributor.getState())
                .city(contributor.getCity())
                .totalContributions(totalContributions)
                .build();
    }
}