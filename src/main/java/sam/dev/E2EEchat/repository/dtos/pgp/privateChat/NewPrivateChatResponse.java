package sam.dev.E2EEchat.repository.dtos.pgp.privateChat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewPrivateChatResponse {

    private Long ChatId;
    private String name;
}
