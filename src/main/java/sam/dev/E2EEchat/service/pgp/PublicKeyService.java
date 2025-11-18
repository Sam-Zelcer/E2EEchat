package sam.dev.E2EEchat.service.pgp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.pgp.publicKey.SavePublicKeyRequest;
import sam.dev.E2EEchat.repository.entitys.pgp.PublicKey;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.pgp.PublicKeyRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;
import sam.dev.E2EEchat.service.jwt.JWTService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PublicKeyService {

    private static final Logger logger = LoggerFactory.getLogger(PublicKeyService.class);

    private final PublicKeyRepository publicKeyRepository;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public String savePublicKey(SavePublicKeyRequest request, String authHeader) {
        try {
            if (
                    request.getPublicKey().isEmpty() ||
                            authHeader.isEmpty()
            ) return "you must provide public key";

            Optional<User> optionalUser = userRepository.findById(
                    jwtService.extractUsersId(authHeader.substring(7))
            );
            if (optionalUser.isEmpty()) return "your account no longer exists";

            PublicKey key = new PublicKey();
            key.setPublicKey(request.getPublicKey());
            key.setOwner(optionalUser.get());
            key.setExpiration(LocalDateTime.now().plusMonths(6));
            publicKeyRepository.save(key);
            return "public key was saved successfully";

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return "something want wrong";
    }
}
