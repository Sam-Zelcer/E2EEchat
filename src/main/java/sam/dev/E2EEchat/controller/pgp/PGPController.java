package sam.dev.E2EEchat.controller.pgp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sam.dev.E2EEchat.repository.dtos.pgp.publicKey.CreatePrivateChatRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.publicKey.SavePublicKeyRequest;
import sam.dev.E2EEchat.service.pgp.PublicKeyService;
import sam.dev.E2EEchat.service.pgp.PrivateChatService;

@RestController
@RequestMapping("/pgp")
@RequiredArgsConstructor
public class PGPController {

    private final PublicKeyService publicKeyService;
    private final PrivateChatService privateChatService;

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
            @RequestParam("Authorization")
            String authHeader
    ) {
        return privateChatService.createPrivateChat(request, authHeader);
    }

}
