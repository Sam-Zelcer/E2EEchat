package sam.dev.E2EEchat.controller.pgp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sam.dev.E2EEchat.repository.dtos.pgp.privateChat.CreatePrivateChatRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.privateChat.GetPrivateChatsResponse;
import sam.dev.E2EEchat.service.pgp.privateChat.CreatePrivateChatService;
import sam.dev.E2EEchat.service.pgp.privateChat.GetPrivateChatsService;

import java.util.List;

@RestController
@RequestMapping("/private-chat")
@RequiredArgsConstructor
public class PrivateChatController {

    private final CreatePrivateChatService createPrivateChatService;
    private final GetPrivateChatsService getPrivateChatsService;

    @PostMapping("/create")
    public String createPrivateChat(
            @RequestBody @Valid
            CreatePrivateChatRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return createPrivateChatService.createPrivateChat(request, authHeader);
    }

    @GetMapping("/get")
    public List<GetPrivateChatsResponse> getUsersChats(
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return getPrivateChatsService.getChats(authHeader);
    }
}
