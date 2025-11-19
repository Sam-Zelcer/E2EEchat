package sam.dev.E2EEchat.service.pgp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessage;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessagesLastMessageIdResponse;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessagesRequest;
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
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PrivateChatRepository privateChatRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GetMessagesLastMessageIdResponse getMessages(GetMessagesRequest request, String autHeader) {
        try {
            if (request.getChatId()==null || autHeader.isEmpty() || request.getLastMessageId()==null) {
                logger.info("invalid chat_id was provided, or bad authorization header was provided");
                return null;
            }

            Optional<User> optionalUser = userRepository.findById(
                    jwtService.extractUsersId(autHeader.substring(7))
            );
            Optional<PrivateChat> optionalPrivateChat = privateChatRepository.findById(
                    request.getChatId()
            );
            if (
                    optionalUser.isEmpty() ||
                            optionalPrivateChat.isEmpty()
            ) {
                logger.info("provided chat doesn't exists or your account no longer exists");
                return null;
            }

            Pageable pageable = PageRequest.of(0, 20, Sort.by("id").descending());
            Optional<List<Message>> messages = Optional.empty();
            if (
                    request.getLastMessageId() == null
            ) messages =  messageRepository.findMessagesByPrivateChat(
                    optionalPrivateChat.get(),
                    pageable
            );
            if (
                    request.getLastMessageId() != null
            ) messages =  messageRepository.findExtraMessagesByPrivateChat(
                    optionalPrivateChat.get(),
                    request.getLastMessageId(),
                    pageable
            );
            if (messages.isEmpty()) {
                logger.info("you have no messages in this chat");
                return null;
            }
            List<GetMessage> mappedMessages =messages.get().stream()
                    .map(
                            message -> new GetMessage(
                                    message.getSender().getUsername(),
                                    message.getEncryptedSessionKey(),
                                    message.getEncryptedMessage(),
                                    message.getAt()
                            )
                    ).toList();
            return new GetMessagesLastMessageIdResponse(mappedMessages, messages.get().getLast().getId());

        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    public String sendMessage(SendMessageRequest request, String authHeader) {
        try {
            if (
                    request.getChatId()==null ||
                            request.getEncryptedMessage().isEmpty() ||
                            request.getEncryptedSessionKey().isEmpty()
            ) return "bad request";

            Optional<User> optionalUser = userRepository.findById(
                    jwtService.extractUsersId(authHeader.substring(7))
            );
            Optional<PrivateChat> optionalPrivateChat = privateChatRepository.findById(
                    request.getChatId()
            );
            if (
                    optionalUser.isEmpty() || optionalPrivateChat.isEmpty()
            ) return "provided chat or your account no longer exists";

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
            logger.info(e.getMessage());
        }
        return "something went wrong";
    }
}
