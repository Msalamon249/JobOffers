package org.example.domain.loginandregister;

import lombok.AllArgsConstructor;
import org.example.domain.loginandregister.dto.RegisterUserDto;
import org.example.domain.loginandregister.dto.RegistrationResultDto;
import org.example.domain.loginandregister.dto.UserDto;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LoginAndRegisterFacade {

    private static final String USER_NOT_FOUND = "User not found";
    private final LoginRepository loginRepository;

    public UserDto findByUsername(String username) {
        return loginRepository.findByUsername(username)
                .map(user -> new UserDto(user.id(), user.username(), user.password()))
                .orElseThrow(() -> new BadCredentialsException(USER_NOT_FOUND));
    }


    public RegistrationResultDto register(RegisterUserDto registerUserDto) {

        boolean exists = loginRepository.existsByUsername(registerUserDto.username());
        if (exists) {
            throw new UsernameDuplicateException(registerUserDto.username());
        }
        final User user = User.builder()
                .username(registerUserDto.username())
                .password(registerUserDto.password())
                .build();
        User created = loginRepository.save(user);
        return new RegistrationResultDto(created.id(), true, created.username());
    }

}