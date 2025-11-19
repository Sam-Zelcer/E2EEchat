package sam.dev.E2EEchat.service.pgp.privateChat;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.pgp.privateChat.GetPrivateChatsResponse;
import sam.dev.E2EEchat.repository.entitys.pgp.PrivateChat;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.pgp.PrivateChatRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;
import sam.dev.E2EEchat.service.jwt.JWTService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class GetPrivateChatsService {

    private static final Logger logger = LoggerFactory.getLogger(GetPrivateChatsService.class);

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final PrivateChatRepository privateChatRepository;

    public List<GetPrivateChatsResponse> getChats(String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty()) return Collections.emptyList();

            Optional<User> optionalUser;
            if (authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                optionalUser = userRepository.findById(
                        jwtService.extractUsersId(authHeader.substring(7))
                );
            } else return Collections.emptyList();
            if (optionalUser.isEmpty()) return Collections.emptyList();

            Optional<List<PrivateChat>> privateChats = privateChatRepository
                    .findUsersPrivateChats(optionalUser.get());
            if (privateChats.isEmpty()) return Collections.emptyList();

            return privateChats.map(chats -> chats.stream()
                    .map(
                            privateChat -> new GetPrivateChatsResponse(
                                    privateChat.getId(),
                                    optionalUser.get().equals(privateChat.getFirstUser()) ?
                                            privateChat.getSecondUser().getUsername() :
                                            privateChat.getFirstUser().getUsername()
                            )
                    ).toList()).orElse(null);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return Collections.emptyList();
    }
}
