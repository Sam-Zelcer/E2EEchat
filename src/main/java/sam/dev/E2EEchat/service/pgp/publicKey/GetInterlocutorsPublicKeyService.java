package sam.dev.E2EEchat.service.pgp.publicKey;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.pgp.publicKey.GetInterlocutorsPublicKeyRequest;
import sam.dev.E2EEchat.repository.entitys.pgp.PrivateChat;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.pgp.PrivateChatRepository;
import sam.dev.E2EEchat.repository.pgp.PublicKeyRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;
import sam.dev.E2EEchat.service.jwt.JWTService;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GetInterlocutorsPublicKeyService {

    private static final Logger logger = LoggerFactory.getLogger(GetInterlocutorsPublicKeyService.class);

    private final PublicKeyRepository publicKeyRepository;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final PrivateChatRepository privateChatRepository;

    public String getInterlocutorsPublicKey(GetInterlocutorsPublicKeyRequest request, String authHeader) {
        if (request.getChatId()==null || authHeader.isEmpty()) return "bad request";

        Optional<User> optionalUser;
        if (authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
            optionalUser = userRepository.findById(
                    jwtService.extractUsersId(authHeader.substring(7))
            );
        } else return "bad jwt token";
        if (optionalUser.isEmpty()) return "your account no longer exists";

        Optional<PrivateChat> optionalPrivateChat = privateChatRepository.findById(request.getChatId());
        if (optionalPrivateChat.isEmpty()) return "this chat doesn't exists, or your account no longer exists";

        String publicKey = "";
        if (optionalUser.get().equals(optionalPrivateChat.get().getFirstUser())) {
            publicKey = publicKeyRepository.findPublicKeyByOwner(optionalPrivateChat.get().getSecondUser()).toString();
        } else if (optionalUser.get().equals(optionalPrivateChat.get().getSecondUser())) {
            publicKey = publicKeyRepository.findPublicKeyByOwner(optionalPrivateChat.get().getFirstUser()).toString();
        }
        if (publicKey.isEmpty()) return "something went wrong";
        return publicKey;
    }
}
