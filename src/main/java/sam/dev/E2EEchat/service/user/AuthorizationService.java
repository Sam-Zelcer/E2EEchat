package sam.dev.E2EEchat.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.auth.SignInRequest;
import sam.dev.E2EEchat.repository.dtos.auth.SignUpRequest;
import sam.dev.E2EEchat.repository.entitys.user.UnverifiedUser;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.user.UnverifiedUserRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;
import sam.dev.E2EEchat.service.email.EmailService;
import sam.dev.E2EEchat.service.jwt.JWTService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    private final UserRepository userRepository;
    private final UnverifiedUserRepository unverifiedUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JWTService jwtService;

    public String singUp(SignUpRequest request) {
        try {
            unverifiedUserRepository.deleteUnverifiedUsersByExpirationBefore(LocalDateTime.now());
            if (
                    request.getUsername().isEmpty() ||
                            request.getEmail().isEmpty() ||
                            request.getPassword().isEmpty() ||
                            request.getUsername().length() < 4 || request.getUsername().length() > 80 ||
                            request.getPassword().length() < 8 || request.getPassword().length() > 100
            ) return "bad request";

            if (
                    userRepository.findUserByUsername(request.getUsername()).isPresent() ||
                            userRepository.findUserByEmail(request.getEmail()).isPresent()
            ) return "user with username: "+request.getUsername()+" or email: "+request.getEmail()+" already exist";

            UnverifiedUser user = new UnverifiedUser();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setExpiration(LocalDateTime.now().plusMinutes(20));
            user.setCode(100000 + (int)(Math.random() * (999999 - 100000) + 1));
            unverifiedUserRepository.save(user);

            if (
                    emailService.sendMail(
                            request.getEmail(),
                            "verification",
                            "http://localhost:8080/unauthorized/verification?code="+user.getCode()
                    )!=200
            ) {
                logger.info("id --> {}", user.getId());
                unverifiedUserRepository.deleteById(user.getId());
                return "error with Email service";
            }

            return "unverified user was created";

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return "something went wrong";
    }

    public String verification(Integer code) {
        try {
            if (code==null) return "you have to provide code";
            Optional<UnverifiedUser> optionalUnverifiedUser = unverifiedUserRepository
                    .findUnverifiedUserByCode(code);
            if (optionalUnverifiedUser.isEmpty()) return "this user doesn't exist";

            User user = new User();
            user.setUsername(optionalUnverifiedUser.get().getUsername());
            user.setEmail(optionalUnverifiedUser.get().getEmail());
            user.setPassword(optionalUnverifiedUser.get().getPassword());

            unverifiedUserRepository.deleteById(optionalUnverifiedUser.get().getId());
            userRepository.save(user);
            return "user was successfully created";

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return "something went wrong";
    }

    public String signIn(SignInRequest request) {
        logger.info("method sign-in start");
        try {
            if (
                    request.getPassword().isEmpty() ||
                            request.getUsername().isEmpty()
            ) return "you have to provide password and email";

            Optional<User> optionalUser = userRepository.findUserByUsername(request.getUsername());
            if (optionalUser.isEmpty()) return "user with username: "+request.getUsername()+" doesn't exist";

            if (
                    passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())
            ) return jwtService.generateToken(optionalUser.get().getId());

            return "bad credentials";

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return "something went wrong";
    }
}
