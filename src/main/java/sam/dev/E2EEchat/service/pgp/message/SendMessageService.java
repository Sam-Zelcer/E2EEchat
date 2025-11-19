package sam.dev.E2EEchat.service.pgp.message;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessage;
import sam.dev.E2EEchat.repository.dtos.pgp.message.SendMessageRequest;
import sam.dev.E2EEchat.repository.entitys.pgp.Message;
import sam.dev.E2EEchat.repository.entitys.pgp.PrivateChat;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.pgp.MessageRepository;
import sam.dev.E2EEchat.repository.pgp.PrivateChatRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;
import sam.dev.E2EEchat.service.jwt.JWTService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SendMessageService {

    private static final Logger logger = LoggerFactory.getLogger(SendMessageService.class);

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PrivateChatRepository privateChatRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public String sendMessage(SendMessageRequest request, String authHeader) {
        try {
            if (
                    request.getChatId()==null ||
                            request.getEncryptedMessage().isEmpty() ||
                            request.getEncryptedSessionKey().isEmpty()
            ) return "bad request";

            Optional<User> optionalUser;
            if (authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                optionalUser = userRepository.findById(
                        jwtService.extractUsersId(authHeader.substring(7))
                );
            } else return "bad jwt token";
            if (optionalUser.isEmpty()) return "your account no longer exists";

            Optional<PrivateChat> optionalPrivateChat = privateChatRepository.findById(
                    request.getChatId()
            );
            if (optionalPrivateChat.isEmpty()) return "provided chat or your account no longer exists";

            Message message = new Message();
            message.setSender(optionalUser.get());
            message.setPrivateChat(optionalPrivateChat.get());
            message.setEncryptedMessage(request.getEncryptedMessage());
            message.setEncryptedSessionKey(request.getEncryptedSessionKey());
            message.setAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            messageRepository.save(message);

            simpMessagingTemplate.convertAndSend(
                    "/topic/chat/get-messages/"+message.getPrivateChat().getId(),
                    new GetMessage(
                            message.getSender().getUsername(),
                            message.getEncryptedMessage(),
                            message.getEncryptedSessionKey(),
                            message.getAt()
                    )
            );
            return "message was sent";

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "something went wrong";
    }
}
