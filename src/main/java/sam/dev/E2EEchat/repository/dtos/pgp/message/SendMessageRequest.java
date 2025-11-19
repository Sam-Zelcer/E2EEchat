package sam.dev.E2EEchat.repository.dtos.pgp.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotNull
    private Long chatId;

    @NotBlank
    private String encryptedMessage;
    @NotBlank
    private String encryptedSessionKey;
}
