package sam.dev.E2EEchat.repository.dtos.pgp.publicKey;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetInterlocutorsPublicKeyRequest {

    @NotNull
    private Long chatId;
}
