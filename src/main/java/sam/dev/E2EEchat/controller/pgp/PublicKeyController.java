package sam.dev.E2EEchat.controller.pgp;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sam.dev.E2EEchat.repository.dtos.pgp.publicKey.GetInterlocutorsPublicKeyRequest;
import sam.dev.E2EEchat.repository.dtos.pgp.publicKey.SavePublicKeyRequest;
import sam.dev.E2EEchat.service.pgp.publicKey.GetInterlocutorsPublicKeyService;
import sam.dev.E2EEchat.service.pgp.publicKey.SavePublicKeyService;

@RestController
@RequestMapping("/public-key")
@RequiredArgsConstructor
public class PublicKeyController {

    private final SavePublicKeyService savePublicKeyService;
    private final GetInterlocutorsPublicKeyService getInterlocutorsPublicKeyService;

    @PostMapping("/save-public-key")
    public String savePublicKey(
            @RequestBody @Valid
            SavePublicKeyRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return savePublicKeyService.savePublicKey(request, authHeader);
    }

    @PostMapping("/get-interlocutors-public-key")
    public String getInterlocutorsPublicKey(
            @RequestBody @Valid
            GetInterlocutorsPublicKeyRequest request,
            @RequestHeader("Authorization")
            String authHeader
    ) {
        return getInterlocutorsPublicKeyService.getInterlocutorsPublicKey(request, authHeader);
    }
}
