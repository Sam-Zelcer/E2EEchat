package sam.dev.E2EEchat.repository.dtos.pgp.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetMessagesRequest {

    @NotNull
    private Long chatId;

    @NotNull
    private Long lastMessageId;
}
