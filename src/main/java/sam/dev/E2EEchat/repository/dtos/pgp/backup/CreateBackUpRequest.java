package sam.dev.E2EEchat.repository.dtos.pgp.backup;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateBackUpRequest {

    @NotBlank
    private List<String> encryptedPrivateKeys;
}
