package sam.dev.E2EEchat.service.pgp.privateChat;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.pgp.privateChat.CreatePrivateChatRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.privateChat.NewPrivateChatResponse;
import sam.dev.E2EEchat.repository.entitys.pgp.PrivateChat;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.pgp.PrivateChatRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;
import sam.dev.E2EEchat.service.jwt.JWTService;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class CreatePrivateChatService {

    private static final Logger logger = LoggerFactory.getLogger(CreatePrivateChatService.class);

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final PrivateChatRepository privateChatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public String createPrivateChat(CreatePrivateChatRequest request, String authHeader) {
        try {
            if (authHeader == null || authHeader.trim().isEmpty() ||
                    request.getSecondUserNameId() == null || request.getSecondUserNameId().trim().isEmpty()) {
                return "bad request";
            }

            String[] secondUserNameId = request.getSecondUserNameId().split(":", 2);
            if (secondUserNameId.length != 2) {
                return "invalid user format";
            }
            Long secondUserId;
            try {
                secondUserId = Long.parseLong(secondUserNameId[1]);
            } catch (Exception e) {
                return "invalid second user id format";
            }

            Optional<User> optionalFirstUser;
            if (authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                optionalFirstUser = userRepository.findById(
                        jwtService.extractUsersId(authHeader.substring(7))
                );
            } else return "bad jwt token";
            if (optionalFirstUser.isEmpty()) return "your account no longer exists";

            Optional<User> optionalSecondUser = userRepository.findById(secondUserId);
            if (
                    optionalSecondUser.isEmpty() ||
                            optionalFirstUser.get().getId().equals(secondUserId)
            ) return "second user was provided incorrectly, or your account no longer exists";
            if (
                    privateChatRepository.findExistingPrivateChat(
                            optionalFirstUser.get(),
                            optionalSecondUser.get()
                    ).isPresent()
            ) return "you already have chat with this user";

            PrivateChat chat = new PrivateChat();
            chat.setFirstUser(optionalFirstUser.get());
            chat.setSecondUser(optionalSecondUser.get());
            chat.setId(privateChatRepository.save(chat).getId());

            simpMessagingTemplate.convertAndSend(
                    "/topic/chat/get-chats/"+chat.getFirstUser().getId(),
                    new NewPrivateChatResponse(chat.getId(), chat.getSecondUser().getUsername()
                    )
            );
            simpMessagingTemplate.convertAndSend(
                    "/topic/chat/get-chats/"+chat.getSecondUser().getId(),
                    new NewPrivateChatResponse(chat.getId(), chat.getFirstUser().getUsername()
                    )
            );
            return "chat was created";

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "something went wrong";
    }
}
