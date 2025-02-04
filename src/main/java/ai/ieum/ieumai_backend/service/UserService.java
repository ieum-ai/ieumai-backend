package ai.ieum.ieumai_backend.service;

import ai.ieum.ieumai_backend.domain.User;
import ai.ieum.ieumai_backend.dto.UserSignUpRequest;
import ai.ieum.ieumai_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User getOrCreateUser(UserSignUpRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .gender(request.getGender())
                .birthyear(request.getBirthyear())
                .state(request.getState())
                .city(request.getCity())
                .build();

        return userRepository.save(newUser);
    }
}
