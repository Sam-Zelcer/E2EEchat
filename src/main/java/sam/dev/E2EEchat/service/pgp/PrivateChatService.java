package sam.dev.E2EEchat.service.pgp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.pgp.privateChat.GetPrivateChatsResponse;
import sam.dev.E2EEchat.repository.dtos.pgp.publicKey.CreatePrivateChatRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.privateChat.ReturnNewPrivateChat;
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
@RequiredArgsConstructor
public class PrivateChatService {

    private static final Logger logger = LoggerFactory.getLogger(PrivateChatService.class);

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final PrivateChatRepository privateChatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public String createPrivateChat(CreatePrivateChatRequest request, String authHeader) {
        try {
            if (
                    request.getName().isEmpty() ||
                            request.getSecondUserNameId().isEmpty() ||
                            authHeader.isEmpty()
            ) return "bad request";

            String[] secondUserNameId = request.getSecondUserNameId().split(":", 2);

            Optional<User> optionalFirstUser = userRepository.findById(
                    jwtService.extractUsersId(authHeader.substring(7))
            );
            Optional<User> optionalSecondUser = userRepository.findById(
                    Long.parseLong(secondUserNameId[1])
            );

            if (
                    optionalFirstUser.isEmpty() ||
                            optionalSecondUser.isEmpty() ||
                            optionalSecondUser.get().getId().equals(Long.parseLong(secondUserNameId[1]))
            ) return "second user was provided incorrectly, or your account no longer exists";
            if (
                    secondUserNameId[0].equals(optionalFirstUser.get().getUsername())
            ) return "you can't create chat with yourself";
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
                    new ReturnNewPrivateChat(chat.getId(), chat.getSecondUser().getUsername()
                    )
            );
            simpMessagingTemplate.convertAndSend(
                    "/topic/chat/get-chats/"+chat.getSecondUser().getId(),
                    new ReturnNewPrivateChat(chat.getId(), chat.getFirstUser().getUsername()
                    )
            );
            return "chat was created";

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return "something went wrong";
   }

    public List<GetPrivateChatsResponse> getChats(String authHeader) {
        try {
            if (authHeader.isEmpty()) return Collections.emptyList();

            Optional<User> optionalUser = userRepository.findById(
                    jwtService.extractUsersId(authHeader.substring(7))
            );
            if (optionalUser.isEmpty()) return Collections.emptyList();

            Optional<List<PrivateChat>> privateChats = privateChatRepository
                    .findUsersPrivateChats(optionalUser.get());

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
            logger.info(e.getMessage());
        }
        return Collections.emptyList();
    }
}
