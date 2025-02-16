package ai.ieum.ieumai_backend.service;

import ai.ieum.ieumai_backend.domain.Contributor;
import ai.ieum.ieumai_backend.dto.UserSignUpRequest;
import ai.ieum.ieumai_backend.repository.ContributorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorRepository contributorRepository;

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
                .city(request.getCity())
                .build();

        return contributorRepository.save(newContributor);
    }
}
