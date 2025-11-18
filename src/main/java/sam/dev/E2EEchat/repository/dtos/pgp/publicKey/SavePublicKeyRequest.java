package sam.dev.E2EEchat.repository.dtos.pgp.publicKey;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SavePublicKeyRequest {

    @NotBlank
    private String publicKey;
}
