package sam.dev.E2EEchat.controller.pgp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessagesLastMessageIdResponse;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessagesRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.message.SendMessageRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.privateChat.GetPrivateChatsResponse;
import sam.dev.E2EEchat.repository.dtos.pgp.privateChat.CreatePrivateChatRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.publicKey.GetInterlocutorsPublicKeyRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.publicKey.SavePublicKeyRequest;
import sam.dev.E2EEchat.service.pgp.PublicKeyService;
import sam.dev.E2EEchat.service.pgp.PrivateChatService;
import sam.dev.E2EEchat.service.pgp.MessageService;

import java.util.List;

@RestController
@RequestMapping("/pgp")
@RequiredArgsConstructor
public class PGPController {

    private final PublicKeyService publicKeyService;
    private final PrivateChatService privateChatService;
    private final MessageService messageService;

    @PostMapping("/save-public-key")
    public String savePublicKey(
            @RequestBody @Valid
            SavePublicKeyRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return publicKeyService.savePublicKey(request, authHeader);
    }

    @PostMapping("/create-private-chat")
    public String createPrivateChat(
            @RequestBody @Valid
            CreatePrivateChatRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return privateChatService.createPrivateChat(request, authHeader);
    }

    @GetMapping("/get-chats")
    public List<GetPrivateChatsResponse> getUsersChats(
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return privateChatService.getChats(authHeader);
    }

    @PostMapping("/get-interlocutors-public-key")
    public String getInterlocutorsPublicKey(
            @RequestBody @Valid
            GetInterlocutorsPublicKeyRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return publicKeyService.getInterlocutorsPublicKey(request, authHeader);
    }

    @PostMapping("/get-messages")
    public GetMessagesLastMessageIdResponse getMessages(
            @RequestBody @Valid
            GetMessagesRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return messageService.getMessages(request, authHeader);
    }

    @PostMapping("/send-message")
    public String sendMessage(
            @RequestBody @Valid
            SendMessageRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return messageService.sendMessage(request, authHeader);
    }
}
