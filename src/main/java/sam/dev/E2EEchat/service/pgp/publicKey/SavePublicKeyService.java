package sam.dev.E2EEchat.service.pgp.publicKey;

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
public class SavePublicKeyService {

    private static final Logger logger = LoggerFactory.getLogger(SavePublicKeyService.class);

    private final PublicKeyRepository publicKeyRepository;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public String savePublicKey(SavePublicKeyRequest request, String authHeader) {
        try {
            if (
                    !request.getPublicKey().startsWith("-----BEGIN PGP PUBLIC KEY BLOCK-----") ||
                            !request.getPublicKey().contains("-----END PGP PUBLIC KEY BLOCK-----") ||
                            authHeader.isEmpty()
            ) return "invalid public key";

            Optional<User> optionalUser;
            if (authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                optionalUser = userRepository.findById(
                        jwtService.extractUsersId(authHeader.substring(7))
                );
            } else return "bad jwt token";
            if (optionalUser.isEmpty()) return "your account no longer exists";

            PublicKey key = new PublicKey();
            key.setPublicKey(request.getPublicKey());
            key.setOwner(optionalUser.get());
            key.setExpiration(LocalDateTime.now().plusMonths(6));

            publicKeyRepository.deleteByOwner(optionalUser.get());
            publicKeyRepository.save(key);
            return "public key was saved successfully";

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return "something want wrong";
    }
}
