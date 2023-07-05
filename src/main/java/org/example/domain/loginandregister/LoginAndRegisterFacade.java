package org.example.domain.loginandregister;

import lombok.AllArgsConstructor;
import org.example.domain.loginandregister.dto.RegisterUserDto;
import org.example.domain.loginandregister.dto.RegistrationResultDto;
import org.example.domain.loginandregister.dto.UserDto;

@AllArgsConstructor
public class LoginAndRegisterFacade {

    private static final String USER_NOT_FOUND = "User not found";
    private final LoginRepository loginRepository;

    public UserDto findByUsername(String username) {
        return loginRepository.findByUsername(username)
                .map(user -> new UserDto(user.id(), user.username(), user.password()))
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
    }

    public RegistrationResultDto register(RegisterUserDto registerUserDto) {

      final  User user = User.builder()
                .username(registerUserDto.username())
                .password(registerUserDto.password())
                .build();
        User created = loginRepository.save(user);
        return new RegistrationResultDto(created.id(), true, created.username());
    }

}