package hello.atfeelogbackend.domain.user.service;

import hello.atfeelogbackend.domain.user.dto.CreateUserInput;
import hello.atfeelogbackend.domain.user.dto.LoginRequest;
import hello.atfeelogbackend.domain.user.dto.UpdateUserInput;
import hello.atfeelogbackend.domain.user.entity.User;
import hello.atfeelogbackend.domain.user.repository.UserRepository;
import hello.atfeelogbackend.global.exception.CustomException;
import hello.atfeelogbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public User createUser(CreateUserInput createUserInput) {
        User user = User.builder()
                .email(createUserInput.getEmail())
                .name(createUserInput.getName())
                .password(bCryptPasswordEncoder.encode(createUserInput.getPassword()))
                .picture(null)
                .build();

        return userRepository.save(user);
    }



    @Transactional
    public User updateUser(UpdateUserInput updateUserInput, Long id){
        try{
            User user = findById(id);
            String encodedPassword = updateUserInput.getPassword() != null
                    ? bCryptPasswordEncoder.encode(updateUserInput.getPassword()) : null;
            user.update(updateUserInput.getName(), encodedPassword, updateUserInput.getPicture());
            return user;
        }catch (Exception e){
            throw new CustomException(ErrorCode.USER_UPDATE_ERROR);
        }

    }


    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public boolean validateUser(String email, String password){
        User user = findByEmail(email);
        return bCryptPasswordEncoder.matches(password, user.getPassword());
    }

}
