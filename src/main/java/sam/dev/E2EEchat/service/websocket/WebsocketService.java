package sam.dev.E2EEchat.service.websocket;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import sam.dev.E2EEchat.repository.entitys.pgp.PrivateChat;
import sam.dev.E2EEchat.repository.entitys.user.User;
import sam.dev.E2EEchat.repository.pgp.PrivateChatRepository;
import sam.dev.E2EEchat.repository.user.UserRepository;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WebsocketService {

    private final PrivateChatRepository privateChatRepository;
    private final UserRepository userRepository;

    public boolean hasUserAccessToPrivateChat(Authentication authentication, Long chatId) {
        if (authentication == null || chatId == null || !authentication.isAuthenticated()) return false;

        String username = authentication.getName();
        Optional<PrivateChat> optionalPrivateChat = privateChatRepository.findById(chatId);

        return optionalPrivateChat.filter(
                privateChat ->
                        privateChat.getFirstUser().getUsername().equals(username) ||
                                privateChat.getSecondUser().getUsername().equals(username)
        ).isPresent();
    }

    public boolean hasUserAccessToChats(Authentication authentication, Long userId) {
        if (authentication == null || userId == null || !authentication.isAuthenticated()) return false;

        String username = authentication.getName();
        Optional<User> optionalUser = userRepository.findById(userId);

        return optionalUser.isPresent() && optionalUser.get().getUsername().equals(username);
    }
}
