package sam.dev.E2EEchat.controller.pgp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessagesLastMessageIdResponse;
import sam.dev.E2EEchat.repository.dtos.pgp.message.GetMessagesRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.message.SendMessageRequest;
import sam.dev.E2EEchat.service.pgp.message.GetMessagesService;
import sam.dev.E2EEchat.service.pgp.message.SendMessageService;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {

    private final SendMessageService sendMessageService;
    private final GetMessagesService getMessagesService;

    @PostMapping("/get")
    public GetMessagesLastMessageIdResponse getMessages(
            @RequestBody @Valid
            GetMessagesRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return getMessagesService.getMessages(request, authHeader);
    }

    @PostMapping("/send")
    public String sendMessage(
            @RequestBody @Valid
            SendMessageRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return sendMessageService.sendMessage(request, authHeader);
    }
}
