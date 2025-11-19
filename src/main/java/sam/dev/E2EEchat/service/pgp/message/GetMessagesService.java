package sam.dev.E2EEchat.service.pgp.message;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessage;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessagesLastMessageIdResponse;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessagesRequest;
import sam.dev.E2EEchat.repository.entitys.pgp.Message;
import sam.dev.E2EEchat.repository.entitys.pgp.PrivateChat;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.pgp.MessageRepository;
import sam.dev.E2EEchat.repository.pgp.PrivateChatRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;
import sam.dev.E2EEchat.service.jwt.JWTService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class GetMessagesService {

    private static final Logger logger = LoggerFactory.getLogger(GetMessagesService.class);

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PrivateChatRepository privateChatRepository;
    private final MessageRepository messageRepository;

    public GetMessagesLastMessageIdResponse getMessages(GetMessagesRequest request, String authHeader) {
        try {
            if (request.getChatId()==null || authHeader.isEmpty() || request.getLastMessageId()==null) {
                logger.info("invalid chat_id was provided, or bad authorization header was provided");
                return null;
            }

            Optional<User> optionalUser;
            if (authHeader.startsWith("Bearer ") && authHeader.length() > 7) {
                optionalUser = userRepository.findById(
                        jwtService.extractUsersId(authHeader.substring(7))
                );
            } else return null;
            if (optionalUser.isEmpty()) return null;

            Optional<PrivateChat> optionalPrivateChat = privateChatRepository.findById(
                    request.getChatId()
            );
            if (optionalPrivateChat.isEmpty()) {
                logger.info("provided chat doesn't exists or your account no longer exists");
                return null;
            }
            if (
                    !optionalPrivateChat.get().getFirstUser().equals(optionalUser.get()) &&
                            !optionalPrivateChat.get().getSecondUser().equals(optionalUser.get())

            ) return null;

            Pageable pageable = PageRequest.of(0, 20, Sort.by("id").descending());
            Optional<List<Message>> messages;
            if (
                    request.getLastMessageId() == null
            ) messages =  messageRepository.findMessagesByPrivateChat(
                    optionalPrivateChat.get(),
                    pageable
            );
            else messages =  messageRepository.findExtraMessagesByPrivateChat(
                    optionalPrivateChat.get(),
                    request.getLastMessageId(),
                    pageable
            );
            if (messages.isEmpty()) {
                logger.info("you have no messages in this chat");
                return null;
            }
            List<GetMessage> mappedMessages = messages.get().stream()
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
            logger.error(e.getMessage());
        }
        return null;
    }
}
